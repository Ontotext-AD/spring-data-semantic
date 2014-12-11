package org.springframework.data.semantic.model.builder;

import org.openrdf.model.URI;
import org.springframework.data.semantic.model.Concept;

import java.util.List;

/**
 * Created by nafets on 12/10/14.
 */
public abstract class ConceptBuilder<Type extends Concept> {
	protected ConceptBuilder() {
		// use factory method instead
	}

	protected URI uri;
	protected String description;
	protected String mainLabel;
	protected List<String> labels;

	public ConceptBuilder<Type> withUri(URI uri) {
		this.uri = uri;
		return this;
	}

	public ConceptBuilder<Type> withDescription(String description) {
		this.description = description;
		return this;
	}

	public ConceptBuilder<Type> withMainLabel(String mainLabel) {
		this.mainLabel = mainLabel;
		return this;
	}

	public Type build() {
		if (uri == null || description == null || mainLabel == null) {
			throw new IllegalArgumentException("URI, description and mainLabel should be set, but one of them is null");
		}

		Type concept = newConcept();
		concept.setUri(uri);
		concept.setDescription(description);
		concept.setMainLabel(mainLabel);
		return concept;
	}

	protected abstract Type newConcept();
}
