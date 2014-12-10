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

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.semantic.convert.access.FieldAccessor;
import org.springframework.data.semantic.convert.access.FieldAccessorFactory;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;

public class DelegatingFieldAccessorFactory implements FieldAccessorFactory{

	private List<FieldAccessorFactory> factories = new LinkedList<FieldAccessorFactory>();
	private SemanticTemplateStatementsCollector statementsCollector;
	private SemanticOperationsCRUD operations;
	
	public DelegatingFieldAccessorFactory(SemanticTemplateStatementsCollector statementsCollector, 
			SemanticOperationsCRUD operations){
		this.statementsCollector = statementsCollector;
		this.operations = operations;
		createFactories();
	}
	
	private void createFactories(){
		PropertyFieldAccessorFactory pfaf = new PropertyFieldAccessorFactory(statementsCollector);
		AssociationFieldAccessorFactory afaf = new AssociationFieldAccessorFactory(statementsCollector, operations);
		factories.add(pfaf);
		factories.add(afaf);
	}
	
	@Override
	public boolean accept(SemanticPersistentProperty property) {
		return true;
	}

	@Override
	public FieldAccessor forField(SemanticPersistentProperty property) {
		for(FieldAccessorFactory factory : factories){
			if(factory.accept(property)){
				return factory.forField(property);
			}
		}
		return null;
	}

}
