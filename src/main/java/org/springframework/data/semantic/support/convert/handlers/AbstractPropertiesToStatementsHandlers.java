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
package org.springframework.data.semantic.support.convert.handlers;

import java.util.Arrays;
import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.semantic.convert.ObjectToLiteralConverter;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.Cascade;
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
		this.objectToLiteralConverter = ObjectToLiteralConverter.getInstance();
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
					}
					if(values.isEmpty() && !persistentProperty.isOptional() && !allowEmpty()){
						throw new RequiredPropertyException(persistentEntity.getIdProperty().getValue(entity, persistentEntity.getMappingPolicy()), persistentProperty);
					}
					for(Object val : values){
						processPropertyStatement(persistentProperty, val);
					}
				}
				else{
					processPropertyStatement(persistentProperty, value);
				}
			}
			else{
				if(!persistentProperty.isOptional() && !allowEmpty()){
					throw new RequiredPropertyException(persistentEntity.getIdProperty().getValue(entity, persistentEntity.getMappingPolicy()), persistentProperty);
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
			if(!persistentProperty.isOptional() && !allowEmpty()){
				throw new RequiredPropertyException(persistentEntity.getIdProperty().getValue(entity, persistentEntity.getMappingPolicy()), persistentProperty);
			}
		}
		else{
			if(persistentProperty.isCollectionLike()){
				SemanticPersistentEntity<Object> associatedEntity = (SemanticPersistentEntity<Object>) mappingContext.getPersistentEntity(persistentProperty.getComponentType());
				Collection<Object> associatedEntityInstances;
				if(persistentProperty.isArray()){
					associatedEntityInstances = Arrays.asList((Object[]) value);
				}
				else{
					associatedEntityInstances = (Collection<Object>) value;
				}
				if(associatedEntityInstances.isEmpty() && !persistentProperty.isOptional() && !allowEmpty()){
					throw new RequiredPropertyException(persistentEntity.getIdProperty().getValue(entity, persistentEntity.getMappingPolicy()), persistentProperty);
				}
				for(Object associatedEntityInstance : associatedEntityInstances){
					URI associatedResourceId = associatedEntity.getResourceId(associatedEntityInstance);
					processAssociationStatement(persistentProperty, associatedResourceId);
					if(persistentProperty.getMappingPolicy().shouldCascade(Cascade.SAVE) && !statements.getCurrentStatements().subjects().contains(associatedResourceId)){
						AbstractPropertiesToStatementsHandlers associationHandler = getInstance(statements, associatedEntityInstance, mappingContext);
						associatedEntity.doWithProperties(associationHandler);
						associatedEntity.doWithAssociations(associationHandler);
					}
				}
			}
			else{
				SemanticPersistentEntity<Object> associatedEntity = (SemanticPersistentEntity<Object>) mappingContext.getPersistentEntity(persistentProperty.getType());
				URI associatedResourceId = associatedEntity.getResourceId(value);
				processAssociationStatement(persistentProperty, associatedResourceId);
				if(persistentProperty.getMappingPolicy().shouldCascade(Cascade.SAVE) && !statements.getCurrentStatements().subjects().contains(associatedResourceId)){
					AbstractPropertiesToStatementsHandlers associationHandler = getInstance(statements, value, mappingContext);
					associatedEntity.doWithProperties(associationHandler);
					associatedEntity.doWithAssociations(associationHandler);
				}
			}
		}
	}
	
	protected abstract AbstractPropertiesToStatementsHandlers getInstance(RDFState statements, Object entity, SemanticMappingContext mappingContext);
	
	protected abstract void processPropertyStatement(SemanticPersistentProperty property, Object value);
	
	protected abstract void processAssociationStatement(SemanticPersistentProperty property, Resource value);
	
}
