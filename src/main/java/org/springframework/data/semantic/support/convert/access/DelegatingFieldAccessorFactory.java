package org.springframework.data.semantic.support.convert.access;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.semantic.convert.fieldaccess.FieldAccessor;
import org.springframework.data.semantic.convert.fieldaccess.FieldAccessorFactory;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.SemanticTemplateCRUD;
import org.springframework.data.semantic.support.SemanticTemplateObjectCreator;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class DelegatingFieldAccessorFactory implements FieldAccessorFactory{

	private List<FieldAccessorFactory> factories = new LinkedList<FieldAccessorFactory>();
	private SemanticTemplateStatementsCollector statementsCollector;
	private SemanticTemplateObjectCreator objectCreator;
	
	public DelegatingFieldAccessorFactory(SemanticTemplateStatementsCollector statementsCollector, 
			SemanticTemplateObjectCreator objectCreator){
		this.statementsCollector = statementsCollector;
		this.objectCreator = objectCreator;
		createFactories();
	}
	
	private void createFactories(){
		PropertyFieldAccessorFactory pfaf = new PropertyFieldAccessorFactory(statementsCollector);
		AssociationFieldAccessorFactory afaf = new AssociationFieldAccessorFactory(statementsCollector, objectCreator);
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
