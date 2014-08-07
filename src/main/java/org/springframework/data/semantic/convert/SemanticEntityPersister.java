package org.springframework.data.semantic.convert;

import org.openrdf.model.Statement;
import org.openrdf.query.GraphQueryResult;
import org.springframework.data.semantic.core.RDFState;


/**
 * Interface defining read and write operations of a facade that hides details like caching, transactions etc.
 * @author konstantin.pentchev
 *
 */
public interface SemanticEntityPersister {

	/**
	 * Creates a DAO entity from a given state and a set of {@link Statement}s given as a {@link GraphQueryResult}.
	 * @param statements
	 * @param type
	 * @return
	 */
	public <T> T createEntityFromState(RDFState statements, Class<T> type);
	
	/**
	 * Persist the given entity's state.
	 * @param entity
	 * @return
	 */
	public <T> T persistEntity(T entity, RDFState existing);
}
