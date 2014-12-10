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
package org.springframework.data.semantic.mapping;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.support.convert.access.MockFieldAccessorFactory;
import org.springframework.data.semantic.support.convert.access.listener.MockFieldAccessListenerFactory;
import org.springframework.data.semantic.support.convert.state.SemanticEntityState;
import org.springframework.data.semantic.support.mapping.SemanticPersistentEntityImpl;
import org.springframework.data.semantic.support.util.ValueUtils;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/default-context.xml" })
public class TestSemanticEntityState {
	
	private SemanticEntityState<?> entityState;
	
	@Autowired
	private SemanticDatabase sdb;
	
	@Before
	public void setUp(){
		entityState = new SemanticEntityState<Object>(null, sdb, new Object(), new MockFieldAccessorFactory(), new MockFieldAccessListenerFactory(), new SemanticPersistentEntityImpl<Object>(ClassTypeInformation.from(Object.class)), new DefaultConversionService());
	}
	
	@Test
	public void testPersist(){
		long count = sdb.count();
		
		RDFState statements = new RDFState();
		statements.addStatement(new StatementImpl(new URIImpl("urn:test:entity-state-1"), new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), new URIImpl("unr:type:test-statement")));
		entityState.setPersistentState(statements);
		entityState.persist();
		assertEquals(count+1, sdb.count());
	}

}
