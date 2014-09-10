package org.springframework.data.semantic.support.model;

import org.openrdf.model.URI;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.annotation.SemanticEntity;

@SemanticEntity
public class SemanticResource {
	
	@ResourceId
	public URI id;

	public URI getId() {
		return id;
	}

	public void setId(URI id) {
		this.id = id;
	}
	
	

}
