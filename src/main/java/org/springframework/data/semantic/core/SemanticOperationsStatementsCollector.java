package org.springframework.data.semantic.core;

import java.util.Collection;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface SemanticOperationsStatementsCollector {
	
	<T> Model getStatementsForResourceProperty(T entity, SemanticPersistentProperty property);
	
	<T> Long getCountForResource(Class<? extends T> clazz);
	
	<T> Long getCountForResourceAndProperties(Class<? extends T> clazz, Map<String, Object> parameterToValue);

	<T> Model getStatementsForResourceOriginalPredicates(URI resource, Class<? extends T> clazz, MappingPolicy globalMappingPolicy);
	
	<T> Model getStatementsForResource(URI resource, Class<? extends T> clazz, MappingPolicy globalMappingPolicy);
	
	<T> Collection<Model> getStatementsForResources(Class<? extends T> clazz);
	
	<T> Collection<Model> getStatementsForResources(Class<? extends T> clazz, Long offset, Long limit);
	
	<T> Collection<Model> getStatementsForResourcesAndProperties(Class<? extends T> clazz, Map<String, Object> parameterToValue, Long offset, Long limit);
} 
