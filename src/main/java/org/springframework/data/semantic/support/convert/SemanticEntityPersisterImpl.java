package org.springframework.data.semantic.support.convert;

import org.springframework.data.semantic.convert.SemanticEntityConverter;
import org.springframework.data.semantic.convert.SemanticEntityPersister;
import org.springframework.data.semantic.core.StatementsIterator;
import org.springframework.data.semantic.mapping.MappingPolicy;

public class SemanticEntityPersisterImpl implements SemanticEntityPersister{
	
	private SemanticEntityConverter entityConverter;
	
	public SemanticEntityPersisterImpl(SemanticEntityConverter entityConverter){
		this.entityConverter = entityConverter;
	}
	

	@Override
	public <T> T createEntityFromState(StatementsIterator statements,
			Class<T> type, MappingPolicy mappingPolicy) {
		if (statements == null || !statements.hasNext()) {
            return null;
        }
		//TODO what is the purpose of the mappingPolicy object?
		return entityConverter.read(type, statements); 
		//TODO add at this point caching of entities		
	}

}
