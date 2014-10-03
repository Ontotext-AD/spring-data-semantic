package org.springframework.data.semantic.support.repository.query;

import java.util.Map;

import org.springframework.data.repository.query.Parameters;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;

public class FindSemanticRepositoryQuery extends AbstractSemanticRepositoryQuery{
	
	private static final String PREFIX = "By";

	public FindSemanticRepositoryQuery(SemanticOperationsCRUD operations,
			String queryMethodName, Class<?> domainClass,
			Parameters<?, ?> parameters) {
		super(operations, queryMethodName, domainClass, parameters);
	}

	@Override
	public Object doExecute(Map<String, Object> params) {
		return operations.findByProperty(this.domainClass, params);
	}

	@Override
	public String getPrefix() {
		return PREFIX;
	}

}
