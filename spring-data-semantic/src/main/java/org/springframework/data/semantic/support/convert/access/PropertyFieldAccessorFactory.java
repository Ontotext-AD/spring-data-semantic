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

import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.data.semantic.convert.access.FieldAccessor;
import org.springframework.data.semantic.convert.access.FieldAccessorFactory;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;

public class PropertyFieldAccessorFactory implements FieldAccessorFactory {
	
	private SemanticTemplateStatementsCollector template;

	public PropertyFieldAccessorFactory(SemanticTemplateStatementsCollector template) {
		this.template = template;
	}
	@Override
	public boolean accept(SemanticPersistentProperty property) {
		return !property.isAssociation();
	}

	@Override
	public FieldAccessor forField(SemanticPersistentProperty property) {
		return new PropertyFieldAccessor(property, template);
	}
	
	public static class PropertyFieldAccessor implements FieldAccessor {
		private SemanticPersistentProperty property;
		private URI predicate;
		private Class<?> fieldType;
		private SemanticTemplateStatementsCollector template;
		
		public PropertyFieldAccessor(SemanticPersistentProperty property, SemanticTemplateStatementsCollector template){
			this.property = property;
			this.predicate = property.getPredicate();
			this.fieldType = property.getType();
			this.template = template;
		}

		@Override
		public Object getDefaultValue() {
			return null;
		}

		@Override
		public Object setValue(Object entity, Object newVal,
				MappingPolicy mappingPolicy) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getValue(Object entity, MappingPolicy mappingPolicy) {
			//TODO multiple values
			Model stIterator = template.getStatementsForResourceProperty(entity, property);
			
			if(!stIterator.isEmpty()) {
				return stIterator.objects().iterator().next().stringValue();
			}
			return null;
		}

		@Override
		public boolean isWritable(Object entity) {
			// TODO Auto-generated method stub
			return false;
		}		
		
	}

}
