package org.springframework.data.semantic.support.convert.state;

import org.springframework.data.semantic.convert.state.EntityState;
import org.springframework.data.semantic.convert.state.EntityStateFactory;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.access.listener.DelegatingFieldAccessListenerFactory;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class SemanticEntityStateFactory implements EntityStateFactory<RDFState>{
	
	private SemanticMappingContext mappingContext;
	private DelegatingFieldAccessorFactory delegatingFieldAccessorFactory;
	private DelegatingFieldAccessListenerFactory delegatingFieldAccessListenerFactory;
	private SemanticDatabase semanticDatabase;
	
	public SemanticEntityStateFactory(SemanticMappingContext mappingContext, DelegatingFieldAccessorFactory delegatingFieldAccessorFactory, DelegatingFieldAccessListenerFactory delegatingFieldAccessListenerFactory, SemanticDatabase semanticDatabase){
		this.delegatingFieldAccessorFactory = delegatingFieldAccessorFactory;
		this.delegatingFieldAccessListenerFactory = delegatingFieldAccessListenerFactory;
		this.mappingContext = mappingContext;
		this.semanticDatabase = semanticDatabase;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> EntityState<R, RDFState> getEntityState(R entity,
			boolean detachable) {
		final Class<?> entityType = entity.getClass();
		SemanticPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(entityType);
		return new SemanticEntityState<R>(new RDFState(), semanticDatabase, entity, (Class<R>) entityType, delegatingFieldAccessorFactory, delegatingFieldAccessListenerFactory, (SemanticPersistentEntity<R>) persistentEntity);
	}
	
}
