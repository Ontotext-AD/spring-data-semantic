package org.springframework.data.semantic.convert.access.listener;

import java.util.List;
import java.util.Map;

import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface FieldAccessListenerProvider {

	Map<SemanticPersistentProperty, List<FieldAccessListener>> provideFieldAccessListeners(SemanticPersistentEntity<?> entity);

	
}
