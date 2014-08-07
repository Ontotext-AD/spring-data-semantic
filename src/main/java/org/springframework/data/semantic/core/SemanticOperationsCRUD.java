package org.springframework.data.semantic.core;

import org.openrdf.model.Model;
import org.openrdf.model.URI;

public interface SemanticOperationsCRUD {
	
	 /**
     * Stores the given entity in the rdf store, if the subject {@link URI} is already present in the store, the statements are updated, otherwise
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
     * Retrieves an entity of the given type T that is identified by the given {@link URI}.
     * @param resourceId
     * @return
     */
    <T> T find(URI resourceId, Class<? extends T> clazz);
    
    /**
     * Create a new entity form the given {@link Class} and {@link Model}.
     * @param statements
     * @param clazz
     * @return
     */
    <T> T createEntity(Model statements, Class<T> clazz);

    /**
     * Count the instances of a given class in the semantic database.
     * @param clazz
     * @return
     */
	<T> long count(Class<T> clazz);
	
}
