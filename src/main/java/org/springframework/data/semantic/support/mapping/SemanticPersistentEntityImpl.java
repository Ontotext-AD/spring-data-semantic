package org.springframework.data.semantic.support.mapping;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.semantic.annotation.Namespace;
import org.springframework.data.semantic.annotation.SemanticEntity;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.MappingPolicyImpl;
import org.springframework.data.semantic.support.util.ValueUtils;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.StringUtils;

/**
 * 
 * @author konstantin.pentchev
 *
 * @param <T>
 */

public class SemanticPersistentEntityImpl<T> extends BasicPersistentEntity<T, SemanticPersistentProperty> implements SemanticPersistentEntity<T>{
	private SemanticPersistentProperty contextProperty;
	private SemanticMappingContext mappingContext;
	private URI rdfType;
	private URI namespace;
	private boolean hasNamespace = true;
	
	
	public SemanticPersistentEntityImpl(TypeInformation<T> typeInformation) {
		this(typeInformation, null);
	}

	public SemanticPersistentEntityImpl(TypeInformation<T> typeInformation,
			SemanticMappingContext semanticMappingContext) {
		super(typeInformation);
		this.mappingContext = semanticMappingContext;
	}

	@Override
	public MappingPolicy getMappingPolicy() {
		return MappingPolicyImpl.DEFAULT_POLICY;
	}

	@Override
	public SemanticPersistentProperty getContextProperty() {
		return contextProperty;
	}
	
	@Override
	public void addPersistentProperty(SemanticPersistentProperty property) {
		if(property.isContext()){
			contextProperty = property;
		}
		super.addPersistentProperty(property);
	}

	@Override
	public URI getRDFType() {
		if(rdfType == null) {
			SemanticEntity seAnnotation = getType().getAnnotation(SemanticEntity.class);
			String type = seAnnotation.rdfType();
			if(StringUtils.hasText(type)){
				rdfType = mappingContext.resolveURI(type);
			}
			else{
				URI ns = getNamespace();
				if(ns == null){
					rdfType = mappingContext.resolveURIDefaultNS(getType().getSimpleName());
				}
				else{
					rdfType = ValueUtils.createUri(ns.stringValue(), getType().getSimpleName());
				}
			}
		}
		return rdfType;
	}

	@Override
	public void setPersistentState(Object entity, RDFState statements) {
		URI subjectId = (URI) statements.getCurrentStatements().filter(null, RDF.TYPE, getRDFType()).subjects().iterator().next();
		setResourceId(entity, subjectId);
	}

	@Override
	public URI getResourceId(Object entity) {
		Object value = getIdProperty().getValue(entity, null);
		if(value instanceof URI){
			return (URI) value;
		}
		URI ns = getNamespace();
		if(ns != null){
			return ValueUtils.createUri(ns.stringValue(), value.toString());
		}
		else{
			return mappingContext.resolveURI(value.toString());
		}
	}

	@Override
	public boolean hasContextProperty() {
		return contextProperty != null;
	}

	@Override
	public void setResourceId(Object entity, URI id) {
		SemanticPersistentProperty idProperty = getIdProperty();
		Class<?> propertyType = idProperty.getActualType();
		if(URI.class.isAssignableFrom(propertyType)){
			idProperty.setValue(entity, id);
		}
		else{
			idProperty.setValue(entity, id.getLocalName());
		}
	}

	@Override
	public URI getNamespace() {
		if(namespace == null && hasNamespace) {
			Namespace nsAnnotation = getType().getAnnotation(Namespace.class);
			if(nsAnnotation != null){
				String ns = nsAnnotation.namespace();
				if(StringUtils.hasText(ns)){
					namespace = ValueUtils.createUri(ns);
				}
			}
			else{
				hasNamespace = false;
			}
		}
		return namespace;
	}

	@Override
	public Resource getContext(Object entity) {
		if(hasContextProperty()){
			return (Resource) getContextProperty().getValue(entity, getMappingPolicy());
		}
		return null;
	}

}
