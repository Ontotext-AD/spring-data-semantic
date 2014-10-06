package org.springframework.data.semantic.support.repository.query;

import java.util.Map;

import org.springframework.data.repository.query.Parameters;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;

public class CountSemanticRepositoryQuery extends AbstractSemanticRepositoryQuery{
	
	private final static String PREFIX = "By";

	public CountSemanticRepositoryQuery(SemanticOperationsCRUD operations,
			String methodName, Class<?> domainClass, Parameters<?, ?> parameters) {
		super(operations, methodName, domainClass, parameters);
	}

	@Override
	public Object doExecute(Map<String, Object> params) {
		return this.operations.countByProperty(this.domainClass, params);
	}

	@Override
	public String getPrefix() {
		return PREFIX;
	}

}
