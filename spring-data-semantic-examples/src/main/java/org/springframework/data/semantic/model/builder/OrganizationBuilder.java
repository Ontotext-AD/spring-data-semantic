package org.springframework.data.semantic.model.builder;

import org.springframework.data.semantic.model.Concept;
import org.springframework.data.semantic.model.Organization;

/**
 * Created by nafets on 12/10/14.
 */
public class OrganizationBuilder extends ConceptBuilder<Organization> {

	protected Organization parentOrganization;

	protected OrganizationBuilder() {
		super();
	}

	public OrganizationBuilder aOrganizationBuilder() {
		return new OrganizationBuilder();
	}

	public OrganizationBuilder withParentOrganization(Organization parentOrganization) {
		this.parentOrganization = parentOrganization;
		return this;
	}

	@Override
	protected Organization newConcept() {
		Organization org = new Organization();
		if (parentOrganization != null) {
			org.setParentOrganization(parentOrganization);
		}

		return org;
	}
}
