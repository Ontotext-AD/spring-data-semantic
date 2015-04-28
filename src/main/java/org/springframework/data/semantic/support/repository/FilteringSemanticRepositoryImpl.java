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
package org.springframework.data.semantic.support.repository;

import org.openrdf.model.URI;
import org.springframework.data.semantic.filter.ValueFilter;
import org.springframework.data.semantic.repository.FilteringSemanticRepository;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;

/**
 * Created by itrend on 4/27/15.
 */
public class FilteringSemanticRepositoryImpl<T>
		extends SemanticRepositoryImpl<T>
		implements FilteringSemanticRepository<T> {

	public FilteringSemanticRepositoryImpl(SemanticOperationsCRUD operations, Class<T> clazz) {
		super(operations, clazz);
	}

	@Override public T findOne(URI id, ValueFilter filter) {
		return operations.find(id, clazz, filter);
	}
}
