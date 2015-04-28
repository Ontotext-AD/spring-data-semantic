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
package org.springframework.data.semantic.filter;

import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

/**
 * Created by itrend on 4/27/15.
 */
public interface ValueFilter {
	/**
	 * Returns a string representation of the filter as it would appear in
	 * SPARQL filter clause. Return null to indicate this property is not to be filtered
	 * @param property the property to be filtered
	 * @return the filter condition in SPARQL syntax (without the FILTER keyword). null return
	 * means there will be no filter
	 */
	String toString(SemanticPersistentProperty property);
}
