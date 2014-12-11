/**
 * Copyright (C) 2014 Ontotext AD (info@ontotext.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.semantic.convert;

import java.util.Map;

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
	<T> T createEntityFromState(RDFState statements, Class<T> type);
	
	/**
	 * Persist the given entity's state.
	 * @param entity
	 * @return
	 */
	<T> T persistEntity(T entity, RDFState existing);
	
	/**
	 * Persist the state of al given entities.
	 * @param entitiesToExistingState
	 * @return
	 */
	<T> Iterable<T> persistEntities(Map<T, RDFState> entitiesToExistingState);
}
