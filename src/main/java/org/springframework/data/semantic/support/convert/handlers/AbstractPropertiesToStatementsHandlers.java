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
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public abstract class AbstractPropertiesToStatementsHandlers implements PropertyHandler<SemanticPersistentProperty>, AssociationHandler<SemanticPersistentProperty>{

	protected RDFState statements;
	protected Object entity;
	protected SemanticPersistentEntity<?> persistentEntity;
	protected SemanticMappingContext mappingContext;
	protected ObjectToLiteralConverter objectToLiteralConverter;
	
	
	public AbstractPropertiesToStatementsHandlers(RDFState statements,
			Object entity,
			SemanticPersistentEntity<?> persistentEntity,
			SemanticMappingContext mappingContext) {
		this.statements = statements;
		this.entity = entity;
		this.persistentEntity = persistentEntity;
		this.mappingContext = mappingContext;
		this.objectToLiteralConverter = new ObjectToLiteralConverter();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doWithPersistentProperty(SemanticPersistentProperty persistentProperty) {
		Object value = persistentProperty.getValue(entity, persistentEntity.getMappingPolicy());
		if(persistentProperty.shallBePersisted() && value != null){
			if(persistentProperty.isCollectionLike()){
				if(persistentProperty.isArray()){
					Object[] values = (Object[]) value;
					for(Object val : values){
						processStatement(persistentProperty, val);
					}
				}
				else{
					Iterable<Object> values = (Iterable<Object>) value;
					for(Object val : values){
						processStatement(persistentProperty, val);
					}
				}
				
			}
			else{
				processStatement(persistentProperty, value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doWithAssociation(Association<SemanticPersistentProperty> association) {
		SemanticPersistentProperty persistentProperty = association.getInverse();
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
					associatedEntity.doWithProperties(this);
					associatedEntity.doWithAssociations(this);
				}
			}
		}
		else{
			SemanticPersistentEntity<Object> associatedEntity = (SemanticPersistentEntity<Object>) mappingContext.getPersistentEntity(persistentProperty.getType());
			URI associatedResourceId = associatedEntity.getResourceId(value);
			processStatement(persistentProperty, associatedResourceId);
			if(persistentProperty.getMappingPolicy().eagerLoad() && !statements.getCurrentStatements().subjects().contains(associatedResourceId)){
				associatedEntity.doWithProperties(this);
				associatedEntity.doWithAssociations(this);
			}
		}
	}
	
	protected abstract void processStatement(SemanticPersistentProperty property, Object value);
	
}
