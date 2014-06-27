package org.springframework.data.semantic.support;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.semantic.convert.SemanticEntityConverter;
import org.springframework.data.semantic.convert.SemanticEntityInstantiator;
import org.springframework.data.semantic.convert.SemanticEntityPersister;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticExceptionTranslator;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.core.StatementsIterator;
import org.springframework.data.semantic.core.UncategorizedSemanticDataAccessException;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.convert.SemanticEntityConverterImpl;
import org.springframework.data.semantic.support.convert.SemanticEntityInstantiatorImpl;
import org.springframework.data.semantic.support.convert.SemanticEntityPersisterImpl;
import org.springframework.data.semantic.support.convert.SemanticSourceStateTransmitter;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.state.SemanticEntityStateFactory;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.semantic.support.mapping.SemanticPersistentEntityImpl;

public class SemanticTemplateCRUD implements SemanticOperationsCRUD, InitializingBean, ApplicationContextAware {
	//private static final Logger LOGGER = LoggerFactory.getLogger(SemanticTemplate.class);
	
	private ApplicationContext applicationContext;
		
	private SemanticDatabase semanticDB;
	
	private SemanticMappingContext mappingContext;
	
	public SemanticTemplateCRUD(SemanticDatabase semanticDB, ConversionService conversionService){
		this.semanticDB = semanticDB;
		
		try {
			mappingContext = new SemanticMappingContext(semanticDB.getNamespaces(), semanticDB.getDefaultNamespace());			
		} catch (RepositoryException e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
		}		
	}		
	
	private SemanticPersistentEntity<?> getPersistentEntity(Class<?> targetClazz){
		return (SemanticPersistentEntityImpl<?>) mappingContext.getPersistentEntity(targetClazz);
	}
	
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	
	@Override
	public <T> T save(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Object entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T load(List<Statement> statements, T target) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
