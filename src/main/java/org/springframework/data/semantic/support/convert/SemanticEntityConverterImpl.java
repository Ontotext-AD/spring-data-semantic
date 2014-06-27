package org.springframework.data.semantic.support.convert;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.semantic.convert.SemanticEntityConverter;
import org.springframework.data.semantic.convert.SemanticEntityInstantiator;
import org.springframework.data.semantic.core.StatementsIterator;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
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
	public <R> R read(Class<R> type, StatementsIterator source) {
		//final TypeInformation<R> requestedTypeInformation = ClassTypeInformation.from(type);
		
		@SuppressWarnings("unchecked")
		final SemanticPersistentEntityImpl<R> persistentEntity = (SemanticPersistentEntityImpl<R>) mappingContext.getPersistentEntity(type);
		R dao = entityInstantiator.createInstanceFromState(persistentEntity, source);
		loadEntity(dao, source, persistentEntity.getMappingPolicy(), persistentEntity);
		return dao;
	}
	
	@Override
	public <R> R read(Class<R> type, StatementsIterator source, MappingPolicy mappingPolicy) {
		//final TypeInformation<R> requestedTypeInformation = ClassTypeInformation.from(type);
		
		@SuppressWarnings("unchecked")
		final SemanticPersistentEntityImpl<R> persistentEntity = (SemanticPersistentEntityImpl<R>) mappingContext.getPersistentEntity(type);
		R dao = entityInstantiator.createInstanceFromState(persistentEntity, source);
		loadEntity(dao, source, mappingPolicy, persistentEntity);
		return dao;
	}

	@Override
	public void write(Object source, StatementsIterator sink) {
		// TODO Auto-generated method stub

	}

	@Override
	public <R> R loadEntity(R entity, StatementsIterator source,
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
