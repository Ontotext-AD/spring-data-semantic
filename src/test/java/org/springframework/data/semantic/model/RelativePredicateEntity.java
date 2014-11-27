package org.springframework.data.semantic.model;

import org.openrdf.model.URI;
import org.springframework.data.semantic.annotation.Namespace;
import org.springframework.data.semantic.annotation.Predicate;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.annotation.SemanticEntity;

@SemanticEntity
public class RelativePredicateEntity {

	@ResourceId
	private URI id;

	@Predicate("urn:really:absolute")
	private String withAbsolutePredicate;

	@Predicate("relative")
	private String withRelativePredicate;

	/**
	 * @return the id
	 */
	public URI getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(URI id) {
		this.id = id;
	}

	public String getWithAbsolutePredicate() {
		return withAbsolutePredicate;
	}

	public void setWithAbsolutePredicate(String withAbsolutePredicate) {
		this.withAbsolutePredicate = withAbsolutePredicate;
	}

	public String getWithRelativePredicate() {
		return withRelativePredicate;
	}

	public void setWithRelativePredicate(String withRelativePredicate) {
		this.withRelativePredicate = withRelativePredicate;
	}
}
