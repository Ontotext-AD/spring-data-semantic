package org.springframework.data.semantic.convert;

import org.openrdf.model.URI;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;

/**
 * 
 * @author konstantin.pentchev
 *
 */
public interface SemanticEntityInstantiator {

	/**
	 * Create instances of the given class and link them to the provided state.
	 * @param entity
	 * @param statements - the given state.
	 * @return
	 */
	public <T> T createInstanceFromState(SemanticPersistentEntity<T> entity, RDFState statements); 
	
	/**
	 * Creates and instance of the given class with the given {@link URI} as id.
	 * @param entity
	 * @param id
	 * @return
	 */
	public <T> T createInstance(SemanticPersistentEntity<T> entity, URI id);
	
}
