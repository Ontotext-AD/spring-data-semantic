package org.springframework.data.semantic.model.builder;

import org.joda.time.DateTime;
import org.springframework.data.semantic.model.Person;

/**
 * Created by nafets on 12/10/14.
 */
public class PersonBuilder extends ConceptBuilder<Person> {

	protected DateTime birthdate;

	protected PersonBuilder() {
		super();
	}

	public static PersonBuilder aPersonBuilder() {
		return new PersonBuilder();
	}

	public PersonBuilder withBirthdate(DateTime birthdate) {
		this.birthdate = birthdate;
		return this;
	}

	@Override
	protected Person newConcept() {
		Person person = new Person();
		if (birthdate != null) {
			person.setBirthdate(birthdate);
		}

		return person;
	}
}
