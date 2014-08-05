package org.springframework.data.semantic.support.convert.state;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Statement;
import org.springframework.data.semantic.convert.fieldaccess.FieldAccessor;
import org.springframework.data.semantic.convert.fieldaccess.FieldAccessorProvider;
import org.springframework.data.semantic.convert.state.EntityState;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.access.DelegatingFieldAccessorProvider;

public class SemanticEntityState<T> implements
		EntityState<T, RDFState> {

	private final T entity;
	//private final Class<? extends T> type;
	private final Map<SemanticPersistentProperty, FieldAccessor> fieldAccessors;
	private RDFState state;
	private final SemanticDatabase semanticDb;
	//private final static Logger log = LoggerFactory.getLogger(SemanticEntityState.class);
	private final FieldAccessorProvider fieldAccessorProvider;
	private final SemanticPersistentEntity<T> persistentEntity;
	

	public SemanticEntityState(
			final RDFState underlyingState,
			final SemanticDatabase semanticDatabase,
			final T entity,
			final Class<? extends T> type,
			final DelegatingFieldAccessorFactory nodeDelegatingFieldAccessorFactory,
			SemanticPersistentEntity<T> persistentEntity) {
		this.entity = entity;
		//this.type = type;
		this.state = underlyingState;
		this.semanticDb = semanticDatabase;
		this.fieldAccessorProvider = new DelegatingFieldAccessorProvider(
				nodeDelegatingFieldAccessorFactory);
		this.fieldAccessors = fieldAccessorProvider
				.provideFieldAccessors(persistentEntity);
		this.persistentEntity = persistentEntity;
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
			return getValueFromState(property.getAliasPredicate());
		}
	}
	
	private Object getValueFromState(String alias){
		Iterator<Statement> stIterator = state.getCurrentStatements().iterator();
		List<Object> values = new LinkedList<Object>();
		//TODO optimize: do not iterate through all the statements each time a value is needed
		while(stIterator.hasNext()){
			Statement st = stIterator.next();
			if(alias.equals(st.getPredicate().toString())){
				values.add(st.getObject().stringValue());
			}
		}
		return values;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setValue(SemanticPersistentProperty property, Object newVal,
			MappingPolicy mappingPolicy) {
		// TODO Auto-generated method stub
		return null;
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
