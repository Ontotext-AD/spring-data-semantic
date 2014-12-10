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
package org.springframework.data.semantic.support.convert.state;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.semantic.convert.state.EntityState;
import org.springframework.data.semantic.convert.state.EntityStateFactory;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.access.listener.DelegatingFieldAccessListenerFactory;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class SemanticEntityStateFactory implements EntityStateFactory<RDFState>{
	
	private SemanticMappingContext mappingContext;
	private DelegatingFieldAccessorFactory delegatingFieldAccessorFactory;
	private DelegatingFieldAccessListenerFactory delegatingFieldAccessListenerFactory;
	private SemanticDatabase semanticDatabase;
	private ConversionService conversionService;
	
	public SemanticEntityStateFactory(SemanticMappingContext mappingContext, DelegatingFieldAccessorFactory delegatingFieldAccessorFactory, DelegatingFieldAccessListenerFactory delegatingFieldAccessListenerFactory, SemanticDatabase semanticDatabase, ConversionService conversionService){
		this.delegatingFieldAccessorFactory = delegatingFieldAccessorFactory;
		this.delegatingFieldAccessListenerFactory = delegatingFieldAccessListenerFactory;
		this.mappingContext = mappingContext;
		this.semanticDatabase = semanticDatabase;
		this.conversionService = conversionService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> EntityState<R, RDFState> getEntityState(R entity,
			boolean detachable) {
		final Class<?> entityType = entity.getClass();
		SemanticPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(entityType);
		return new SemanticEntityState<R>(new RDFState(), semanticDatabase, entity, delegatingFieldAccessorFactory, delegatingFieldAccessListenerFactory, (SemanticPersistentEntity<R>) persistentEntity, conversionService);
	}
	
}
