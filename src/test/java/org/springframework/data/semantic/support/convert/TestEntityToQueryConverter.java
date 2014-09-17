package org.springframework.data.semantic.support.convert;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

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

public class TestEntityToQueryConverter {
	private String expectedBindings = "<http://ontotext.com/resource/test> a <urn:sprind-data-semantic:ModelEntity> . <http://ontotext.com/resource/test> <urn:modelentity:field:name> ?modelentity_name . <http://ontotext.com/resource/test> <urn:modelentity:field:synonyms> ?modelentity_synonyms . <http://ontotext.com/resource/test> <urn:modelentity:field:related> ?modelentity_related . ";
	private String expectedPattern = "<http://ontotext.com/resource/test> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:sprind-data-semantic:ModelEntity> . OPTIONAL { <http://ontotext.com/resource/test> <http://www.w3.org/2004/02/skos/core#prefLabel> ?modelentity_name . } OPTIONAL { <http://ontotext.com/resource/test> <http://www.w3.org/2004/02/skos/core#altLabel> ?modelentity_synonyms . } OPTIONAL { <http://ontotext.com/resource/test> <urn:sprind-data-semantic:related> ?modelentity_related . } ";
	private String expectedQuery = "CONSTRUCT { "+expectedBindings+" } WHERE { "+expectedPattern+"}";
	
	private String expectedBindingsEager = "<http://ontotext.com/resource/test-collection> a <urn:sprind-data-semantic:ModelEntityCollector> . <http://ontotext.com/resource/test-collection> <urn:modelentitycollector:field:entities> ?modelentitycollector_entities . ?modelentitycollector_entities a <urn:sprind-data-semantic:ModelEntity> . ?modelentitycollector_entities <urn:modelentity:field:name> ?modelentity_name . ?modelentitycollector_entities <urn:modelentity:field:synonyms> ?modelentity_synonyms . ?modelentitycollector_entities <urn:modelentity:field:related> ?modelentity_related . ";
	private String expectedPatternEager = "<http://ontotext.com/resource/test-collection> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:sprind-data-semantic:ModelEntityCollector> . <http://ontotext.com/resource/test-collection> <urn:sprind-data-semantic:entities> ?modelentitycollector_entities . ?modelentitycollector_entities <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <urn:sprind-data-semantic:ModelEntity> . OPTIONAL { ?modelentitycollector_entities <http://www.w3.org/2004/02/skos/core#prefLabel> ?modelentity_name . } OPTIONAL { ?modelentitycollector_entities <http://www.w3.org/2004/02/skos/core#altLabel> ?modelentity_synonyms . } OPTIONAL { ?modelentitycollector_entities <urn:sprind-data-semantic:related> ?modelentity_related . } ";
	private String expectedQueryEager = "CONSTRUCT { "+expectedBindingsEager+" } WHERE { "+expectedPatternEager+"}";
	
	
	private URI resource = new URIImpl("http://ontotext.com/resource/test");
	
	private URI collectionResource = new URIImpl("http://ontotext.com/resource/test-collection");
	
	private SemanticMappingContext mappingContext;
	private SemanticPersistentEntity<?> testEntityType;
	private SemanticPersistentEntity<?> testCollectionType;
	
	private EntityToQueryConverter entityToQueryConverter;
	
	private LocaleIndipendentStringComparator comparator = new LocaleIndipendentStringComparator();
	
	
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
		String queryBindings = entityToQueryConverter.getPropertyBindings(resource, testEntityType, new HashMap<String, Object>());
		String[] expected = expectedBindings.replaceAll("\\s+", " ").split(" \\. ");
		String[] resultBindings = queryBindings.replaceAll("\\s+", " ").split(" \\. ");
		Arrays.sort(expected, comparator);
		Arrays.sort(resultBindings, comparator);
		assertArrayEquals(expected, resultBindings);
	}

	@Test
	public void TestPatternCreation(){
		String queryPattern = entityToQueryConverter.getPropertyPatterns(resource, testEntityType, new HashMap<String, Object>());
		String[] expected = expectedPattern.replaceAll("\\{|\\}", " ").replaceAll("\\s+", " ").split(" \\. ");
		String[] resultPattern = queryPattern.replaceAll("\\{|\\}", " ").replaceAll("\\s+", " ").split(" \\. ");
		Arrays.sort(expected, comparator);
		Arrays.sort(resultPattern, comparator);
		System.out.println(Arrays.asList(expected));
		System.out.println(Arrays.asList(resultPattern));
		assertArrayEquals(expected, resultPattern);
	}
	
	@Test
	public void TestGraphQueryCreation(){
		String query = entityToQueryConverter.getGraphQueryForResource(resource, testEntityType);
		String[] expected = expectedQuery.replaceAll("\\{|\\}", " ").replaceAll("\\s+", " ").split(" \\. ");
		String[] resultBindings = query.replaceAll("\\{|\\}", " ").replaceAll("\\s+", " ").split(" \\. ");
		Arrays.sort(expected, comparator);
		Arrays.sort(resultBindings, comparator);
		assertArrayEquals(expected, resultBindings);
	}
	
	@Test
	public void TestBindingCreationEagerLoad(){
		String queryBindings = entityToQueryConverter.getPropertyBindings(collectionResource, testCollectionType, new HashMap<String, Object>());
		String[] expected = expectedBindingsEager.replaceAll("\\s+", " ").split(" \\. ");
		String[] resultBindings = queryBindings.replaceAll("\\s+", " ").split(" \\. ");
		Arrays.sort(expected, comparator);
		Arrays.sort(resultBindings, comparator);
		assertArrayEquals(expected, resultBindings);
	}
	
	@Test
	public void TestPatternCreationEagerLoad(){
		String queryPattern = entityToQueryConverter.getPropertyPatterns(collectionResource, testCollectionType, new HashMap<String, Object>());
		String[] expected = expectedPatternEager.replaceAll("\\{|\\}", " ").replaceAll("\\s+", " ").split(" \\. ");
		String[] resultPattern = queryPattern.replaceAll("\\{|\\}", " ").replaceAll("\\s+", " ").split(" \\. ");
		Arrays.sort(expected, comparator);
		Arrays.sort(resultPattern, comparator);
		assertArrayEquals(expected, resultPattern);
	}
	
	@Test
	public void TestGraphQueryCreationEagerLoad(){
		String query = entityToQueryConverter.getGraphQueryForResource(collectionResource, testCollectionType);
		String[] expected = expectedQueryEager.replaceAll("\\{|\\}", " ").replaceAll("\\s+", " ").split(" \\. ");
		String[] resultBindings = query.replaceAll("\\{|\\}", " ").replaceAll("\\s+", " ").split(" \\. ");
		Arrays.sort(expected);
		Arrays.sort(resultBindings);
		assertArrayEquals(expected, resultBindings);
	}
	
	private class LocaleIndipendentStringComparator implements Comparator<String> {

		private Collator collator = Collator.getInstance(Locale.US);
		
		@Override
		public int compare(String o1, String o2) {
			return this.collator.compare(o1, o2);
		}
		
	}
}
