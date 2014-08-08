package org.springframework.data.semantic.core;

import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface SemanticOperationsStatementsCollector {
	
	Model getStatementsForResourceProperty(Object entity,
			SemanticPersistentProperty property);

	Model getStatementsForResource(URI resource, Class<?> clazz);
	
	Collection<Model> getStatementsForResources(Class<?> clazz);
	
	Collection<Model> getStatementsForResources(Class<?> clazz, Long offset, Long limit);
}
