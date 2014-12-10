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

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.semantic.convert.access.listener.FieldAccessListener;
import org.springframework.data.semantic.convert.access.listener.FieldAccessListenerFactory;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;
import org.springframework.data.semantic.support.convert.access.AssociationFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.access.PropertyFieldAccessorFactory;

public class DelegatingFieldAccessListenerFactory implements FieldAccessListenerFactory{
	
	private List<FieldAccessListenerFactory> factories = new LinkedList<FieldAccessListenerFactory>();
	private SemanticTemplateStatementsCollector statementsCollector;
	private SemanticOperationsCRUD operations;

	public DelegatingFieldAccessListenerFactory(SemanticTemplateStatementsCollector statementsCollector, 
			SemanticOperationsCRUD operations){
		this.statementsCollector = statementsCollector;
		this.operations = operations;
		createFactories();
	}
	
	private void createFactories(){
		AssociationFieldAccessListenerFactory afalc = new AssociationFieldAccessListenerFactory(statementsCollector, operations);
		factories.add(afalc);
	}
	
	@Override
	public boolean accept(SemanticPersistentProperty property) {
		return true;
	}

	@Override
	public List<FieldAccessListener> forField(SemanticPersistentProperty property) {
		for(FieldAccessListenerFactory factory : factories){
			if(factory.accept(property)){
				return factory.forField(property);
			}
		}
		return null;
	}

}
