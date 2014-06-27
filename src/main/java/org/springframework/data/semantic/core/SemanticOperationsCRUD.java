package org.springframework.data.semantic.core;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.GraphQueryResult;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface SemanticOperationsCRUD {
	/*
	public void persistStatement(Statement statement);
	
	public void persistStatements(List<Statement> statements);
	
	public void deleteStatement(Statement statement);
	
	public void deleteStatements(List<Statement> statements);*/
	
	
	 /**
     * Stores the given entity in the rdf store, if the subject URI is already present in the store, the statements are updated, otherwise
     * statements are just added. Attached relationships will be cascaded.
     * This method is also provided by the appropriate repository.
     */
    <T> T save(T entity);

    /**
     * Removes the given statements from the rdf store, the entity is first removed
     * from all indexes and then deleted.
     * @param entity
     */
    void delete(Object entity);
    
    /**
     * Loades the provided statements to be used as an entity of the given type.
     * @param statements
     * @param target
     * @return
     */
    <T> T load(List<Statement> statements, T target);

	
	
}
