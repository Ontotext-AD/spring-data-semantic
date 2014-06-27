package org.springframework.data.semantic.convert;

import org.openrdf.model.Model;
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
	public <T> T createInstanceFromState(SemanticPersistentEntity<T> entity, Model statements); 
	
}
