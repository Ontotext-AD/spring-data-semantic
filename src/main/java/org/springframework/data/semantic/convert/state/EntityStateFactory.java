package org.springframework.data.semantic.convert.state;

/**
 * Factory interface for creating {@link EntityState}s.
 * @author konstantin.pentchev
 *
 * @param <STATE>
 */
public interface EntityStateFactory<STATE> {

	/**
	 * Creates and returns an {@link EntityState} for the given entity. 
	 * @param entity
	 * @param detachable
	 * @return
	 */
	public <R> EntityState<R, STATE> getEntityState(final R entity, boolean detachable);
}
