package org.springframework.data.semantic.support.convert;

import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.support.convert.handlers.PropertiesToDeleteStatementsHandler;
import org.springframework.data.semantic.support.convert.handlers.PropertiesToStatementsHandler;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
/**
 * Class that converts domain objects to RDF.
 * 
 * @author konstantin.pentchev
 *
 */
public class EntityToStatementsConverter {
	
	private SemanticMappingContext mappingContext;
	
	
	
	public EntityToStatementsConverter(SemanticMappingContext mappingContext){
		this.mappingContext = mappingContext;
	}
	
	public RDFState convertEntityToStatements(SemanticPersistentEntity<?> persistentEntity, Object entity){
		RDFState statements = new  RDFState();
		PropertiesToStatementsHandler handler = new PropertiesToStatementsHandler(statements, entity, mappingContext);
		persistentEntity.doWithProperties(handler);
		persistentEntity.doWithAssociations(handler);
		return statements;
	}
	
	public RDFState convertEntityToDeleteStatements(SemanticPersistentEntity<?> persistentEntity, Object entity){
		RDFState statements = new  RDFState();
		PropertiesToDeleteStatementsHandler handler = new PropertiesToDeleteStatementsHandler(statements, entity, mappingContext);
		persistentEntity.doWithProperties(handler);
		persistentEntity.doWithAssociations(handler);
		return statements;
	}
	
	public RDFState convertClassToDeleteStatements(SemanticPersistentEntity<?> persistentEntity){
		RDFState statements = new RDFState();
		//TODO
		return statements;
	}
	

}
