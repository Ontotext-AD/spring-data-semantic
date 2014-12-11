package org.springframework.data.semantic.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.model.Person;
import org.springframework.data.semantic.model.builder.PersonBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext.xml" })
public class PersonRepositoryTest {

	@Autowired
	private SemanticDatabase database;
	
	@Autowired
	private PersonRepository personRepository;
	
	@Before
	public void setUp() throws Exception {
		assertTrue("Database not empty before tests", database.count() == 0);
	}

	@After
	public void tearDown() throws Exception {
		database.clear();
	}

	private Person createObama() {
		return PersonBuilder.aPersonBuilder().withBirthdate(new DateTime(1961, 8, 4, 12, 53, DateTimeZone.UTC)).
		  withUri(new URIImpl("urn:person:Barack_Obama")).withMainLabel("Barack Obama").
		  withDescription("Barack Obama is the president of the United States").build();
	}

	private Person createElizabeth() {
		return PersonBuilder.aPersonBuilder().withBirthdate(new DateTime(1926, 4, 21, 17, 9, DateTimeZone.UTC)).
		  withUri(new URIImpl("urn:person:Elizabeth_II")).withMainLabel("Elizabeth II").
		  withDescription("Elizabeth II (Elizabeth Alexandra Mary) is the constitutional monarch of 16 of the 53 member states in the "
			+ "Commonwealth of Nations. She is also Head of the Commonwealth and Supreme Governor of the Church of England.").build();
	}

	@Test
	public void testAdd() {
		// assign
		Person obama = createObama();

		// act
		personRepository.save(obama);

		// verify
		assertEquals("Database is empty, but there should be 1 entity", database.count(), 1);
	}

	@Test
	public void testFind() {
		// assign
		Person obama = createObama();

		// act
		personRepository.save(obama);
		Person found = personRepository.findOne(obama.getUri());

		// verify
		assertTrue(found.getUri().equals(obama.getUri()));
	}
	
	@Test
	public void testFindOneOfMany() {
		Person obama = createObama();
		Person elizabeth = createElizabeth();
		
		personRepository.save(obama);
		personRepository.save(elizabeth);
		
		List<Person> found = personRepository.findAll(Collections.singletonList(elizabeth.getUri()));
		assertEquals(1, found.size());
		assertTrue(found.get(0).getUri().equals(elizabeth.getUri()));
	}
	
	@Test
	public void testRemove() {
		// assign
		Person obama = createObama();

		// act
		personRepository.save(obama);
		assertEquals(1, personRepository.count());
		personRepository.delete(obama.getUri());

		// verify
		assertEquals(0, personRepository.count());
	}
	
	@Test
	public void testUpdate() {
		Person obama = createObama();
		personRepository.save(obama);

		Person newObama = personRepository.findOne(obama.getUri());
		assertNotNull(newObama);
		
		final String description = "Barack Obama is the chief janitor at Ontotext";
		newObama.setDescription(description);
		personRepository.save(newObama);
		
		Person updatedObama = personRepository.findOne(newObama.getUri());
		assertTrue(description.equals(updatedObama.getDescription()));
	}
}
