package org.springframework.data.semantic.support.convert.state;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.semantic.convert.access.FieldAccessor;
import org.springframework.data.semantic.convert.access.FieldAccessorProvider;
import org.springframework.data.semantic.convert.access.listener.FieldAccessListener;
import org.springframework.data.semantic.convert.access.listener.FieldAccessListenerProvider;
import org.springframework.data.semantic.convert.state.EntityState;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorProvider;
import org.springframework.data.semantic.support.convert.access.listener.DelegatingFieldAccessListenerFactory;
import org.springframework.data.semantic.support.convert.access.listener.DelegatingFieldAccessListenerProvider;

public class SemanticEntityState<T> implements
		EntityState<T, RDFState> {

	private final T entity;
	//private final Class<? extends T> type;
	private final Map<SemanticPersistentProperty, FieldAccessor> fieldAccessors;
	private final Map<SemanticPersistentProperty, List<FieldAccessListener>> fieldAccessorListeners;
	private RDFState state;
	private final SemanticDatabase semanticDb;
	//private final static Logger log = LoggerFactory.getLogger(SemanticEntityState.class);
	private final FieldAccessorProvider fieldAccessorProvider;
	private final FieldAccessListenerProvider fieldAccessListenerProvider;
	private final SemanticPersistentEntity<T> persistentEntity;
	private final ConversionService conversionService;

	public SemanticEntityState(
			final RDFState underlyingState,
			final SemanticDatabase semanticDatabase,
			final T entity,
			final DelegatingFieldAccessorFactory nodeDelegatingFieldAccessorFactory,
			final DelegatingFieldAccessListenerFactory delegatingFieldAccessListenerFactory,
			SemanticPersistentEntity<T> persistentEntity, 
			ConversionService conversionService) {
		this.entity = entity;
		//this.type = type;
		this.state = underlyingState;
		this.semanticDb = semanticDatabase;
		this.fieldAccessorProvider = new DelegatingFieldAccessorProvider(
				nodeDelegatingFieldAccessorFactory);
		this.fieldAccessListenerProvider = new DelegatingFieldAccessListenerProvider(delegatingFieldAccessListenerFactory);
		this.fieldAccessors = fieldAccessorProvider.provideFieldAccessors(persistentEntity);
		this.fieldAccessorListeners = fieldAccessListenerProvider.provideFieldAccessListeners(persistentEntity);
		this.persistentEntity = persistentEntity;
		this.conversionService = conversionService;
	}

	@Override
	public T getEntity() {
		return entity;
	}

	@Override
	public void setPersistentState(RDFState state) {
		this.state = state;
	}

	@Override
	public Object getDefaultValue(SemanticPersistentProperty property) {
		final FieldAccessor accessor = fieldAccessors.get(property);
		if (accessor == null) {
			return null;
		} else {
			return accessor.getDefaultValue();
		}
	}

	@Override
	public Object getValue(Field field, MappingPolicy mappingPolicy) {
		return getValue(persistentEntity.getPersistentProperty(field.getName()), mappingPolicy);
	}

	@Override
	public Object getValue(SemanticPersistentProperty property,
			MappingPolicy mappingPolicy) {
		final FieldAccessor accessor = fieldAccessors.get(property);
		if (!mappingPolicy.useDirty() && accessor != null) {
			return accessor.getValue(entity, mappingPolicy);
		}
		else {			
			return getValueFromState(property);
		}
	}
	
	private Object getValueFromState(SemanticPersistentProperty property){
		String alias = property.getAliasPredicate();
		URI predicate = new URIImpl(alias);
		Model model = state.getCurrentStatements();
		
		List<Object> values = new LinkedList<Object>();
		Model results = model.filter(null, predicate, null);
		for(Statement st : results){
			values.add(st.getObject().stringValue());
		}
		
		if(property.isCollectionLike()){
			Class<?> componentType = property.getComponentType();
			List<Object> convertedValues = new ArrayList<Object>(values.size());
			if(conversionService.canConvert(String.class, componentType)){
				for(Object o : values){
					convertedValues.add(conversionService.convert(o, componentType));
				}
				return convertedValues;
			}
			else{
				throw new IllegalArgumentException("No converter available for target type "+componentType.getName()+".");
			}
		}
		else{
			return values.isEmpty() ? null : values.get(0);
		}
	}

	@Override
	public boolean isWritable(SemanticPersistentProperty property) {
		FieldAccessor fieldAccessor = fieldAccessors.get(property);
		if (fieldAccessor != null) {
			return fieldAccessor.isWritable(entity);
		} else {
			return true;
		}
	}

	@Override
	public Object setValue(Field field, Object newVal,
			MappingPolicy mappingPolicy) {
		return setValue(persistentEntity.getPersistentProperty(field.getName()), newVal, mappingPolicy);
	}

	@Override
	public Object setValue(SemanticPersistentProperty property, Object newVal,
			MappingPolicy mappingPolicy) {
		final FieldAccessor accessor = fieldAccessors.get(property);
		final Object oldValue = getValue(property, mappingPolicy);
        final Object result=accessor!=null ? accessor.setValue(entity, newVal, mappingPolicy) : newVal;
        notifyListeners(property, oldValue, result);
        return result;
	}
	
	 private void notifyListeners(final SemanticPersistentProperty field, final Object oldValue, final Object newValue) {
        if (!fieldAccessorListeners.containsKey(field) || fieldAccessorListeners.get(field) == null) return;
        for (final FieldAccessListener listener : fieldAccessorListeners.get(field)) {
            listener.valueChanged(entity, oldValue, newValue); // todo oldValue
        }
	}

	@Override
	public void createAndAssignState() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasPersistentState() {
		return state != null;
	}

	@Override
	public RDFState getPersistentState() {
		return state;
	}

	@Override
	public T persist() {
		semanticDb.removeStatement(state.getDeleteStatements());
		state.getDeleteStatements().clear();
		semanticDb.addStatements(state.getCurrentStatements());
		return entity;
	}

	@Override
	public SemanticPersistentEntity<T> getPersistentEntity() {
		return persistentEntity;
	}

}
