package org.springframework.data.semantic.support.convert.access;

import org.springframework.data.semantic.convert.access.FieldAccessor;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public class MockFieldAccessorFactory extends DelegatingFieldAccessorFactory {
	
	public MockFieldAccessorFactory(){
		super(null, null);
	}

	@Override
	public boolean accept(SemanticPersistentProperty property) {
		return true;
	}

	@Override
	public FieldAccessor forField(SemanticPersistentProperty property) {
		return new MockFieldAccessor();
	}

}
