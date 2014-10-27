package org.springframework.data.semantic.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.model.ModelEntity;
import org.springframework.data.semantic.support.convert.SemanticEntityInstantiatorImpl;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.util.ClassTypeInformation;

public class TestEntityInstantiator {
	
	private RDFState state;
	private URI id = new URIImpl("urn:default:id1");
	private URI namePredicate = new URIImpl("http://www.w3.org/2004/02/skos/core#prefLabel");
	private Literal name = new LiteralImpl("name");
	private SemanticEntityInstantiator instantiator = new SemanticEntityInstantiatorImpl();
	private SemanticMappingContext mappingContext;
	private SemanticPersistentEntity<ModelEntity> testEntityType;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setupTest(){
		state = new RDFState();
		mappingContext = new SemanticMappingContext(Arrays.asList(new NamespaceImpl("skos", "http://www.w3.org/2004/02/skos/core#")), new NamespaceImpl("", "urn:default:"));
		testEntityType = (SemanticPersistentEntity<ModelEntity>) mappingContext.getPersistentEntity(ClassTypeInformation.from(ModelEntity.class));
		state.addStatement(new StatementImpl(id, namePredicate, name));
		state.addStatement(new StatementImpl(id, RDF.TYPE, testEntityType.getRDFType()));
	}
	
	@Test
	public void testInstantiateDefaultCtor(){
		ModelEntity entity = instantiator.createInstanceFromState(testEntityType, state);
		assertNotNull(entity);
		assertNotNull(entity.getUri());
		assertEquals(id, entity.getUri());
	}

}
