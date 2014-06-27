package org.springframework.data.semantic.convert.fieldaccess;

import java.util.Map;

import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface FieldAccessorProvider {
	
	public  Map<SemanticPersistentProperty, FieldAccessor> provideFieldAccessors(SemanticPersistentEntity<?> entity);

}
