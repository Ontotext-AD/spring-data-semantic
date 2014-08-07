package org.springframework.data.semantic.support;

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
import org.springframework.data.semantic.convert.SemanticEntityConverter;
import org.springframework.data.semantic.convert.SemanticEntityInstantiator;
import org.springframework.data.semantic.convert.SemanticEntityPersister;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.support.convert.SemanticEntityConverterImpl;
import org.springframework.data.semantic.support.convert.SemanticEntityInstantiatorImpl;
import org.springframework.data.semantic.support.convert.SemanticEntityPersisterImpl;
import org.springframework.data.semantic.support.convert.SemanticSourceStateTransmitter;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.access.listener.DelegatingFieldAccessListenerFactory;
import org.springframework.data.semantic.support.convert.state.SemanticEntityStateFactory;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class SemanticTemplateCRUD implements SemanticOperationsCRUD, InitializingBean, ApplicationContextAware {
	//private static final Logger LOGGER = LoggerFactory.getLogger(SemanticTemplate.class);
	
	private ApplicationContext applicationContext;
		
	private SemanticDatabase semanticDB;
	
	private SemanticMappingContext mappingContext;
	
	private SemanticTemplateStatementsCollector statementsCollector;
	private SemanticEntityPersister entityPersister;
	
	private SemanticEntityInstantiator entityInstantiator;
	private DelegatingFieldAccessorFactory delegatingFieldAxsorFactory;
	private DelegatingFieldAccessListenerFactory delegatingFieldAccessListenerFactory;
	private SemanticEntityStateFactory sesFactory;
	private SemanticSourceStateTransmitter sourceStateTransmitter;
	private SemanticEntityConverter entityConverter;
	
	private Logger logger = LoggerFactory.getLogger(SemanticTemplateCRUD.class);
	
	public SemanticTemplateCRUD(SemanticDatabase semanticDB, ConversionService conversionService){
		this.semanticDB = semanticDB;
		
		try {
			this.mappingContext = new SemanticMappingContext(semanticDB.getNamespaces(), this.semanticDB.getDefaultNamespace());
			this.entityInstantiator = new SemanticEntityInstantiatorImpl();
			this.statementsCollector = new SemanticTemplateStatementsCollector(semanticDB, conversionService, this.mappingContext);
			this.delegatingFieldAxsorFactory = new DelegatingFieldAccessorFactory(this.statementsCollector, this);
			this.delegatingFieldAccessListenerFactory = new DelegatingFieldAccessListenerFactory(this.statementsCollector, this);
			this.sesFactory = new SemanticEntityStateFactory(this.mappingContext, this.delegatingFieldAxsorFactory, this.delegatingFieldAccessListenerFactory, semanticDB);
			this.sourceStateTransmitter = new SemanticSourceStateTransmitter(this.sesFactory);
			this.entityConverter = new SemanticEntityConverterImpl(this.mappingContext, conversionService, this.entityInstantiator, this.sourceStateTransmitter);
			this.entityPersister = new SemanticEntityPersisterImpl(this.entityConverter);
		} catch (RepositoryException e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
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
		// TODO Auto-generated method stub
		
	}
	
	
	
	@Override
	public <T> T save(T entity) {
		@SuppressWarnings("unchecked")
		SemanticPersistentEntity<T> persistentEntity = (SemanticPersistentEntity<T>) this.mappingContext.getPersistentEntity(entity.getClass());
		Model dbState = this.statementsCollector.getStatementsForResourceClass(persistentEntity.getResourceId(entity), entity.getClass());
		return this.entityPersister.persistEntity(entity, new RDFState(dbState));
	}

	@Override
	public void delete(Object entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T find(URI resourceId, Class<? extends T> clazz) {
		try{
			return createEntity(this.statementsCollector.getStatementsForResourceClass(resourceId, clazz), clazz);
		} catch (DataAccessException e){
			logger.debug(e.getMessage(), e);
		}
		return null;
	}

	
	public <T> T createEntity(Model statements, Class<T> clazz) {
		return entityPersister.createEntityFromState(new RDFState(statements), clazz);
    }
	
}
