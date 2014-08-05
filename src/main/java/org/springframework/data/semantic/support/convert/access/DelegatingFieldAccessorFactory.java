package org.springframework.data.semantic.support.convert.access;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.semantic.convert.fieldaccess.FieldAccessor;
import org.springframework.data.semantic.convert.fieldaccess.FieldAccessorFactory;
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
