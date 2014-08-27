package org.springframework.data.semantic.support.mapping;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AbstractPersistentProperty;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.semantic.annotation.Context;
import org.springframework.data.semantic.annotation.Datatype;
import org.springframework.data.semantic.annotation.Fetch;
import org.springframework.data.semantic.annotation.Language;
import org.springframework.data.semantic.annotation.Language.Languages;
import org.springframework.data.semantic.annotation.Optional;
import org.springframework.data.semantic.annotation.Predicate;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.ValueUtils;

/**
 * 
 * @author konstantin.pentchev
 *
 */

public class SemanticPersistentPropertyImpl extends
		AbstractPersistentProperty<SemanticPersistentProperty> implements
		SemanticPersistentProperty {

	private Map<Class<? extends Annotation>, ? extends Annotation> annotations;
	private boolean isIdProperty;
	private SemanticMappingContext mappingContext;
	private String aliasPredicate;

	public SemanticPersistentPropertyImpl(Field field,
			PropertyDescriptor propertyDescriptor,
			PersistentEntity<?, SemanticPersistentProperty> owner,
			SimpleTypeHolder simpleTypeHolder) {
		this(field, propertyDescriptor, owner, simpleTypeHolder, null);
	}
	
	public SemanticPersistentPropertyImpl(Field field,
			PropertyDescriptor propertyDescriptor,
			PersistentEntity<?, SemanticPersistentProperty> owner,
			SimpleTypeHolder simpleTypeHolder, SemanticMappingContext mappingContext) {
		super(field, propertyDescriptor, owner, simpleTypeHolder);
		annotations = extractAnnotations(field);
		isIdProperty = annotations.containsKey(ResourceId.class)
				&& propertyDescriptor.getPropertyType().isAssignableFrom(
						URI.class);
		this.mappingContext = mappingContext;
	}

	private Map<Class<? extends Annotation>, ? extends Annotation> extractAnnotations(Field field) {
		Map<Class<? extends Annotation>, Annotation> extracted = new HashMap<Class<? extends Annotation>, Annotation>();
		for (Annotation annotation : field.getAnnotations()) {
			extracted.put(annotation.annotationType(), annotation);
		}
		return extracted;
	}

	@Override
	public boolean isIdProperty() {
		return isIdProperty;
	}

	@Override
	public boolean hasPredicate() {
		return annotations.containsKey(Predicate.class);
	}

	@Override
	public List<URI> getPredicate() {
		if (hasPredicate()) {
			List<URI> predicates = new LinkedList<URI>();
			for(String defPredicate : getAnnotation(Predicate.class).value()){
				predicates.add(mappingContext.resolveURI(defPredicate));
			}
			return predicates;
		} else {
			if(this.getOwner() instanceof SemanticPersistentEntity){
				SemanticPersistentEntity<?> persistentEntity = (SemanticPersistentEntity<?>) this.getOwner();
				URI namespace = persistentEntity.getNamespace();
				if(namespace != null){
					return Arrays.asList(ValueUtils.createUri(namespace.stringValue(), field.getName()));
				}
			}
			return Arrays.asList(mappingContext.resolveURI(field.getName()));
		}
	}

	@Override
	public boolean hasLanguage() {
		return annotations.containsKey(Language.class);
	}

	@Override
	public List<String> getLanguage() {
		final Language lang = getAnnotation(Language.class);
		if (lang != null) {
			List<String> languages = new LinkedList<String>();
			for (Languages l : lang.value()) {
				languages.add(l.toString());
			}
			return languages;
		}
		return new ArrayList<String>(0);
	}

	@SuppressWarnings("unchecked")
	private <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		return (T) annotations.get(annotationType);
	}

	@Override
	public boolean hasDatatype() {
		return annotations.containsKey(Datatype.class);
	}

	@Override
	public String getDatatype() {
		final Datatype dt = getAnnotation(Datatype.class);
		if (dt != null) {
			return dt.value().toString();
		}
		return null;
	}

	@Override
	protected Association<SemanticPersistentProperty> createAssociation() {
		return new Association<SemanticPersistentProperty>(this, null);
	}

	@Override
	public Object getValue(final Object entity,
			final MappingPolicy mappingPolicy) {
		return getValueFromEntity(entity, mappingPolicy);
	}

	private Object getValueFromEntity(Object entity, MappingPolicy mappingPolicy) {
		try {
			final Field field = getField();
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			return field.get(entity);
		} catch (IllegalAccessException e) {
			throw new MappingException("Could not access field " + field, e);
		}
	}

	@Override
	public void setValue(Object entity, Object newValue) {
		try {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			field.set(entity, newValue);
		} catch (IllegalAccessException e) {
			throw new MappingException("Could not access field " + field
					+ " for setting value " + newValue + " on " + this, e);
		}
	}

	@Override
	public boolean isContext() {
		return annotations.containsKey(Context.class);
	}

	@Override
	public String getAliasPredicate() {
		if(aliasPredicate == null){
			aliasPredicate = "urn:field:"+field.getName();
		}
		return aliasPredicate;
	}

	@Override
	public MappingPolicy getMappingPolicy() {
		if (!annotations.containsKey(Fetch.class)){
            //return MappingPolicy.DEFAULT_POLICY;
			return MappingPolicy.LAZY_LOAD_POLICY;
		}
        else {
        	return MappingPolicy.DEFAULT_POLICY;
        }
	}
	
	@Override
	public boolean shallBePersisted() {
		return super.shallBePersisted() && (getPredicate().size() == 1);
	}

	@Override
	public boolean isVersionProperty() {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A extends Annotation> A findAnnotation(Class<A> annotationType) {
		return (A) annotations.get(annotationType);
	}

	@Override
	public <A extends Annotation> A findPropertyOrOwnerAnnotation(Class<A> annotationType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
		return annotations.containsKey(annotationType);
	}

	@Override
	public boolean isOptional() {
		return annotations.containsKey(Optional.class);
	}
}
