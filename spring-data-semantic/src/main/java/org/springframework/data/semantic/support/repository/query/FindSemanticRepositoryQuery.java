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
package org.springframework.data.semantic.support.repository.query;

import java.util.Map;

import org.springframework.data.repository.query.Parameters;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;

public class FindSemanticRepositoryQuery extends AbstractSemanticRepositoryQuery{
	
	private static final String PREFIX = "By";

	public FindSemanticRepositoryQuery(SemanticOperationsCRUD operations,
			String queryMethodName, Class<?> domainClass,
			Parameters<?, ?> parameters) {
		super(operations, queryMethodName, domainClass, parameters);
	}

	@Override
	public Object doExecute(Map<String, Object> params) {
		return operations.findByProperty(this.domainClass, params);
	}

	@Override
	public String getPrefix() {
		return PREFIX;
	}

}
