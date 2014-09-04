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
import org.springframework.data.semantic.support.Direction;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.semantic.support.util.ValueUtils;

public class PropertiesToStatementsHandler extends AbstractPropertiesToStatementsHandlers {
	
	private URI resourceId;
	
	
	
	public PropertiesToStatementsHandler(RDFState statements, Object entity, SemanticMappingContext mappingContext){
		super(statements, entity, mappingContext);
		@SuppressWarnings("unchecked")
		SemanticPersistentEntity<Object> persistentEntity = (SemanticPersistentEntity<Object>) mappingContext.getPersistentEntity(entity.getClass());
		this.resourceId = persistentEntity.getResourceId(entity);
	}

	
	@Override
	protected void processPropertyStatement(SemanticPersistentProperty persistentProperty, Object value){
		SemanticPersistentEntity<?> persistentEntity = (SemanticPersistentEntity<?>) persistentProperty.getOwner();
		if(persistentProperty.isContext()){
			return;
		}
		Resource context = persistentEntity.getContext(entity);
		if(persistentProperty.isIdProperty()){
			addStatement((URI) value, new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), persistentEntity.getRDFType(), context);
		}
		else{
			addStatement(resourceId, persistentProperty.getPredicate().get(0), objectToLiteralConverter.convert(value), context);
		}
	}
	
	@Override
	protected void processAssociationStatement(SemanticPersistentProperty persistentProperty, Resource value) {
		SemanticPersistentEntity<?> persistentEntity = (SemanticPersistentEntity<?>) persistentProperty.getOwner();
		Resource context = persistentEntity.getContext(entity);
		if(Direction.OUTGOING.equals(persistentProperty.getDirection())){
			addStatement(resourceId, persistentProperty.getPredicate().get(0), value, context);	
		}
		else if(Direction.INCOMING.equals(persistentProperty.getDirection())){
			addStatement(value, persistentProperty.getPredicate().get(0), resourceId, context);
		}
		else{
			addStatement(resourceId, persistentProperty.getPredicate().get(0), value, context);
			addStatement(value, persistentProperty.getPredicate().get(0), resourceId, context);
		}
	}
	
	private void addStatement(Resource subject, URI predicate, Value object, Resource context){
		if(context == null){
			statements.addStatement(new StatementImpl(subject, predicate, object));
		}
		else{
			statements.addStatement(new ContextStatementImpl(subject, predicate, object, context));
		}
	}


	@Override
	protected AbstractPropertiesToStatementsHandlers getInstance(
			RDFState statements, Object entity,
			SemanticMappingContext mappingContext) {
		return new PropertiesToStatementsHandler(statements, entity, mappingContext);
	}


	@Override
	protected boolean allowEmpty() {
		return false;
	}





	
}
