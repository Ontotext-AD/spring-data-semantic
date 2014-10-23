package org.springframework.data.semantic.convert;

import org.springframework.data.semantic.mapping.SemanticPersistentEntity;

public interface SemanticEntityRemover {
	
	<T> void delete(SemanticPersistentEntity<T> persistentEntity, T entity); 
	
	<T> void deleteAll(SemanticPersistentEntity<T> persistentEntity);

}
