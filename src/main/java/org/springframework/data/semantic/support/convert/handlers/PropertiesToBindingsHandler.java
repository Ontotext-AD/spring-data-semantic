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
package org.springframework.data.semantic.support.convert.handlers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.springframework.data.mapping.Association;
import org.springframework.data.semantic.convert.ObjectToLiteralConverter;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.Cascade;
import org.springframework.data.semantic.support.Direction;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class PropertiesToBindingsHandler extends AbstractPropertiesToQueryHandler {

	private StringBuilder sb;
	private String binding;
	private Map<String, Object> propertyToValue;
	private ObjectToLiteralConverter objectToLiteralConverter;
	private int depth;
	private final MappingPolicy globalMappingPolicy;
	private final Boolean originalPredicates;
	
	public PropertiesToBindingsHandler(StringBuilder sb, String binding, Map<String, Object> propertyToValue, SemanticMappingContext mappingContext, MappingPolicy globalMappingPolicy){
		this(sb, binding, propertyToValue, mappingContext, 0, globalMappingPolicy, false);
	}
	
	public PropertiesToBindingsHandler(StringBuilder sb, String binding, Map<String, Object> propertyToValue, SemanticMappingContext mappingContext, MappingPolicy globalMappingPolicy, Boolean originalPredicates){
		this(sb, binding, propertyToValue, mappingContext, 0, globalMappingPolicy, originalPredicates);
	}
	
	public PropertiesToBindingsHandler(StringBuilder sb, String binding, Map<String, Object> propertyToValue, SemanticMappingContext mappingContext, int depth, MappingPolicy globalMappingPolicy, Boolean originalPredicates){
		super(mappingContext);
		this.sb = sb;
		this.binding = binding;
		this.propertyToValue = propertyToValue;
		this.objectToLiteralConverter = ObjectToLiteralConverter.getInstance();
		this.depth = depth;
		this.globalMappingPolicy = globalMappingPolicy;
		this.originalPredicates = originalPredicates;
	}
	
	@Override
	public void doWithPersistentProperty(
			SemanticPersistentProperty persistentProperty) {
		handlePersistentProperty(persistentProperty);			
	}
	
	@Override
	public void doWithAssociation(
			Association<SemanticPersistentProperty> association) {
		if(depth < 50){
			handleAssociation(association.getInverse());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void handlePersistentProperty(SemanticPersistentProperty persistentProperty) {
		if(isRetrivableProperty(persistentProperty)){
			Object objectValue = propertyToValue.get(persistentProperty.getName());
			if(objectValue != null){
				if(objectValue instanceof Collection<?> || objectValue.getClass().isArray()){
					if(objectValue.getClass().isArray()){
						objectValue = Arrays.asList((Object[]) objectValue);
					}
					for(Object o : (Collection<Object>) objectValue){
						Value val = this.objectToLiteralConverter.convert(o);
						String obj = val instanceof URI ? "<"+val+">" : val.toString();
						if(originalPredicates){
							appendPattern(sb, binding, "<" + persistentProperty.getPredicate() + ">", obj);
						}
						else{
							appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", obj);
						}
					}
				}
				else{
					Value val = this.objectToLiteralConverter.convert(objectValue);
					String obj = val instanceof URI ? "<"+val+">" : val.toString();
					if(originalPredicates){
						appendPattern(sb, binding, "<" + persistentProperty.getPredicate() + ">", obj);
					}
					else{
						appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", obj);
					}
				}
			}
			else{
				if(originalPredicates){
					appendPattern(sb, binding, "<" + persistentProperty.getPredicate() + ">", getObjectBinding(binding, persistentProperty));
				}
				else{
					appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", getObjectBinding(binding, persistentProperty));
				}
			}
			
		}
	}
	
	private void handleAssociation(SemanticPersistentProperty persistentProperty) {
		String associationBinding = getObjectBinding(binding, persistentProperty);
		Object objectValue = propertyToValue.get(persistentProperty.getName());
		if(objectValue == null){
			if(originalPredicates){
				if(Direction.OUTGOING.equals(persistentProperty.getDirection())){
					appendPattern(sb, binding, "<" + persistentProperty.getPredicate() + ">", associationBinding);
				}
				else if(Direction.INCOMING.equals(persistentProperty.getDirection())){
					appendPattern(sb, associationBinding, "<" + persistentProperty.getPredicate() + ">", binding);
				}
				else{
					appendPattern(sb, binding, "<" + persistentProperty.getPredicate() + ">", associationBinding);
					appendPattern(sb, associationBinding, "<" + persistentProperty.getPredicate() + ">", binding);
				}
			}
			else{
				appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", associationBinding);
			}
			if(persistentProperty.getMappingPolicy().combineWith(globalMappingPolicy).shouldCascade(Cascade.GET)){
				SemanticPersistentEntity<?> associatedPersistentEntity = mappingContext.getPersistentEntity(persistentProperty.getActualType());
				appendPattern(sb, associationBinding, "a", "<"+associatedPersistentEntity.getRDFType()+">");
				PropertiesToBindingsHandler associationHandler = new PropertiesToBindingsHandler(this.sb, associationBinding, new HashMap<String, Object>(), this.mappingContext, ++this.depth, this.globalMappingPolicy, this.originalPredicates);
				associatedPersistentEntity.doWithProperties(associationHandler);
				associatedPersistentEntity.doWithAssociations(associationHandler);
			}
		}
	}
}
