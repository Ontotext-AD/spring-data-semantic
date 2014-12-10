/**
 * Copyright (C) 2014 Ontotext AD (info@ontotext.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
