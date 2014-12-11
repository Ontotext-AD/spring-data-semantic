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
package org.springframework.data.semantic.convert.state;

import java.lang.reflect.Field;

import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

/**
 * Interface for representing the state of an entity. EntityStates can be
 * attached and detached. The former load the property values from the store,
 * the latter from memory (and are hence referred-to as 'dirty').
 * 
 * @author konstantin.pentchev
 * 
 * @param <STATE>
 */
public interface EntityState<T, STATE> {

	/**
	 * Retrieve the entity of the given state.
	 * @return
	 */
	T getEntity();

	void setPersistentState(STATE state);

	/**
	 * @return a default value for the given field by its {@link FieldAccessor}
	 *         or {@code null} if none is provided.
	 */
	Object getDefaultValue(SemanticPersistentProperty property);

	/**
	 * 
	 * @return value of the field either from the state and/or the entity
	 */
	Object getValue(Field field, MappingPolicy mappingPolicy);

	/**
	 * @return value of the property either from the state and/or the entity
	 */
	Object getValue(SemanticPersistentProperty property,
			MappingPolicy mappingPolicy);

	/**
	 * @return true if the field can be written
	 */
	boolean isWritable(SemanticPersistentProperty property);

	/**
	 * 
	 * 
	 * @return sets the value in the entity and/or the state
	 */
	Object setValue(Field field, Object newVal, MappingPolicy mappingPolicy);

	Object setValue(SemanticPersistentProperty property, Object newVal,
			MappingPolicy mappingPolicy);

	/**
	 * callback for creating and initializing an initial state TODO will be
	 * internal implementation detail of persist
	 */
	void createAndAssignState();

	boolean hasPersistentState();

	STATE getPersistentState();

	T persist();

	SemanticPersistentEntity<T> getPersistentEntity();
}
