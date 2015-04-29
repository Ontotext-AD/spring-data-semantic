/**
 * Copyright (C) 2014 Ontotext AD (info@ontotext.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.semantic.support;

import net.sf.ehcache.CacheManager;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.semantic.cache.EntityCache;
import org.springframework.data.semantic.convert.SemanticEntityConverter;
import org.springframework.data.semantic.convert.SemanticEntityInstantiator;
import org.springframework.data.semantic.convert.SemanticEntityPersister;
import org.springframework.data.semantic.convert.SemanticEntityRemover;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.filter.ValueFilter;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.support.cache.EhCacheEntityCache;
import org.springframework.data.semantic.support.cache.EmptyEntityCache;
import org.springframework.data.semantic.support.convert.*;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.access.listener.DelegatingFieldAccessListenerFactory;
import org.springframework.data.semantic.support.convert.state.SemanticEntityStateFactory;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

import java.util.*;

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
	
	private final boolean explicitSupertypes;
	private volatile boolean isInitialized = false;
	private final Object initLockObject = new Object();
	
	private Logger logger = LoggerFactory.getLogger(SemanticTemplateCRUD.class);
	
	public SemanticTemplateCRUD(SemanticDatabase semanticDB, ConversionService conversionService, boolean explicitSupertypes){
		this.semanticDB = semanticDB;
		this.conversionService = conversionService;
		this.explicitSupertypes = explicitSupertypes;
	}
	
	public void changeDatabase(SemanticDatabase semanticDB){
		this.semanticDB = semanticDB;
		isInitialized = false;
	}

	private void lazyInit() {
		if (!isInitialized) {
			synchronized (initLockObject) {
				if (!isInitialized) {
					init();
					isInitialized = true;
				}
			}
		}
	}

	private void init(){
		if(this.semanticDB != null && this.conversionService != null){
			try {
				this.entityInstantiator = new SemanticEntityInstantiatorImpl();
				this.mappingContext = new SemanticMappingContext(semanticDB.getNamespaces(), this.semanticDB.getDefaultNamespace(), this.explicitSupertypes);
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
	public <T> Iterable<T> create(Iterable<T> entities) {
		lazyInit();
		Map<T, RDFState> entityToExistingState = new HashMap<T, RDFState>();
		for(T entity : entities){
			entityToExistingState.put(entity, new RDFState());
		}
		return this.entityPersister.persistEntities(entityToExistingState);
	}

	@Override
	public <T> T create(T entity) {
		lazyInit();
		entity = this.entityPersister.persistEntity(entity, new RDFState());
		entityCache.put(entity);
		return entity;
	}
	
	
	@Override
	public <T> T save(T entity) {
		lazyInit();
		@SuppressWarnings("unchecked")
		SemanticPersistentEntity<T> persistentEntity = (SemanticPersistentEntity<T>) this.mappingContext.getPersistentEntity(entity.getClass());
		URI id = persistentEntity.getResourceId(entity);
		Model dbState = this.statementsCollector.getStatementsForResourceOriginalPredicates(id, entity.getClass(), MappingPolicyImpl.DEFAULT_POLICY, null);
		entity = this.entityPersister.persistEntity(entity, new RDFState(dbState));
		entityCache.put(entity);
		return entity;
	}
	
	@Override
	public <T> Iterable<T> save(Iterable<T> entities) {
		lazyInit();
		Map<T, RDFState> entityToExistingState = new HashMap<T, RDFState>();
		for(T entity : entities){
			@SuppressWarnings("unchecked")
			SemanticPersistentEntity<T> persistentEntity = (SemanticPersistentEntity<T>) this.mappingContext.getPersistentEntity(entity.getClass());
			URI id = persistentEntity.getResourceId(entity);
			Model dbState = this.statementsCollector.getStatementsForResourceOriginalPredicates(id, entity.getClass(), MappingPolicyImpl.DEFAULT_POLICY, null);
			entityToExistingState.put(entity, new RDFState(dbState));
		}
		return this.entityPersister.persistEntities(entityToExistingState);
	}
	
	@Override
	public <T> List<T> findAll(Class<? extends T> clazz) {
		lazyInit();
		Collection<Model> statementsPerEntity = this.statementsCollector.getStatementsForResources(clazz);
		List<T> results = new LinkedList<T>();
		for(Model statements : statementsPerEntity){
			results.add(createEntity(statements, clazz));
		}
		return results;
	}

	@Override
	public <T> T find(URI resourceId, Class<? extends T> clazz) {
		return find(resourceId, clazz, null);
	}

	@Override
	public <T> T find(URI resourceId, Class<? extends T> clazz, ValueFilter valueFilter) {
		lazyInit();
		T entity = entityCache.get(resourceId, clazz);
		if(entity == null){
			try{
				Model model = this.statementsCollector.getStatementsForResource(resourceId, clazz, MappingPolicyImpl.ALL_POLICY, valueFilter);
				if(valueFilter!=null) {
					model = getSortedModel(model, valueFilter);
				}
				entity = createEntity(model, clazz);
				entityCache.put(entity);
			} catch (DataAccessException e){
				logger.error(e.getMessage(), e);
			}
		}
		return entity;
	}

	private Model getSortedModel(Model model, final ValueFilter valueFilter) {
		Model m = new LinkedHashModel();
		m.getNamespaces().addAll(model.getNamespaces());

		List<Statement> stlist = new ArrayList<Statement>(model);

		Collections.sort(stlist, new Comparator<Statement>() {
			@Override
			public int compare(Statement o1, Statement o2) {
				return valueFilter.compare(o1,o2);
			}
		});

		m.addAll(stlist);

		return m;
	}

	
	public <T> T createEntity(Model statements, Class<T> clazz) {
		lazyInit();
		return entityPersister.createEntityFromState(new RDFState(statements), clazz);
    }

	@Override
	public <T> long count(Class<T> clazz) {
		lazyInit();
		try {
			return this.statementsCollector.getCountForResource(clazz);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public <T> boolean exists(URI resourceId, Class<? extends T> clazz) {
		lazyInit();
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
		lazyInit();
		T entity = this.find(resourceId, clazz);
		this.delete(entity);
		
	}

	@Override
	public <T> void delete(T entity) {
		lazyInit();
		@SuppressWarnings("unchecked")
		SemanticPersistentEntity<T> persistentEntity = (SemanticPersistentEntity<T>) this.mappingContext.getPersistentEntity(entity.getClass());
		entityCache.remove(entity);
		this.entityRemover.delete(persistentEntity, entity);
		
	}

	@Override
	public <T> void deleteAll(Class<? extends T> clazz) {
		lazyInit();
		@SuppressWarnings("unchecked")
		SemanticPersistentEntity<T> persistentEntity = (SemanticPersistentEntity<T>) this.mappingContext.getPersistentEntity(clazz);
		entityCache.clear(clazz);
		this.entityRemover.deleteAll(persistentEntity);
	}

	@Override
	public <T> Collection<T> findByProperty(Class<? extends T> clazz,
			Map<String, Object> parameterToValue) {
		lazyInit();
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
		lazyInit();
		return this.statementsCollector.getCountForResourceAndProperties(clazz, parameterToValue);
	}
	
	@Override
	public SemanticMappingContext getSemanticMappingContext() {
		lazyInit();
		return this.mappingContext;
	}

	public SemanticDatabase getSemanticDB() {
		return semanticDB;
	}

	@Override
	public <T> List<T> findAll(Class<? extends T> clazz, Pageable pageRequest) {
		lazyInit();
		Collection<URI> ids = this.statementsCollector.getUrisForOffsetAndLimit(clazz, pageRequest.getOffset(), pageRequest.getPageSize());
		List<T> entities = new ArrayList<T>(pageRequest.getPageSize());
		for(URI id : ids){
			T entity = this.find(id, clazz);
			entities.add(entity);
		}
		return entities;
	}
	
	

	


	
	
}
