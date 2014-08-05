package org.springframework.data.semantic.support.convert;

import org.openrdf.model.URI;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.semantic.convert.SemanticEntityConverter;
import org.springframework.data.semantic.convert.SemanticEntityInstantiator;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.EntityToStatementsConverter;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.semantic.support.mapping.SemanticPersistentEntityImpl;

/**
 * Handles the logic for converting Statements to Entities
 * This service should not be used directly, but rather via a caching abstraction (SemanticEntityPersister)
 */
public class SemanticEntityConverterImpl implements SemanticEntityConverter {

	private final SemanticMappingContext mappingContext;
	private final ConversionService conversionService;
	private final SemanticEntityInstantiator entityInstantiator;
	private final SemanticSourceStateTransmitter sourceStateTransmitter;
	private final EntityToStatementsConverter toStatementsConverter = new EntityToStatementsConverter();
	
	
	
	public SemanticEntityConverterImpl(SemanticMappingContext mappingContext, ConversionService conversionService, SemanticEntityInstantiator entityInstantiator, SemanticSourceStateTransmitter sourceStateTransmitter){
		this.mappingContext = mappingContext;
		this.conversionService = conversionService;
		this.entityInstantiator = entityInstantiator;
		this.sourceStateTransmitter = sourceStateTransmitter;
	}

	@Override
	public MappingContext<? extends SemanticPersistentEntity<?>, SemanticPersistentProperty> getMappingContext() {
		return mappingContext;
	}

	@Override
	public ConversionService getConversionService() {
		return conversionService;
	}

	@Override
	public <R> R read(Class<R> type, RDFState source) {
		
		@SuppressWarnings("unchecked")
		final SemanticPersistentEntityImpl<R> persistentEntity = (SemanticPersistentEntityImpl<R>) mappingContext.getPersistentEntity(type);
		R dao = entityInstantiator.createInstanceFromState(persistentEntity, source);
		loadEntity(dao, source, persistentEntity.getMappingPolicy(), persistentEntity);
		return dao;
	}

	@Override
	public void write(Object source, RDFState sink) {
		final SemanticPersistentEntityImpl<?> persistentEntity = mappingContext.getPersistentEntity(source.getClass());
		final URI resourceId = persistentEntity.getResourceId(source);
		
		final BeanWrapper<SemanticPersistentEntity<Object>, Object> wrapper = BeanWrapper.<SemanticPersistentEntity<Object>, Object>create(source, conversionService);
        if (sink == null) {
        	sink = toStatementsConverter.convertEntityToStatements(resourceId, persistentEntity, source);
        }
        sourceStateTransmitter.copyPropertiesTo(wrapper, sink);

	}

	@Override
	public <R> R loadEntity(R entity, RDFState source,
			MappingPolicy mappingPolicy,
			SemanticPersistentEntity<R> persistentEntity) {
		if (mappingPolicy.eagerLoad()) {
            final BeanWrapper<SemanticPersistentEntity<R>, R> wrapper = 
            		BeanWrapper.<SemanticPersistentEntity<R>, R>create(entity, conversionService);
            
            sourceStateTransmitter.copyPropertiesFrom(wrapper, source, persistentEntity, mappingPolicy);
            
            cascadeFetch(persistentEntity, wrapper, mappingPolicy);
        }
        return entity;
	}
	
	private <R> void cascadeFetch(SemanticPersistentEntity<R> persistentEntity, final BeanWrapper<SemanticPersistentEntity<R>, R> wrapper, final MappingPolicy policy) {
		//TODO
	}

}
