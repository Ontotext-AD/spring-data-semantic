package org.springframework.data.semantic.core;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface SemanticOperationsStatementsCollector {
	
	<T> Model getStatementsForResourceProperty(T entity,
			SemanticPersistentProperty property);

	<T> Model getStatementsForResource(URI resource, Class<? extends T> clazz);
	
	<T> Collection<Model> getStatementsForResources(Class<? extends T> clazz);
	
	<T> Collection<Model> getStatementsForResources(Class<? extends T> clazz, Long offset, Long limit);
}
