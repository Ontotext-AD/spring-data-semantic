package org.springframework.data.semantic.support;

import org.junit.Test;
import org.openrdf.model.Namespace;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.semantic.annotation.Predicate;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.model.RelativePredicateEntity;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for @Predicate annotation on classes with no @Namespace annotation
 * The cases with @Namespace are in TestNamespaceAnnotation
 * Created by itrend on 11/27/14.
 */
public class TestRelativePredicateAnnotation {
	private static final String DEFAULT_NS = "urn:default:namespace:";
	private SemanticMappingContext
			mappingContext = new SemanticMappingContext((List<? extends Namespace>) new LinkedList<Namespace>(), new NamespaceImpl("", DEFAULT_NS));

	@SuppressWarnings("unchecked")
	private SemanticPersistentEntity<RelativePredicateEntity> pe =
			(SemanticPersistentEntity<RelativePredicateEntity>) mappingContext.getPersistentEntity(RelativePredicateEntity.class);


	@Test
	public void testRelativePredicateWithNoNamespace() {
		assertEquals(
				new URIImpl(DEFAULT_NS + "relative"),
				pe.getPersistentProperty("withRelativePredicate").getPredicate());
	}

	@Test
	public void testAbsolutePredicateWithNoNamespace() throws NoSuchFieldException {
		assertEquals(
				new URIImpl("urn:really:absolute"),
				pe.getPersistentProperty("withAbsolutePredicate").getPredicate());
	}

}
