package org.springframework.data.semantic.support.convert;

import org.springframework.data.semantic.convert.SemanticEntityConverter;
import org.springframework.data.semantic.convert.SemanticEntityPersister;
import org.springframework.data.semantic.core.RDFState;

public class SemanticEntityPersisterImpl implements SemanticEntityPersister{
	
	private SemanticEntityConverter entityConverter;
	
	public SemanticEntityPersisterImpl(SemanticEntityConverter entityConverter){
		this.entityConverter = entityConverter;
	}
	

	@Override
	public <T> T createEntityFromState(RDFState statements,
			Class<T> type) {
		if (statements.isEmpty()) {
            return null;
        }
		return entityConverter.read(type, statements); 
		//TODO add at this point caching of entities		
	}


	@Override
	public <T> T persistEntity(T entity) {
		entityConverter.write(entity, null);
		return entity;
		//TODO invalidate cache if applicable
	}

}
