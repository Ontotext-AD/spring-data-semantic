package org.springframework.data.semantic.model;

import org.springframework.data.semantic.annotation.*;
import org.springframework.data.semantic.support.Cascade;

@Namespace(namespace = Ontology.DEMO_NAMESPACE)
@SemanticEntity(rdfType = Ontology.CLASS_ORGANIZATION)
public class Organization extends Concept {
	@Optional
	@Fetch(Cascade.ALL)
	@Predicate(Ontology.ORGANIZATION_PARENT_ORGANIZATION)
	Organization parentOrganization;

	public Organization getParentOrganization() {
		return parentOrganization;
	}

	public void setParentOrganization(Organization parentOrganization) {
		this.parentOrganization = parentOrganization;
	}
}
