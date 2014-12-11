package org.springframework.data.semantic.model;

import org.joda.time.DateTime;
import org.springframework.data.semantic.annotation.*;

@Namespace(namespace = Ontology.DEMO_NAMESPACE)
@SemanticEntity(rdfType = Ontology.CLASS_PERSON)
public class Person extends Concept {

	@Optional
	@Predicate(Ontology.PERSON_BIRTHDATE)
	DateTime birthdate;

	public DateTime getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(DateTime birthdate) {
		this.birthdate = birthdate;
	}
}
