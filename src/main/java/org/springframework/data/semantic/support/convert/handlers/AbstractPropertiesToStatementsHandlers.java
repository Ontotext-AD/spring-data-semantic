package org.springframework.data.semantic.support.convert.handlers;

import java.util.Arrays;
import java.util.Collection;

import org.openrdf.model.URI;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.semantic.convert.ObjectToLiteralConverter;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.exceptions.RequiredPropertyException;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public abstract class AbstractPropertiesToStatementsHandlers implements PropertyHandler<SemanticPersistentProperty>, AssociationHandler<SemanticPersistentProperty>{

	protected RDFState statements;
	protected Object entity;
	protected SemanticMappingContext mappingContext;
	protected ObjectToLiteralConverter objectToLiteralConverter;
	
	
	public AbstractPropertiesToStatementsHandlers(RDFState statements,
			Object entity,
			SemanticMappingContext mappingContext) {
		this.statements = statements;
		this.entity = entity;
		this.mappingContext = mappingContext;
		this.objectToLiteralConverter = new ObjectToLiteralConverter();
	}
	
	protected abstract boolean allowEmpty();

	@SuppressWarnings("unchecked")
	@Override
	public void doWithPersistentProperty(SemanticPersistentProperty persistentProperty) {
		SemanticPersistentEntity<?> persistentEntity = (SemanticPersistentEntity<?>) persistentProperty.getOwner();
		Object value = persistentProperty.getValue(entity, persistentEntity.getMappingPolicy());
		if(persistentProperty.shallBePersisted() && !persistentProperty.isContext()){
			if(value != null){
				if(persistentProperty.isCollectionLike()){
					Collection<Object> values;
					if(persistentProperty.isArray()){
						values = Arrays.asList((Object[]) value);
					}
					else{
						values = (Collection<Object>) value;
						if(values.isEmpty() && !persistentProperty.isOptional() && !allowEmpty()){
							throw new RequiredPropertyException(persistentProperty);
						}
						for(Object val : values){
							processStatement(persistentProperty, val);
						}
					}
					
				}
				else{
					processStatement(persistentProperty, value);
				}
			}
			else{
				if(!persistentProperty.isOptional() && !allowEmpty()){
					throw new RequiredPropertyException(persistentProperty);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doWithAssociation(Association<SemanticPersistentProperty> association) {
		SemanticPersistentProperty persistentProperty = association.getInverse();
		SemanticPersistentEntity<?> persistentEntity = (SemanticPersistentEntity<?>) persistentProperty.getOwner();
		Object value = persistentProperty.getValue(entity, persistentEntity.getMappingPolicy());
		if(value == null){
			return;
		}
		if(persistentProperty.isCollectionLike()){
			SemanticPersistentEntity<Object> associatedEntity = (SemanticPersistentEntity<Object>) mappingContext.getPersistentEntity(persistentProperty.getComponentType());
			Collection<Object> associatedEntityInstances;
			if(persistentProperty.isArray()){
				associatedEntityInstances = Arrays.asList((Object[]) value);
			}
			else{
				associatedEntityInstances = (Collection<Object>) value;
			}
			for(Object associatedEntityInstance : associatedEntityInstances){
				URI associatedResourceId = associatedEntity.getResourceId(associatedEntityInstance);
				processStatement(persistentProperty, associatedResourceId);
				if(persistentProperty.getMappingPolicy().eagerLoad() && !statements.getCurrentStatements().subjects().contains(associatedResourceId)){
					AbstractPropertiesToStatementsHandlers associationHandler = getInstance(statements, associatedEntityInstance, mappingContext);
					associatedEntity.doWithProperties(associationHandler);
					associatedEntity.doWithAssociations(associationHandler);
				}
			}
		}
		else{
			SemanticPersistentEntity<Object> associatedEntity = (SemanticPersistentEntity<Object>) mappingContext.getPersistentEntity(persistentProperty.getType());
			URI associatedResourceId = associatedEntity.getResourceId(value);
			processStatement(persistentProperty, associatedResourceId);
			if(persistentProperty.getMappingPolicy().eagerLoad() && !statements.getCurrentStatements().subjects().contains(associatedResourceId)){
				AbstractPropertiesToStatementsHandlers associationHandler = getInstance(statements, value, mappingContext);
				associatedEntity.doWithProperties(associationHandler);
				associatedEntity.doWithAssociations(associationHandler);
			}
		}
	}
	
	protected abstract AbstractPropertiesToStatementsHandlers getInstance(RDFState statements, Object entity, SemanticMappingContext mappingContext);
	
	protected abstract void processStatement(SemanticPersistentProperty property, Object value);
	
}
