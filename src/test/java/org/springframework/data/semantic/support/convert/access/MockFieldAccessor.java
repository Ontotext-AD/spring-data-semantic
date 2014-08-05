package org.springframework.data.semantic.support.convert.access;

import org.springframework.data.semantic.convert.fieldaccess.FieldAccessor;
import org.springframework.data.semantic.mapping.MappingPolicy;

public class MockFieldAccessor implements FieldAccessor{

	@Override
	public Object getDefaultValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setValue(Object entity, Object newVal, MappingPolicy mappingPolicy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(Object entity, MappingPolicy mappingPolicy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWritable(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
