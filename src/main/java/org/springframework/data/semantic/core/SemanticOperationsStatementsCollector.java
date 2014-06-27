package org.springframework.data.semantic.core;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface SemanticOperationsStatementsCollector {
	Model getStatementsForResourceProperty(Object entity,
			SemanticPersistentProperty property);

	Model getStatementsForResourceClass(URI resource, Class<?> clazz);
}
