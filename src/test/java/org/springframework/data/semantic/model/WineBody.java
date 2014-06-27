package org.springframework.data.semantic.model;

import org.openrdf.model.URI;
import org.springframework.data.semantic.annotation.Predicate;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.annotation.SemanticEntity;

@SemanticEntity(rdfType="http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#WineBody")
public class WineBody {
	
	@ResourceId
	private URI uri;
	
	@Predicate("http://www.w3.org/2000/01/rdf-schema#label")
	private String label;
	
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	public URI getUri() {
		return uri;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
