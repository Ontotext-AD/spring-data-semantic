package org.springframework.data.semantic.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.semantic.model.TestEntity;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.util.ClassTypeInformation;

public class SemanticPersistentEntityTest {
	private SemanticMappingContext mappingContext;
	private SemanticPersistentEntity<?> testEntityType;
	
	
	@Before
	public void setup(){
		mappingContext = new SemanticMappingContext(Arrays.asList(new NamespaceImpl("skos", "http://www.w3.org/2004/02/skos/core#")), new NamespaceImpl("", "urn:default:"));
		testEntityType = mappingContext.getPersistentEntity(ClassTypeInformation.from(TestEntity.class));
	}
	
	@Test
	public void testIdProperty(){
		final SemanticPersistentProperty idProperty = testEntityType.getIdProperty();
		assertNotNull(idProperty);
        assertEquals("uri", idProperty.getName());
	}
	
	@Test
	public void TestPredicateProperty(){
		final SemanticPersistentProperty nameProperty = testEntityType.getPersistentProperty("name");
		assertNotNull(nameProperty);
		assertTrue(nameProperty.hasPredicate());
		assertEquals(new URIImpl("http://www.w3.org/2004/02/skos/core#prefLabel"), nameProperty.getPredicate().get(0));
	}
	
	@Test
	public void TestLanguageProperty(){
		final SemanticPersistentProperty nameProperty = testEntityType.getPersistentProperty("name");
		assertNotNull(nameProperty);
		assertTrue(nameProperty.hasLanguage());
		assertTrue(nameProperty.getLanguage().contains("en"));
		assertTrue(nameProperty.getLanguage().contains("de"));
	}
	
	@Test
	public void TestContextProperty(){
		final SemanticPersistentProperty contextProperty = testEntityType.getContextProperty();
		assertNotNull(contextProperty);
		assertEquals("graph", contextProperty.getName());
	}
	
	@Test
	public void TestRdfType(){
		URI rdfType = testEntityType.getRDFType();
		assertEquals(new URIImpl("urn:default:TestEntity"), rdfType);
	}

}
