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
package org.springframework.data.semantic.core;

import org.openrdf.model.Model;
import org.openrdf.query.GraphQueryResult;
import org.springframework.data.semantic.mapping.MappingPolicy;

public interface SemanticOperationsObjectCreator {
	/**
	 * Create a target object of the given class from the provided statements in {@link GraphQueryResult}.
	 * @param rdfGraph
	 * @param targetClazz
	 * @param mappingPolicy
	 * @return
	 */
	<T> T createObjectFromStatements(
			Model rdfGraph, Class<T> targetClazz, MappingPolicy mappingPolicy);
}
