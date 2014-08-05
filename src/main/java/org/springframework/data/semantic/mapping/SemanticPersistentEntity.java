package org.springframework.data.semantic.mapping;

import org.openrdf.model.URI;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.semantic.core.RDFState;

/**
 * 
 * @author konstantin.pentchev
 *
 * @param <T>
 */
public interface SemanticPersistentEntity<T> extends PersistentEntity<T, SemanticPersistentProperty> {
	
	 /**
	  * Creates and returns the MappingPolicy for this Semantic
	  * @return
	  */
	 MappingPolicy getMappingPolicy();
	 
	 /**
	  * Returns the {@link SemanticPersistentProperty} to be used as RDF context or null if none is defined.
	  * @return
	  */
	 SemanticPersistentProperty getContextProperty();
	 
	 /**
	  * Check if a context property is defined and set.
	  * @return
	  */
	 boolean hasContextProperty();
	 
	 /**
	  * Returns the 
	  * @return
	  */
	 URI getRDFType();
	 
	 URI getResourceId(Object entity);
	 
	 /**
	  * Sets the subject id of the entity from the given statements.
	  * @param entity
	  * @param statements
	  */
	 public void setPersistentState(Object entity, RDFState statements);

}
