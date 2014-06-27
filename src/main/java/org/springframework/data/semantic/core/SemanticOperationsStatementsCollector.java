package org.springframework.data.semantic.core;

import org.openrdf.model.URI;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface SemanticOperationsStatementsCollector {
	StatementsIterator getStatementsForResourceProperty(Object entity, SemanticPersistentProperty property);
	StatementsIterator getStatementsForResourceClass(URI resource, Class<?> clazz);
}
