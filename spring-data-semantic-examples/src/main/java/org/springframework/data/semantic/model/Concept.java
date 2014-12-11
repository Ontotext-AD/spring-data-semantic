package org.springframework.data.semantic.model;

import org.openrdf.model.URI;
import org.springframework.data.semantic.annotation.*;
import org.springframework.data.semantic.support.Cascade;

import java.util.List;

@Namespace(namespace = Ontology.DEMO_NAMESPACE)
@SemanticEntity(rdfType = Ontology.CLASS_CONCEPT)
public class Concept {

	@ResourceId
	protected URI uri;

	@Predicate(Ontology.MAIN_LABEL)
	protected String mainLabel;

	@Predicate(Ontology.DESCRIPTION)
	protected String description;

	@Optional
	@Predicate(Ontology.RDFS_LABEL)
	protected List<String> labels;

	@Optional
	@Predicate(Ontology.RDF_TYPE)
	protected List<URI> types;

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getDescription() {
		return description;
	}

	public String getMainLabel() {
		return mainLabel;
	}

	public void setMainLabel(String mainLabel) {
		this.mainLabel = mainLabel;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getLabels() {
		return this.labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<URI> getTypes() {
		return this.types;
	}

	public void setTypes(List<URI> types) {
		this.types = types;
	}
}
