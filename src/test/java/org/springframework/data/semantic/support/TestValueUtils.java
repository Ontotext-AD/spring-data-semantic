package org.springframework.data.semantic.support;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.data.semantic.support.util.ValueUtils;

public class TestValueUtils {
	
	@Test
	public void testIsURI(){
		assertTrue(ValueUtils.isAbsoluteURI("http://ontotext.com/resource/projects/spring-data-semantic"));
		assertTrue(ValueUtils.isAbsoluteURI("http://ontotext.com/resource/projects#spring-data-semantic"));
		assertFalse(ValueUtils.isAbsoluteURI("http://ontotext.com/resource#projects/spring-data-semantic"));
		assertTrue(ValueUtils.isAbsoluteURI("urn:mylocalfile:spring-data-semantic"));
		assertFalse(ValueUtils.isAbsoluteURI("urn:mylocalfile: spring-data-semantic"));
		assertFalse(ValueUtils.isAbsoluteURI("urn:mylocalfile|spring-data-semantic"));
		assertFalse(ValueUtils.isAbsoluteURI("someText"));
		assertFalse(ValueUtils.isAbsoluteURI("skos:prefLabel"));
		assertTrue(ValueUtils.isAbsoluteURI("mailto:peter@example.org"));
	}

}
