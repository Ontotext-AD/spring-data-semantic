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
package org.springframework.data.semantic.mapping;

import java.util.List;

import org.openrdf.model.URI;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.semantic.annotation.Context;
import org.springframework.data.semantic.annotation.Optional;
import org.springframework.data.semantic.support.Direction;

public interface SemanticPersistentProperty extends PersistentProperty<SemanticPersistentProperty> {

	Direction getDirection();
	
	String getBindingName(); 

	/**
	 * Checks whether the property has an explicit predicate associated with it.
	 * 
	 * @return
	 */
	boolean hasPredicate();

	/**
	 * Returns the associated predicate. This operation includes resolving of
	 * namespace.
	 * 
	 * @return
	 */
	URI getPredicate();
	
	/**
	 * Returns the predicate associated with the inverse property from an associated entity. Null otherwise.
	 * @return
	 */
	SemanticPersistentProperty getInverseProperty();

	/**
	 * Returns a dummy/alias predicate to be used in CONSTRUCT query bindings.
	 * 
	 * @return
	 */
	String getAliasPredicate();

	/**
	 * Checks whether the property has a language associated with it.
	 * 
	 * @return
	 */
	boolean hasLanguage();

	/**
	 * Returns the associated languages or an empty {@link List}.
	 * 
	 * @return
	 */
	List<String> getLanguage();

	/**
	 * Checks whether the property has an associated XSD datatype.
	 * 
	 * @return
	 */
	boolean hasDatatype();

	/**
	 * Checks whether the property is annotated as {@link Context}.
	 * 
	 * @return
	 */
	boolean isContext();

	/**
	 * Checks whether the property is annotated as {@link Optional}.
	 * 
	 * @return
	 */
	boolean isOptional();

	/**
	 * Returns the associated XSD datatype or null.
	 * 
	 * @return
	 */
	String getDatatype();

	/**
	 * Returns the value of the property field. Based on the
	 * {@link MappingPolicy} this can be the (dirty) value from memory or the
	 * (queried) value from the store. IdProperties always return from memory.
	 * 
	 * @param entity
	 * @param mappingPolicy
	 * @return
	 */
	Object getValue(Object entity, MappingPolicy mappingPolicy);

	/**
	 * Set the property value of the given entity to the given new value.
	 * 
	 * @param entity
	 * @param newValue
	 */
	void setValue(Object entity, Object newValue);

	/**
	 * Return the property-specific {@link MappingPolicy}.
	 * 
	 * @return
	 */
	MappingPolicy getMappingPolicy();

}
