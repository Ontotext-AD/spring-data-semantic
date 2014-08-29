package org.springframework.data.semantic.support.convert;

import org.springframework.data.semantic.convert.SemanticEntityRemover;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;

public class SemanticEntityRemoverImpl implements SemanticEntityRemover {
	
	private SemanticDatabase semanticDb;
	private EntityToStatementsConverter toStatementsConverter;
	
	public SemanticEntityRemoverImpl(SemanticDatabase semanticDb, EntityToStatementsConverter toStatementsConverter) {
		this.semanticDb = semanticDb;
		this.toStatementsConverter = toStatementsConverter;
	}

	@Override
	public <T> void delete(SemanticPersistentEntity<T> persistentEntity,
			T entity) {
		
		RDFState state = this.toStatementsConverter.convertEntityToDeleteStatements(persistentEntity, entity);
		this.semanticDb.removeStatement(state.getDeleteStatements());
	}


}
