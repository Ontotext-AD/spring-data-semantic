package org.springframework.data.semantic.repository.support;

import org.openrdf.model.URI;
import org.springframework.data.repository.core.support.AbstractEntityInformation;

public class SemanticMetamodelEntityInformation<T> extends AbstractEntityInformation<T, URI> {

	public SemanticMetamodelEntityInformation(Class<T> domainClass) {
		super(domainClass);
	}

	@Override
	public URI getId(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<URI> getIdType() {
		return URI.class;
	}

}
