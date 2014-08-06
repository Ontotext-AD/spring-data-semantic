package org.springframework.data.semantic.support;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.model.TestEntity;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.util.ClassTypeInformation;

public class TestEntityToGraphQueryConverter {
	private String expectedBindings = "<http://ontotext.com/resource/test> <urn:field:name> ?name . <http://ontotext.com/resource/test> <urn:field:related> ?related . ";
	private String expectedPattern = "<http://ontotext.com/resource/test> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:default:TestEntity> . <http://ontotext.com/resource/test> <http://www.w3.org/2004/02/skos/core#prefLabel> ?name . <http://ontotext.com/resource/test> <urn:default:related> ?related . ";
	private String expectedQuery = "CONSTRUCT { "+expectedBindings+" } WHERE { "+expectedPattern+"}";
	private URI resource = new URIImpl("http://ontotext.com/resource/test");
	private SemanticMappingContext mappingContext;
	private SemanticPersistentEntity<?> testEntityType;
	
	
	@Before
	public void setup(){
		mappingContext = new SemanticMappingContext(Arrays.asList(new NamespaceImpl("skos", "http://www.w3.org/2004/02/skos/core#")), new NamespaceImpl("", "urn:default:"));
		testEntityType = mappingContext.getPersistentEntity(ClassTypeInformation.from(TestEntity.class));
	}
	
	@Test
	public void TestGetVar(){
		assertEquals("bay", EntityToGraphQueryConverter.getVar(700));
	}
	
	@Test
	public void TestBindingCreation(){
		String queryBindings = EntityToGraphQueryConverter.getPropertyBindings(resource, testEntityType);
		assertEquals(expectedBindings, queryBindings);
	}

	@Test
	public void TestPatternCreation(){
		String queryPattern = EntityToGraphQueryConverter.getPropertyPatterns(resource, testEntityType);
		assertEquals(expectedPattern, queryPattern);
	}
	
	@Test
	public void TestGraphQueryCreation(){
		String query = EntityToGraphQueryConverter.getGraphQueryForResource(resource, testEntityType);
		assertEquals(expectedQuery.replaceAll("\\s+", " "), query.replaceAll("\\s+", " "));
	}
}
