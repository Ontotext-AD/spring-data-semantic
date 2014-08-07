package org.springframework.data.semantic.support.convert.access.listener;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.semantic.convert.access.listener.FieldAccessListener;
import org.springframework.data.semantic.convert.access.listener.FieldAccessListenerFactory;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;

public class AssociationFieldAccessListenerFactory implements FieldAccessListenerFactory{
	
	private final SemanticTemplateStatementsCollector statementsCollector;
	private final SemanticOperationsCRUD operations;
	

	public AssociationFieldAccessListenerFactory(SemanticTemplateStatementsCollector statementsCollector, SemanticOperationsCRUD operations) {
		this.statementsCollector = statementsCollector;
		this.operations = operations;
	}

	@Override
	public boolean accept(SemanticPersistentProperty property) {
		return property.isAssociation();
	}

	@Override
	public List<FieldAccessListener> forField(SemanticPersistentProperty property) {
		List<FieldAccessListener> listeners = new LinkedList<FieldAccessListener>();
		//TODO add actual listeners
		return listeners;
	}
	


}
