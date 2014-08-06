package org.springframework.data.semantic.support.mapping;

import org.openrdf.model.URI;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.semantic.annotation.SemanticEntity;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
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
		return MappingPolicy.DEFAULT_POLICY;
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
				return mappingContext.resolveURI(type);
			}
			else{
				return mappingContext.resolveURIDefaultNS(getType().getSimpleName());
			}
		}
		return rdfType;
	}

	@Override
	public void setPersistentState(Object entity, RDFState statements) {
		SemanticPersistentProperty idProperty = getIdProperty();
		URI subjectId = (URI) statements.getCurrentStatements().subjects().iterator().next();
		idProperty.setValue(entity, subjectId);
	}

	@Override
	public URI getResourceId(Object entity) {
		return (URI) getIdProperty().getValue(entity, null);
	}

	@Override
	public boolean hasContextProperty() {
		return contextProperty != null;
	}

	@Override
	public void setResourceId(Object entity, URI id) {
		SemanticPersistentProperty idProperty = getIdProperty();
		idProperty.setValue(entity, id);
	}

}
