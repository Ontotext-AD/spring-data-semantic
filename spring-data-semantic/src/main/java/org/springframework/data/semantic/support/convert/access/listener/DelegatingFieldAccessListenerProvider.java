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
package org.springframework.data.semantic.support.convert.access.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.semantic.convert.access.listener.FieldAccessListener;
import org.springframework.data.semantic.convert.access.listener.FieldAccessListenerProvider;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.util.TypeInformation;

public class DelegatingFieldAccessListenerProvider implements FieldAccessListenerProvider{
	
	private final DelegatingFieldAccessListenerFactory delegatingListenerFactory;
	private final Map<TypeInformation<?>, Map<SemanticPersistentProperty, List<FieldAccessListener>>> fieldAccessListenerCache = new HashMap<TypeInformation<?>, Map<SemanticPersistentProperty, List<FieldAccessListener>>>();
	
	
	public DelegatingFieldAccessListenerProvider(DelegatingFieldAccessListenerFactory delegatingListenerFactory){
		this.delegatingListenerFactory = delegatingListenerFactory;
	}

	@Override
	public Map<SemanticPersistentProperty, List<FieldAccessListener>> provideFieldAccessListeners(SemanticPersistentEntity<?> entity) {
		final TypeInformation<?> typeInformation = entity.getTypeInformation();
		if(fieldAccessListenerCache.containsKey(typeInformation)){
			return fieldAccessListenerCache.get(typeInformation);
		}
		else{
			final Map<SemanticPersistentProperty, List<FieldAccessListener>> fieldAccessors =  new HashMap<SemanticPersistentProperty, List<FieldAccessListener>>();
			entity.doWithProperties(new PropertyHandler<SemanticPersistentProperty>() {
	            @Override
	            public void doWithPersistentProperty(SemanticPersistentProperty property) {
	            	 final List<FieldAccessListener> accessListener = delegatingListenerFactory.forField(property);
	                 if(accessListener != null){
	                 	fieldAccessors.put(property, accessListener);
	                 }
	            }
			});
			
			entity.doWithAssociations(new AssociationHandler<SemanticPersistentProperty>() {
	            @Override
	            public void doWithAssociation(Association<SemanticPersistentProperty> association) {
	                final SemanticPersistentProperty property = association.getInverse();
	                final List<FieldAccessListener> accessListener = delegatingListenerFactory.forField(property);
	                if(accessListener != null){
	                	fieldAccessors.put(property, accessListener);
	                }
	            }
	        });
			fieldAccessListenerCache.put(typeInformation, fieldAccessors);
			return fieldAccessors;
		}
	}

}
