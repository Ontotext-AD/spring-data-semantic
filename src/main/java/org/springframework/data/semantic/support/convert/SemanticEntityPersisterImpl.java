package org.springframework.data.semantic.support.convert;

import java.util.Map;

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
	}


	@Override
	public <T> T persistEntity(T entity, RDFState dbState) {
		entityConverter.write(entity, dbState);
		return entity;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> Iterable<T> persistEntities(
			Map<T, RDFState> entitiesToExistingState) {
		entityConverter.write((Map<Object, RDFState>) entitiesToExistingState);
		return entitiesToExistingState.keySet();
	}

}
