package org.springframework.data.semantic.convert.access;

import java.util.Map;

import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface FieldAccessorProvider {
	
	Map<SemanticPersistentProperty, FieldAccessor> provideFieldAccessors(SemanticPersistentEntity<?> entity);
	
	
}
