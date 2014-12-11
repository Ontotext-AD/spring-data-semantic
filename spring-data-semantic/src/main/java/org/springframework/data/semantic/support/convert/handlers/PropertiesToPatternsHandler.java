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
import org.springframework.data.semantic.support.util.ValueUtils;

public class PropertiesToPatternsHandler extends AbstractPropertiesToQueryHandler{

	private StringBuilder sb;
	private String binding;
	private Map<String, Object> propertyToValue;
	private ObjectToLiteralConverter objectToLiteralConverter;
	private int depth;
	private boolean isCount;
	private boolean isDelete;
	private final MappingPolicy globalMappingPolicy;
	
	
	public PropertiesToPatternsHandler(StringBuilder sb, String binding, Map<String, Object> propertyToValue, SemanticMappingContext mappingContext, boolean isCount, boolean isDelete, MappingPolicy globalMappingPolicy){
		this(sb, binding, propertyToValue, mappingContext, 0, isCount, isDelete, globalMappingPolicy);
	}
	
	public PropertiesToPatternsHandler(StringBuilder sb, String binding, Map<String, Object> propertyToValue, SemanticMappingContext mappingContext, int depth, boolean isCount, boolean isDelete, MappingPolicy globalMappingPolicy){
		super(mappingContext);
		this.sb = sb;
		this.binding = binding;
		this.propertyToValue = propertyToValue;
		this.objectToLiteralConverter = ObjectToLiteralConverter.getInstance();
		this.depth = depth;
		this.isCount = isCount;
		this.isDelete = isDelete;
		this.globalMappingPolicy = globalMappingPolicy;
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
			handleAssociation(association);
		}
	}
	
	public void handleAssociation(Association<SemanticPersistentProperty> association){
		SemanticPersistentProperty persistentProperty = association.getInverse();
		//TODO handle existing value in propertyToValue
		//handlePersistentProperty(persistentProperty);
		Object objectValue = this.propertyToValue.get(persistentProperty.getName());
		Boolean optional = persistentProperty.isOptional() && (objectValue == null) && !isDelete;
		if(optional && isCount){
			return;
		}
		if(optional){
			sb.append("OPTIONAL { ");
		}
		handlePersistentProperty(persistentProperty);
		if(persistentProperty.getMappingPolicy().combineWith(globalMappingPolicy).shouldCascade(Cascade.GET)){
			SemanticPersistentEntity<?> associatedPersistentEntity = mappingContext.getPersistentEntity(persistentProperty.getActualType());
			if(objectValue == null){
				String associationBinding = getObjectBinding(binding, persistentProperty);
				appendPattern(sb, associationBinding, "<"+ValueUtils.RDF_TYPE_PREDICATE+">", "<"+associatedPersistentEntity.getRDFType()+">");
				PropertiesToPatternsHandler associationHandler = new PropertiesToPatternsHandler(this.sb, associationBinding, new HashMap<String, Object>(), this.mappingContext, ++this.depth, this.isCount, this.isDelete, globalMappingPolicy);
				associatedPersistentEntity.doWithProperties(associationHandler);
				associatedPersistentEntity.doWithAssociations(associationHandler);
			}
		}
		if(optional){
			sb.append("} ");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void handlePersistentProperty(SemanticPersistentProperty persistentProperty) {
		if(isRetrivableProperty(persistentProperty)){
			Object objectValue = this.propertyToValue.get(persistentProperty.getName());
			Boolean optional = persistentProperty.isOptional() && (objectValue == null) && !persistentProperty.isAssociation() && !isDelete; //&& !isTransitiveOptional
			if(optional && isCount){
				return;
			}
			URI predicate = persistentProperty.getPredicate();
			String subj = "";
			String obj = "";
			String pred = "<"+predicate+">";
			subj = binding;
			if(objectValue != null){
				if(objectValue instanceof Collection<?> || objectValue.getClass().isArray()){
					if(objectValue.getClass().isArray()){
						objectValue = Arrays.asList((Object[])objectValue);
					}
					for (Object o : (Collection<Object>) objectValue){
						Value val = this.objectToLiteralConverter.convert(o);
						obj = val instanceof URI ? "<"+val.toString()+">" : val.toString();
						addPattern(persistentProperty, optional, subj, pred, obj);
					}
				}
				else{
					Value val = this.objectToLiteralConverter.convert(objectValue);
					obj = val instanceof URI ? "<"+val.toString()+">" : val.toString();
					addPattern(persistentProperty, optional, subj, pred, obj);
				}
			}
			else{
				obj = getObjectBinding(binding, persistentProperty);
				addPattern(persistentProperty, optional, subj, pred, obj);
			}
		}
	}
	
	private void addPattern(SemanticPersistentProperty persistentProperty, Boolean optional, String subj, String pred, String obj){
		if(optional){
			sb.append("OPTIONAL { ");
		}
		if(persistentProperty.isAssociation()){
			if(Direction.INCOMING.equals(persistentProperty.getDirection())){
				SemanticPersistentProperty associatedProperty = persistentProperty.getInverseProperty();
				if(associatedProperty != null){
					pred = "<"+associatedProperty.getPredicate()+">";
					appendPattern(sb, obj, pred, subj);
				}
				else{
					appendPattern(sb, subj, pred, obj);
				}
			}
			else{
				appendPattern(sb, subj, pred, obj);
			}
		}
		else{
			appendPattern(sb, subj, pred, obj);
		}
		if(optional){
			sb.append("} ");
		}
	}
}
