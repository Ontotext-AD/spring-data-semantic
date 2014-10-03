package org.springframework.data.semantic.support.repository.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;

public abstract class AbstractSemanticRepositoryQuery implements RepositoryQuery {
	
	protected SemanticOperationsCRUD operations;
	private final String methodName;
	protected final Class<?> domainClass;
	private final Parameters<?, ?> parameters;
	
	public abstract Object doExecute(Map<String, Object> params);
	
	public abstract String getPrefix();
	
	public AbstractSemanticRepositoryQuery(SemanticOperationsCRUD operations, String methodName, Class<?> domainClass, Parameters<?, ?> parameters) {
		this.operations = operations;
		this.methodName = methodName;
		this.domainClass = domainClass;
		this.parameters = parameters;
	}
	
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
        String methodNameParams = methodName.substring(methodName.indexOf(getPrefix())+getPrefix().length());
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
        for (Parameter parameter : this.parameters.getBindableParameters()) {
            final Object value = accessor.getBindableValue(parameter.getIndex());
            parameters.put(parameter,value);
        }
        return parameters;
    }
    
    @Override
	public Object execute(Object[] parameters) {
		final ParameterAccessor accessor = new ParametersParameterAccessor(this.parameters, parameters);
		Map<String, Object> params = resolveParameters(getParameterValues(accessor));
		return doExecute(params);
	}

	@Override
	public QueryMethod getQueryMethod() {
		return null;
	}

}
