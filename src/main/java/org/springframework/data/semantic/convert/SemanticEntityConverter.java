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

import org.springframework.data.convert.EntityConverter;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface SemanticEntityConverter extends EntityConverter<SemanticPersistentEntity<?>, SemanticPersistentProperty, Object, RDFState> {

	/**
	 * Loads data from state into the properties of the given entities.
	 * @param entity
	 * @param source
	 * @param mappingPolicy
	 * @param persistentEntity
	 * @return
	 */
	<R> R loadEntity(R entity, RDFState source, MappingPolicy mappingPolicy, SemanticPersistentEntity<R> persistentEntity);
	
	/**
	 * Updates the existing state for each entity with the given object's new state.
	 * @param objectsAndState
	 */
	void write(Map<Object, RDFState> objectsAndState);
	
}
