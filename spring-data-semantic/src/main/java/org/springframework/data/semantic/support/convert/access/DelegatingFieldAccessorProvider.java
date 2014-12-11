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
package org.springframework.data.semantic.support.convert.access;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.semantic.convert.access.FieldAccessor;
import org.springframework.data.semantic.convert.access.FieldAccessorProvider;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.util.TypeInformation;

public class DelegatingFieldAccessorProvider implements FieldAccessorProvider {

	private final DelegatingFieldAccessorFactory delegatingFactory;
	
	private final Map<TypeInformation<?>, Map<SemanticPersistentProperty, FieldAccessor>> fieldAccessorCache = new HashMap<TypeInformation<?>, Map<SemanticPersistentProperty,FieldAccessor>>();
	
	public DelegatingFieldAccessorProvider(DelegatingFieldAccessorFactory factory){
		this.delegatingFactory = factory;
	}
	
	@Override
	public Map<SemanticPersistentProperty, FieldAccessor> provideFieldAccessors(
			SemanticPersistentEntity<?> entity) {
		
		final TypeInformation<?> typeInformation = entity.getTypeInformation();
		if(fieldAccessorCache.containsKey(typeInformation)){
			return fieldAccessorCache.get(typeInformation);
		}
		else{
			final Map<SemanticPersistentProperty, FieldAccessor> fieldAccessors =  new HashMap<SemanticPersistentProperty, FieldAccessor>();
			entity.doWithProperties(new PropertyHandler<SemanticPersistentProperty>() {
	            @Override
	            public void doWithPersistentProperty(SemanticPersistentProperty property) {
	            	 final FieldAccessor accessor = delegatingFactory.forField(property);
	                 if(accessor != null){
	                 	fieldAccessors.put(property, accessor);
	                 }
	            }
			});
			
			entity.doWithAssociations(new AssociationHandler<SemanticPersistentProperty>() {
	            @Override
	            public void doWithAssociation(Association<SemanticPersistentProperty> association) {
	                final SemanticPersistentProperty property = association.getInverse();
	                final FieldAccessor accessor = delegatingFactory.forField(property);
	                if(accessor != null){
	                	fieldAccessors.put(property, accessor);
	                }
	            }
	        });
			fieldAccessorCache.put(typeInformation, fieldAccessors);
			return fieldAccessors;
		}
	}

}
