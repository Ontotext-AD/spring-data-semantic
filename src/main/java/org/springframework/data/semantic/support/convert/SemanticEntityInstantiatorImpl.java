package org.springframework.data.semantic.support.convert;

import org.openrdf.model.Model;
import org.springframework.data.convert.EntityInstantiator;
import org.springframework.data.convert.ReflectionEntityInstantiator;
import org.springframework.data.semantic.convert.SemanticEntityInstantiator;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;

public class SemanticEntityInstantiatorImpl implements SemanticEntityInstantiator{

	private EntityInstantiator instantiator = ReflectionEntityInstantiator.INSTANCE;

	@Override
	public <T> T createInstanceFromState(SemanticPersistentEntity<T> entity,
			Model statements) {
		T instance = instantiator.createInstance(entity, null);
		entity.setPersistentState(instance, statements);
		return instance;
	}

}
