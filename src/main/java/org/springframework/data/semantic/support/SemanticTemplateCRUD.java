package org.springframework.data.semantic.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheManager;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.semantic.cache.EntityCache;
import org.springframework.data.semantic.convert.SemanticEntityConverter;
import org.springframework.data.semantic.convert.SemanticEntityInstantiator;
import org.springframework.data.semantic.convert.SemanticEntityPersister;
import org.springframework.data.semantic.convert.SemanticEntityRemover;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.support.cache.EhCacheEntityCache;
import org.springframework.data.semantic.support.cache.EmptyEntityCache;
import org.springframework.data.semantic.support.convert.EntityToQueryConverter;
import org.springframework.data.semantic.support.convert.EntityToStatementsConverter;
import org.springframework.data.semantic.support.convert.SemanticEntityConverterImpl;
import org.springframework.data.semantic.support.convert.SemanticEntityInstantiatorImpl;
import org.springframework.data.semantic.support.convert.SemanticEntityPersisterImpl;
import org.springframework.data.semantic.support.convert.SemanticEntityRemoverImpl;
import org.springframework.data.semantic.support.convert.SemanticSourceStateTransmitter;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.access.listener.DelegatingFieldAccessListenerFactory;
import org.springframework.data.semantic.support.convert.state.SemanticEntityStateFactory;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class SemanticTemplateCRUD implements SemanticOperationsCRUD, InitializingBean, ApplicationContextAware {
	//private static final Logger LOGGER = LoggerFactory.getLogger(SemanticTemplate.class);
	
	private ApplicationContext applicationContext;
		
	private SemanticDatabase semanticDB;
	
	private ConversionService conversionService;
	
	private SemanticMappingContext mappingContext;
	
	private SemanticTemplateStatementsCollector statementsCollector;
	private SemanticEntityPersister entityPersister;
	private SemanticEntityRemover entityRemover;
	
	private SemanticEntityInstantiator entityInstantiator;
	private DelegatingFieldAccessorFactory delegatingFieldAxsorFactory;
	private DelegatingFieldAccessListenerFactory delegatingFieldAccessListenerFactory;
	private SemanticEntityStateFactory sesFactory;
	private SemanticSourceStateTransmitter sourceStateTransmitter;
	private SemanticEntityConverter entityConverter;
	private EntityToQueryConverter entityToQueryConverter;
	private EntityToStatementsConverter entityToStatementsConverter;
	
	private EntityCache entityCache;
	
	private Logger logger = LoggerFactory.getLogger(SemanticTemplateCRUD.class);
	
	public SemanticTemplateCRUD(SemanticDatabase semanticDB, ConversionService conversionService){
		this.semanticDB = semanticDB;
		this.conversionService = conversionService;
		init();
	}
	
	public void changeDatabase(SemanticDatabase semanticDB){
		this.semanticDB = semanticDB;
		init();
	}
	
	private void init(){
		if(this.semanticDB != null && this.conversionService != null){
			try {
				this.entityInstantiator = new SemanticEntityInstantiatorImpl();
				this.mappingContext = new SemanticMappingContext(semanticDB.getNamespaces(), this.semanticDB.getDefaultNamespace());
				this.entityToQueryConverter = new EntityToQueryConverter(this.mappingContext);
				this.entityToStatementsConverter = new EntityToStatementsConverter(mappingContext);
				this.statementsCollector = new SemanticTemplateStatementsCollector(this.semanticDB, this.mappingContext, this.entityToQueryConverter);
				this.delegatingFieldAxsorFactory = new DelegatingFieldAccessorFactory(this.statementsCollector, this);
				this.delegatingFieldAccessListenerFactory = new DelegatingFieldAccessListenerFactory(this.statementsCollector, this);
				this.sesFactory = new SemanticEntityStateFactory(this.mappingContext, this.delegatingFieldAxsorFactory, this.delegatingFieldAccessListenerFactory, this.semanticDB, this.conversionService);
				this.sourceStateTransmitter = new SemanticSourceStateTransmitter(this.sesFactory);
				this.entityConverter = new SemanticEntityConverterImpl(this.mappingContext, this.conversionService, this.entityInstantiator, this.sourceStateTransmitter, this.entityToStatementsConverter, this.semanticDB);
				this.entityPersister = new SemanticEntityPersisterImpl(this.entityConverter);
				this.entityRemover = new SemanticEntityRemoverImpl(this.semanticDB, this.entityToStatementsConverter, this.mappingContext);
				if(this.entityCache != null){
					this.entityCache.clearAll();
					if(applicationContext.getBeanNamesForType(CacheManager.class).length != 0){
						this.entityCache = new EhCacheEntityCache(this.mappingContext, applicationContext.getBean(CacheManager.class));
					}
				}
				
			} catch (RepositoryException e) {
				throw ExceptionTranslator.translateExceptionIfPossible(e);
			}
		}
		else{
			logger.warn("The SemanticTemplateCRUD is not initialized due to no semantic database or conversion service provided.");
		}
	}
	
	/*private SemanticPersistentEntity<?> getPersistentEntity(Class<?> targetClazz){
		return (SemanticPersistentEntityImpl<?>) mappingContext.getPersistentEntity(targetClazz);
	}*/
	
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void afterPropertiesSet() throws Exception {
		if(applicationContext.getBeanNamesForType(CacheManager.class).length != 0 && this.mappingContext != null){
			this.entityCache = new EhCacheEntityCache(this.mappingContext, applicationContext.getBean(CacheManager.class));
			logger.info("Using EhcacheEntityCache for second level caching.");
		}
		else{
			logger.info("EntityCache is not configured. No caching will be applied.");
			this.entityCache = new EmptyEntityCache();
		}
	}
	
	
	
	@Override
	public <T> T save(T entity) {
		@SuppressWarnings("unchecked")
		SemanticPersistentEntity<T> persistentEntity = (SemanticPersistentEntity<T>) this.mappingContext.getPersistentEntity(entity.getClass());
		URI id = persistentEntity.getResourceId(entity);
		Model dbState = this.statementsCollector.getStatementsForResourceOriginalPredicates(id, entity.getClass(), MappingPolicyImpl.DEFAULT_POLICY);
		entity = this.entityPersister.persistEntity(entity, new RDFState(dbState));
		entityCache.put(entity);
		return entity;
	}
	
	@Override
	public <T> Iterable<T> save(Iterable<T> entities) {
		Map<T, RDFState> entityToExistingState = new HashMap<T, RDFState>();
		for(T entity : entities){
			@SuppressWarnings("unchecked")
			SemanticPersistentEntity<T> persistentEntity = (SemanticPersistentEntity<T>) this.mappingContext.getPersistentEntity(entity.getClass());
			URI id = persistentEntity.getResourceId(entity);
			Model dbState = this.statementsCollector.getStatementsForResourceOriginalPredicates(id, entity.getClass(), MappingPolicyImpl.DEFAULT_POLICY);
			entityToExistingState.put(entity, new RDFState(dbState));
		}
		return this.entityPersister.persistEntities(entityToExistingState);
	}
	
	@Override
	public <T> List<T> findAll(Class<? extends T> clazz) {
		Collection<Model> statementsPerEntity = this.statementsCollector.getStatementsForResources(clazz);
		List<T> results = new LinkedList<T>();
		for(Model statements : statementsPerEntity){
			results.add(createEntity(statements, clazz));
		}
		return results;
	}

	@Override
	public <T> T find(URI resourceId, Class<? extends T> clazz) {
		T entity = entityCache.get(resourceId, clazz);
		if(entity == null){
			try{
				entity = createEntity(this.statementsCollector.getStatementsForResource(resourceId, clazz, MappingPolicyImpl.ALL_POLICY), clazz);
				entityCache.put(entity);
			} catch (DataAccessException e){
				logger.error(e.getMessage(), e);
			}
		}
		return entity;
	}

	
	public <T> T createEntity(Model statements, Class<T> clazz) {
		return entityPersister.createEntityFromState(new RDFState(statements), clazz);
    }

	@Override
	public <T> long count(Class<T> clazz) {
		try {
			return this.statementsCollector.getCountForResource(clazz);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public <T> boolean exists(URI resourceId, Class<? extends T> clazz) {
		T entity = entityCache.get(resourceId, clazz);
		if(entity != null){
			return true;
		}
		try {
			return this.semanticDB.getBooleanQueryResult(entityToQueryConverter.getQueryForResourceExistence(resourceId, this.mappingContext.getPersistentEntity(clazz)));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public <T> void delete(URI resourceId, Class<? extends T> clazz) {
		T entity = this.find(resourceId, clazz);
		this.delete(entity);
		
	}

	@Override
	public <T> void delete(T entity) {
		@SuppressWarnings("unchecked")
		SemanticPersistentEntity<T> persistentEntity = (SemanticPersistentEntity<T>) this.mappingContext.getPersistentEntity(entity.getClass());
		entityCache.remove(entity);
		this.entityRemover.delete(persistentEntity, entity);
		
	}

	@Override
	public <T> void deleteAll(Class<? extends T> clazz) {
		@SuppressWarnings("unchecked")
		SemanticPersistentEntity<T> persistentEntity = (SemanticPersistentEntity<T>) this.mappingContext.getPersistentEntity(clazz);
		entityCache.clear(clazz);
		this.entityRemover.deleteAll(persistentEntity);
	}

	@Override
	public <T> Collection<T> findByProperty(Class<? extends T> clazz,
			Map<String, Object> parameterToValue) {
		Collection<Model> statementsPerEntity = this.statementsCollector.getStatementsForResourcesAndProperties(clazz, parameterToValue, null, null);
		List<T> results = new LinkedList<T>();
		for(Model statements : statementsPerEntity){
			T entity = createEntity(statements, clazz);
			//TODO set required values
			results.add(entity);
		}
		return results;
	}

	@Override
	public Long countByProperty(Class<?> clazz,
			Map<String, Object> parameterToValue) {
		return this.statementsCollector.getCountForResourceAndProperties(clazz, parameterToValue);
	}
	
	@Override
	public SemanticMappingContext getSemanticMappingContext() {
		return this.mappingContext;
	}

	public SemanticDatabase getSemanticDB() {
		return semanticDB;
	}
	
	

	


	
	
}
