package org.springframework.data.semantic.support.repository.query;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;

public class SemanticQueryMethod extends QueryMethod {

	public SemanticQueryMethod(Method method, RepositoryMetadata metadata) {
		super(method, metadata);
	}
	
	public RepositoryQuery createQuery(SemanticOperationsCRUD operations){
		return new SemanticRepositoryQuery(operations, this);
		
	}
	
	private class SemanticRepositoryQuery implements RepositoryQuery {
		
		private SemanticOperationsCRUD operations;
		private SemanticQueryMethod queryMethod;
		
		public SemanticRepositoryQuery(SemanticOperationsCRUD operations, SemanticQueryMethod queryMethod) {
			this.operations = operations;
			this.queryMethod = queryMethod;
		}

		@Override
		public Object execute(Object[] parameters) {
			final ParameterAccessor accessor = new ParametersParameterAccessor(queryMethod.getParameters(), parameters);
			Map<String, Object> params = resolveParameters(getParameterValues(accessor));
			return operations.findByProperty(this.queryMethod.getDomainClass(), params);
		}

		@Override
		public QueryMethod getQueryMethod() {
			return this.queryMethod;
		}
		/*
		public Map<Parameter, Object> resolveParameters(Map<Parameter, Object> parameters) {
	        Map<Parameter, Object> result=new LinkedHashMap<Parameter, Object>();
	        for (Map.Entry<Parameter, Object> entry : parameters.entrySet()) {
	            result.put(entry.getKey(), entry.getValue());
	        }
	        return result;
	    }*/
		
		private Map<String, Object> resolveParameters(Map<Parameter, Object> parameters) {
	        Map<String, Object> params = new HashMap<String, Object>();
	        for (Map.Entry<Parameter, Object> entry : parameters.entrySet()) {
	            params.put(getParameterName(entry.getKey()), entry.getValue());
	        }
	        return params;
	    }
		
		private String getParameterName(Parameter parameter) {
	        final String parameterName = parameter.getName();
	        if (parameterName != null) {
	            return parameterName;
	        }
	        String methodName = this.queryMethod.getName();
	        String methodNameParams = methodName.substring(methodName.indexOf("By")+2);
	        String[] paramNames = methodNameParams.split("And|Or");
	        return paramNames[parameter.getIndex()].toLowerCase();
	    }

	    private Map<Parameter, Object> getParameterValues(ParameterAccessor accessor) {
	        Map<Parameter,Object> parameters=new LinkedHashMap<Parameter, Object>();
	        for (Parameter parameter : getParameters().getBindableParameters()) {
	            final Object value = accessor.getBindableValue(parameter.getIndex());
	            parameters.put(parameter,value);
	        }
	        return parameters;
	    }
		
	}

}
