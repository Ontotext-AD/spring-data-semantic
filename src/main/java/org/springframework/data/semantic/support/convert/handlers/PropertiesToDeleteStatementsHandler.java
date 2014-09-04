package org.springframework.data.semantic.support.convert.handlers;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
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
	protected void processStatement(SemanticPersistentProperty persistentProperty, Object value) {
		SemanticPersistentEntity<?> persistentEntity = (SemanticPersistentEntity<?>) persistentProperty.getOwner();
		if(persistentProperty.isContext()){
			return;
		}
		else if(persistentProperty.isIdProperty()){
			if(persistentEntity.hasContextProperty() && persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy()) != null){
				statements.deleteStatement(new ContextStatementImpl((URI) value, new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), persistentEntity.getRDFType(), (Resource) persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy())));
			}
			else{
				statements.deleteStatement(new StatementImpl((URI) value, new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), persistentEntity.getRDFType()));
			}
		}
		else{
			if(persistentEntity.hasContextProperty() && persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy()) != null){
				statements.deleteStatement(new ContextStatementImpl(resourceId, persistentProperty.getPredicate().get(0), objectToLiteralConverter.convert(value), (Resource) persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy())));
			}
			else{
				statements.deleteStatement(new StatementImpl(resourceId, persistentProperty.getPredicate().get(0), objectToLiteralConverter.convert(value)));	
			}
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
