package org.springframework.data.semantic.convert;

import org.openrdf.model.Statement;
import org.openrdf.query.GraphQueryResult;
import org.springframework.data.semantic.core.StatementsIterator;
import org.springframework.data.semantic.mapping.MappingPolicy;


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
	 * @param mappingPolicy
	 * @return
	 */
	public <T> T createEntityFromState(StatementsIterator statements, Class<T> type, MappingPolicy mappingPolicy);
}
