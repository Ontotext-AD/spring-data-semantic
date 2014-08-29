package org.springframework.data.semantic.support.convert.handlers;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.ValueUtils;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class PropertiesToDeleteStatementsHandler extends AbstractPropertiesToStatementsHandlers {

	private URI resourceId;
	
	public PropertiesToDeleteStatementsHandler(URI resourceId, RDFState statements, Object entity, SemanticPersistentEntity<?> persistentEntity, SemanticMappingContext mappingContext){
		super(statements, entity, persistentEntity, mappingContext);
		this.resourceId = resourceId;
	}

	@Override
	protected void processStatement(SemanticPersistentProperty persistentProperty, Object value) {
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
	

}
