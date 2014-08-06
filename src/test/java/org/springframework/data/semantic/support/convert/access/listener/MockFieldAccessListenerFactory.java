package org.springframework.data.semantic.support.convert.access.listener;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.semantic.convert.access.listener.FieldAccessListener;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public class MockFieldAccessListenerFactory extends DelegatingFieldAccessListenerFactory{

	public MockFieldAccessListenerFactory() {
		super(null, null);
	}

	@Override
	public boolean accept(SemanticPersistentProperty property) {
		return true;
	}

	@Override
	public List<FieldAccessListener> forField(SemanticPersistentProperty property) {
		List<FieldAccessListener> listeners = new ArrayList<FieldAccessListener>(1);
		listeners.add(new MockFieldAccessListener());
		return listeners;
	}

}
