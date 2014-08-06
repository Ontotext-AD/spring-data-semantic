package org.springframework.data.semantic.support.convert;

import org.openrdf.model.URI;
import org.springframework.data.convert.EntityInstantiator;
import org.springframework.data.convert.ReflectionEntityInstantiator;
import org.springframework.data.semantic.convert.SemanticEntityInstantiator;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;

public class SemanticEntityInstantiatorImpl implements SemanticEntityInstantiator{

	private EntityInstantiator instantiator = ReflectionEntityInstantiator.INSTANCE;

	@Override
	public <T> T createInstanceFromState(SemanticPersistentEntity<T> entity,
			RDFState statements) {
		T instance = instantiator.createInstance(entity, null);
		entity.setPersistentState(instance, statements);
		return instance;
	}

	@Override
	public <T> T createInstance(SemanticPersistentEntity<T> entity, URI id) {
		T instance = instantiator.createInstance(entity, null);
		entity.setResourceId(instance, id);
		return instance;
	}

}
