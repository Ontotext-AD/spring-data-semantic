package org.springframework.data.semantic.convert;

import org.openrdf.model.Model;
import org.springframework.data.convert.EntityConverter;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface SemanticEntityConverter extends EntityConverter<SemanticPersistentEntity<?>, SemanticPersistentProperty, Object, Model> {

	/**
	 * Loads data from state into the properties of the given entities.
	 * @param entity
	 * @param source
	 * @param mappingPolicy
	 * @param persistentEntity
	 * @return
	 */
	public <R> R loadEntity(R entity, Model source, MappingPolicy mappingPolicy, SemanticPersistentEntity<R> persistentEntity) ;

	public <R> R read(Class<R> type, Model source, MappingPolicy mappingPolicy);
	
}
