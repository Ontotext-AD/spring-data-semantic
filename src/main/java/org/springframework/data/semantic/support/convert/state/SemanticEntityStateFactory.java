package org.springframework.data.semantic.support.convert.state;

import org.openrdf.model.Model;
import org.springframework.data.semantic.convert.state.EntityState;
import org.springframework.data.semantic.convert.state.EntityStateFactory;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorFactory;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class SemanticEntityStateFactory implements EntityStateFactory<Model>{
	
	private SemanticMappingContext mappingContext;
	private DelegatingFieldAccessorFactory delegatingFieldAccessorFactory;
	
	public SemanticEntityStateFactory(SemanticMappingContext mappingContext, DelegatingFieldAccessorFactory delegatingFieldAccessorFactory){
		this.delegatingFieldAccessorFactory = delegatingFieldAccessorFactory;
		this.mappingContext = mappingContext;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> EntityState<R, Model> getEntityState(R entity,
			boolean detachable) {
		final Class<?> entityType = entity.getClass();
		SemanticPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(entityType);
		return new SemanticEntityState<R>(null, entity, (Class<R>) entityType, delegatingFieldAccessorFactory, (SemanticPersistentEntity<R>) persistentEntity);
	}
	
}
