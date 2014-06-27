package org.springframework.data.semantic.support;

import org.openrdf.model.Model;
import org.openrdf.repository.RepositoryException;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.semantic.convert.SemanticEntityConverter;
import org.springframework.data.semantic.convert.SemanticEntityInstantiator;
import org.springframework.data.semantic.convert.SemanticEntityPersister;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticOperationsObjectCreator;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.support.convert.SemanticEntityConverterImpl;
import org.springframework.data.semantic.support.convert.SemanticEntityInstantiatorImpl;
import org.springframework.data.semantic.support.convert.SemanticEntityPersisterImpl;
import org.springframework.data.semantic.support.convert.SemanticSourceStateTransmitter;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.state.SemanticEntityStateFactory;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class SemanticTemplateObjectCreator implements SemanticOperationsObjectCreator {

private SemanticEntityPersister entityPersister;
		
	private SemanticMappingContext mappingContext;
	
	public SemanticTemplateObjectCreator(SemanticDatabase semanticDB, ConversionService conversionService){
		
		try {
			mappingContext = new SemanticMappingContext(semanticDB.getNamespaces(), semanticDB.getDefaultNamespace());
			SemanticEntityInstantiator entityInstantiator = new SemanticEntityInstantiatorImpl();
			SemanticTemplateStatementsCollector statementsCollector = new SemanticTemplateStatementsCollector(semanticDB, conversionService, mappingContext);
			DelegatingFieldAccessorFactory delegatingFieldAxsorFactory = new DelegatingFieldAccessorFactory(statementsCollector, this);
			SemanticEntityStateFactory sesFactory = new SemanticEntityStateFactory(mappingContext, delegatingFieldAxsorFactory);
			SemanticSourceStateTransmitter sourceStateTransmitter = new SemanticSourceStateTransmitter(sesFactory);
			SemanticEntityConverter entityConverter = new SemanticEntityConverterImpl(mappingContext, conversionService, entityInstantiator, sourceStateTransmitter);
			this.entityPersister = new SemanticEntityPersisterImpl(entityConverter);
		} catch (RepositoryException e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
		}		
	}	
	
	@Override
	public <T> T createObjectFromStatements(Model stIterator, 
			Class<T> clazz, MappingPolicy mappingPolicy) {
		return entityPersister.createEntityFromState(stIterator, clazz, mappingPolicy);
	}	
}
