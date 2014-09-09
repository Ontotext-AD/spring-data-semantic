package org.springframework.data.semantic.support.repository.query;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;

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
	        	params.put(getParameterName(entry.getKey()), resolveValue(entry.getValue()));
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
		
		private Object resolveValue(Object value){
			if(value instanceof Collection<?> || value.getClass().isArray()){
				Collection<?> values;
				if(value.getClass().isArray()){
					values = Arrays.asList(value);
				}
				else{
					values = (Collection<?>) value;
				}
				if(!values.isEmpty()){
					Object firstValue = values.iterator().next();
					if(operations.getSemanticMappingContext().isSemanticPersistentEntity(firstValue.getClass())){
						SemanticPersistentEntity<?> persistentEntity = operations.getSemanticMappingContext().getPersistentEntity(firstValue.getClass());
						List<URI> ids = new ArrayList<URI>(values.size());
						for(Object o : values){
							ids.add(persistentEntity.getResourceId(o));
						}
						return ids;
					}
				}
				
			}
			else{
				if(operations.getSemanticMappingContext().isSemanticPersistentEntity(value.getClass())){
					SemanticPersistentEntity<?> persistentEntity = operations.getSemanticMappingContext().getPersistentEntity(value.getClass());
	        		return persistentEntity.getResourceId(value);
				}
			}
			return value;
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
