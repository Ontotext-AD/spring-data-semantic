package org.springframework.data.semantic.support;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.model.ModelEntity;
import org.springframework.data.semantic.model.ModelEntityCollector;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.util.ClassTypeInformation;

public class TestEntityToGraphQueryConverter {
	private String expectedBindings = "<http://ontotext.com/resource/test> a <urn:sprind-data-semantic:ModelEntity> . <http://ontotext.com/resource/test> <urn:field:name> ?name . <http://ontotext.com/resource/test> <urn:field:related> ?related . ";
	private String expectedPattern = "<http://ontotext.com/resource/test> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:sprind-data-semantic:ModelEntity> . OPTIONAL { <http://ontotext.com/resource/test> <http://www.w3.org/2004/02/skos/core#prefLabel> ?name . } OPTIONAL { <http://ontotext.com/resource/test> <urn:sprind-data-semantic:related> ?related . } ";
	private String expectedQuery = "CONSTRUCT { "+expectedBindings+" } WHERE { "+expectedPattern+"}";
	
	private String expectedBindingsEager = "<http://ontotext.com/resource/test-collection> a <urn:sprind-data-semantic:ModelEntityCollector> . <http://ontotext.com/resource/test-collection> <urn:field:entities> ?entities . ?entities a <urn:sprind-data-semantic:ModelEntity> . ?entities <urn:field:name> ?name . ?entities <urn:field:related> ?related . ";
	private String expectedPatternEager = "<http://ontotext.com/resource/test-collection> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:sprind-data-semantic:ModelEntityCollector> . <http://ontotext.com/resource/test-collection> <urn:sprind-data-semantic:entities> ?entities . ?entities <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:sprind-data-semantic:ModelEntity> . OPTIONAL { ?entities <http://www.w3.org/2004/02/skos/core#prefLabel> ?name . } OPTIONAL { ?entities <urn:sprind-data-semantic:related> ?related . } ";
	private String expectedQueryEager = "CONSTRUCT { "+expectedBindingsEager+" } WHERE { "+expectedPatternEager+"}";
	
	
	private URI resource = new URIImpl("http://ontotext.com/resource/test");
	
	private URI collectionResource = new URIImpl("http://ontotext.com/resource/test-collection");
	
	private SemanticMappingContext mappingContext;
	private SemanticPersistentEntity<?> testEntityType;
	private SemanticPersistentEntity<?> testCollectionType;
	
	private EntityToQueryConverter entityToQueryConverter;
	
	
	@Before
	public void setup(){
		this.mappingContext = new SemanticMappingContext(Arrays.asList(new NamespaceImpl("skos", "http://www.w3.org/2004/02/skos/core#")), new NamespaceImpl("", "urn:sprind-data-semantic:"));
		this.testEntityType = this.mappingContext.getPersistentEntity(ClassTypeInformation.from(ModelEntity.class));
		this.testCollectionType = this.mappingContext.getPersistentEntity(ClassTypeInformation.from(ModelEntityCollector.class));
		this.entityToQueryConverter = new EntityToQueryConverter(this.mappingContext);
	}
	
	@Test
	public void TestGetVar(){
		assertEquals("bay", entityToQueryConverter.getVar(700));
	}
	
	@Test
	public void TestBindingCreation(){
		String queryBindings = entityToQueryConverter.getPropertyBindings(resource, testEntityType);
		assertEquals(expectedBindings, queryBindings);
	}

	@Test
	public void TestPatternCreation(){
		String queryPattern = entityToQueryConverter.getPropertyPatterns(resource, testEntityType);
		assertEquals(expectedPattern, queryPattern);
	}
	
	@Test
	public void TestGraphQueryCreation(){
		String query = entityToQueryConverter.getGraphQueryForResource(resource, testEntityType);
		assertEquals(expectedQuery.replaceAll("\\s+", " "), query.replaceAll("\\s+", " "));
	}
	
	@Test
	public void TestBindingCreationEagerLoad(){
		String queryBindings = entityToQueryConverter.getPropertyBindings(collectionResource, testCollectionType);
		assertEquals(expectedBindingsEager, queryBindings);
	}
	
	@Test
	public void TestPatternCreationEagerLoad(){
		String queryPattern = entityToQueryConverter.getPropertyPatterns(collectionResource, testCollectionType);
		assertEquals(expectedPatternEager, queryPattern);
	}
	
	@Test
	public void TestGraphQueryCreationEagerLoad(){
		String query = entityToQueryConverter.getGraphQueryForResource(collectionResource, testCollectionType);
		assertEquals(expectedQueryEager.replaceAll("\\s+", " "), query.replaceAll("\\s+", " "));
	}
}
