package org.springframework.data.semantic.core;

import java.util.Collection;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public interface SemanticOperationsCRUD {
	
	SemanticMappingContext getSemanticMappingContext();
	
	/**
	 * Store the given entities in the rdf store. If the subject {@link URI} of an entity is already present in the store, the statements for it are updated, otherwise
     * statements are just added. Attached relationships will be cascaded. The operation occurs in a single transaction.
     * This method is also provided by the appropriate repository.
	 * @param entities
	 * @return
	 */
	<T> Iterable<T> save(Iterable<T> entities);
	
	 /**
     * Stores the given entity in the rdf store. If the subject {@link URI} is already present in the store, the statements are updated, otherwise
     * statements are just added. Attached relationships will be cascaded.
     * This method is also provided by the appropriate repository.
     */
    <T> T save(T entity);

    /**
     * Removes the given statements from the rdf store, the entity is first removed
     * from all indexes and then deleted.
     * @param entity
     */
    <T> void delete(URI resourceId, Class<? extends T> clazz);
    
    /**
     * Removes the given statements from the rdf store, the entity is first removed
     * from all indexes and then deleted.
     * @param entity
     */
    <T> void delete(T entity);
    
    /**
     * Removes the statements for all entities of the given class.
     * @param clazz
     */
    <T> void deleteAll(Class<? extends T> clazz);
    
    /**
     * Retrieves an entity of the given type T that is identified by the given {@link URI}.
     * @param resourceId
     * @return
     */
    <T> T find(URI resourceId, Class<? extends T> clazz);
    
    /**
     * Retrieve all entities of the given type T.
     * @param clazz
     * @return
     */
    <T> Iterable<T> findAll(Class<? extends T> clazz);
    
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
	
	/**
	 * Check if an instance of the given class with the given {@link URI} id exists in the semantic database. 
	 * @param resourceId
	 * @param clazz
	 * @return
	 */
	<T> boolean exists(URI resourceId, Class<? extends T> clazz);
	
	/**
	 * Retrieve a collection of entities of the given type that fulfill the parameter requirements.
	 * @param clazz
	 * @param parameterToValue
	 * @return
	 */
	<T> Collection<T> findByProperty(Class<? extends T> clazz, Map<String, Object> parameterToValue);
	
	
	
}
