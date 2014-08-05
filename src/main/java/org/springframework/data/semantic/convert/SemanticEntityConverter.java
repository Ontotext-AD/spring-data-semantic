package org.springframework.data.semantic.convert;

import org.springframework.data.convert.EntityConverter;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface SemanticEntityConverter extends EntityConverter<SemanticPersistentEntity<?>, SemanticPersistentProperty, Object, RDFState> {

	/**
	 * Loads data from state into the properties of the given entities.
	 * @param entity
	 * @param source
	 * @param mappingPolicy
	 * @param persistentEntity
	 * @return
	 */
	public <R> R loadEntity(R entity, RDFState source, MappingPolicy mappingPolicy, SemanticPersistentEntity<R> persistentEntity);
	
}
