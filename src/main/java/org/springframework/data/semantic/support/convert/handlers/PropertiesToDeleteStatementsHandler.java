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

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.Cascade;
import org.springframework.data.semantic.support.Direction;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.semantic.support.util.ValueUtils;

public class PropertiesToDeleteStatementsHandler extends AbstractPropertiesToStatementsHandlers {

	private URI resourceId;
	
	public PropertiesToDeleteStatementsHandler(RDFState statements, Object entity, SemanticMappingContext mappingContext){
		super(statements, entity, mappingContext);
		@SuppressWarnings("unchecked")
		SemanticPersistentEntity<Object> persistentEntity = (SemanticPersistentEntity<Object>) mappingContext.getPersistentEntity(entity.getClass());
		this.resourceId = persistentEntity.getResourceId(entity);
	}

	@Override
	protected void processPropertyStatement(SemanticPersistentProperty persistentProperty, Object value) {
		SemanticPersistentEntity<?> persistentEntity = (SemanticPersistentEntity<?>) persistentProperty.getOwner();
		if(persistentProperty.isContext()){
			return;
		}
		else if(persistentProperty.isIdProperty()){
			if(persistentEntity.hasContextProperty() && persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy()) != null){
				statements.deleteStatement(new ContextStatementImpl((URI) value, new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), persistentEntity.getRDFType(), (Resource) persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy())));
			}
			else{
				statements.deleteStatement(new StatementImpl(this.resourceId, new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), persistentEntity.getRDFType()));
			}
		}
		else{
			if(persistentEntity.hasContextProperty() && persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy()) != null){
				statements.deleteStatement(new ContextStatementImpl(resourceId, persistentProperty.getPredicate(), objectToLiteralConverter.convert(value), (Resource) persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy())));
			}
			else{
				statements.deleteStatement(new StatementImpl(resourceId, persistentProperty.getPredicate(), objectToLiteralConverter.convert(value)));	
			}
		}
		
	}
	
	@Override
	protected void processAssociationStatement(SemanticPersistentProperty persistentProperty, Resource value) {
		SemanticPersistentEntity<?> persistentEntity = (SemanticPersistentEntity<?>) persistentProperty.getOwner();
		Resource context = persistentEntity.getContext(entity);
		if(Direction.OUTGOING.equals(persistentProperty.getDirection())){
			deleteStatement(resourceId, persistentProperty.getPredicate(), value, context);	
		}
		else if(Direction.INCOMING.equals(persistentProperty.getDirection())){
			SemanticPersistentProperty inverseProperty = persistentProperty.getInverseProperty();
			if(inverseProperty != null){
				deleteStatement(value, inverseProperty.getPredicate(), resourceId, context);
			}
			else{
				deleteStatement(value, persistentProperty.getPredicate(), resourceId, context);
			}
		}
		else{
			deleteStatement(resourceId, persistentProperty.getPredicate(), value, context);
			SemanticPersistentProperty inverseProperty = persistentProperty.getInverseProperty();
			if(inverseProperty != null){
				deleteStatement(value, inverseProperty.getPredicate(), resourceId, context);
			}
			else{
				deleteStatement(value, persistentProperty.getPredicate(), resourceId, context);
			}
		}
		if(persistentProperty.getMappingPolicy().shouldCascade(Cascade.DELETE)){
			cascadeDelete();
		}
	}
	
	private void cascadeDelete(){
		//TODO
	}
	
	private void deleteStatement(Resource subject, URI predicate, Value object, Resource context){
		if(context == null){
			statements.deleteStatement(new StatementImpl(subject, predicate, object));
		}
		else{
			statements.deleteStatement(new ContextStatementImpl(subject, predicate, object, context));
		}
	}
	

	@Override
	protected AbstractPropertiesToStatementsHandlers getInstance(
			RDFState statements, Object entity,
			SemanticMappingContext mappingContext) {
		return new PropertiesToDeleteStatementsHandler(statements, entity, mappingContext);
	}

	@Override
	protected boolean allowEmpty() {
		return true;
	}
	

}
