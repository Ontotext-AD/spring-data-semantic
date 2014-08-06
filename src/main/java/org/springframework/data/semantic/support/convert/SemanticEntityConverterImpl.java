package org.springframework.data.semantic.support.convert;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
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
		
		final BeanWrapper<Object> wrapper = BeanWrapper.<Object>create(source, conversionService);
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
            final BeanWrapper<R> wrapper = 
            		BeanWrapper.<R>create(entity, conversionService);
            
            sourceStateTransmitter.copyPropertiesFrom(wrapper, source, persistentEntity, mappingPolicy);
            
            cascadeFetch(persistentEntity, wrapper, source, mappingPolicy);
        }
        return entity;
	}
	
	private <R> void cascadeFetch(SemanticPersistentEntity<R> persistentEntity, final BeanWrapper<R> wrapper, final RDFState source, final MappingPolicy policy) {
		persistentEntity.doWithAssociations(new AssociationHandler<SemanticPersistentProperty>() {
            @Override
            public void doWithAssociation(Association<SemanticPersistentProperty> association) {
                final SemanticPersistentProperty property = association.getInverse();
                // MappingPolicy mappingPolicy = policy.combineWith(property.getMappingPolicy());
                final MappingPolicy mappingPolicy = property.getMappingPolicy();
                if (mappingPolicy.eagerLoad()) {
                   //TODO
                }
                else{
                	@SuppressWarnings("unchecked")
					SemanticPersistentEntity<Object> entity = (SemanticPersistentEntity<Object>) mappingContext.getPersistentEntity(property.getTypeInformation().getActualType());
                	Set<Value> associatedEntityIds = source.getCurrentStatements().filter(null, new URIImpl(property.getAliasPredicate()), null).objects();
                	if (property.getTypeInformation().isCollectionLike()) {
                		List<Object> associationValuesList = new LinkedList<Object>();
                		for(Value associatedEntityId : associatedEntityIds){
                    		if(associatedEntityId instanceof URI){
                    			Object associatedEntity = entityInstantiator.createInstance(entity, (URI) associatedEntityId);
                    			associationValuesList.add(associatedEntity);
                    		}
                    	}
                		sourceStateTransmitter.setProperty(wrapper, property, associationValuesList);
                	}
                	else{
                		if(!associatedEntityIds.isEmpty()){
                			Object associatedEntity = entityInstantiator.createInstance(entity, (URI) associatedEntityIds.iterator().next());
                			sourceStateTransmitter.setProperty(wrapper, property, associatedEntity);
                		}
                	}
                }
            }
        });
	}

}
