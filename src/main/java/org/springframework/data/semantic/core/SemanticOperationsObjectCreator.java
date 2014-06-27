package org.springframework.data.semantic.core;

import org.openrdf.model.Model;
import org.openrdf.query.GraphQueryResult;
import org.springframework.data.semantic.mapping.MappingPolicy;

public interface SemanticOperationsObjectCreator {
	/**
	 * Create a target object of the given class from the provided statements in {@link GraphQueryResult}.
	 * @param rdfGraph
	 * @param targetClazz
	 * @param mappingPolicy
	 * @return
	 */
	<T> T createObjectFromStatements(
			Model rdfGraph, Class<T> targetClazz, MappingPolicy mappingPolicy);
}
