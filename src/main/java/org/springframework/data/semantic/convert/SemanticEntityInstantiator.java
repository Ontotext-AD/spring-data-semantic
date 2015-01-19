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
	<T> T createInstanceFromState(SemanticPersistentEntity<T> entity, RDFState statements); 
	
	/**
	 * Creates and instance of the given class with the given {@link URI} as id.
	 * @param entity
	 * @param id
	 * @return
	 */
	<T> T createInstance(SemanticPersistentEntity<T> entity, URI id);
	
}
