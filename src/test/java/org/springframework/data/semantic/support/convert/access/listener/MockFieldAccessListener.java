package org.springframework.data.semantic.support.convert.access.listener;

import org.springframework.data.semantic.convert.access.listener.FieldAccessListener;

public class MockFieldAccessListener implements FieldAccessListener{

	@Override
	public void valueChanged(Object entity, Object oldVal, Object newVal) {
		//TODO trace
	}

}
