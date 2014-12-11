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
package org.springframework.data.semantic.support.convert;

import java.util.Map;

import org.springframework.data.semantic.convert.SemanticEntityConverter;
import org.springframework.data.semantic.convert.SemanticEntityPersister;
import org.springframework.data.semantic.core.RDFState;

public class SemanticEntityPersisterImpl implements SemanticEntityPersister{
	
	private SemanticEntityConverter entityConverter;
	
	public SemanticEntityPersisterImpl(SemanticEntityConverter entityConverter){
		this.entityConverter = entityConverter;
	}
	

	@Override
	public <T> T createEntityFromState(RDFState statements,
			Class<T> type) {
		if (statements.isEmpty()) {
            return null;
        }
		return entityConverter.read(type, statements);
	}


	@Override
	public <T> T persistEntity(T entity, RDFState dbState) {
		entityConverter.write(entity, dbState);
		return entity;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> Iterable<T> persistEntities(
			Map<T, RDFState> entitiesToExistingState) {
		entityConverter.write((Map<Object, RDFState>) entitiesToExistingState);
		return entitiesToExistingState.keySet();
	}

}
