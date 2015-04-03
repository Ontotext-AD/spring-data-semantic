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

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.memory.model.IntegerMemLiteral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.repository.query.QueryCreationException;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.model.Merlot;
import org.springframework.data.semantic.model.vocabulary.WINE;
import org.springframework.data.semantic.support.convert.EntityToQueryConverter;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.semantic.testutils.Utils;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/*
 * TODO
 * When implemented, add test to check the behavior when the properties are associations, not simple literals
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/default-context.xml" })
public class SemanticEntityCreationTest {

	//------------Expected values------------
	private HashMap<String, Statement> expectedStatements;	
	private URI expBody;
	private String expFlavor;
	private String expMaker;
	private String expSugar;
	private String expLocation;
	private int expYear;

	@Autowired
	SemanticDatabase sdb;

	SemanticMappingContext mappingContext;
	SemanticPersistentEntity<?> testEntityType;
	
	//these are not autowired because they depend on a configured repository
	SemanticOperationsCRUD operations;
	SemanticTemplateStatementsCollector statementsCollector;


	@Before
	public void prepare() throws RepositoryException {
		//populate repository with test data in order to have repeatable results
		Utils.populateTestRepository(sdb);	

		expectedStatements = new HashMap<String, Statement>();
				
		expectedStatements.put("urn:field:flavor",
				new StatementImpl(
						new URIImpl(WINE.NAMESPACE+"LongridgeMerlot"),
						new URIImpl("urn:field:flavor"), 
						new URIImpl(WINE.NAMESPACE+"Moderate")));
		
		/*expectedStatements.put("urn:field:body",
				new StatementImpl(
						new URIImpl(defaultNs.getName()+"LongridgeMerlot"), 
						new URIImpl("urn:field:body"), 
						new URIImpl(defaultNs.getName()+"Light")));*/
		
		expectedStatements.put("urn:field:maker",
				new StatementImpl(
						new URIImpl(WINE.NAMESPACE+"LongridgeMerlot"),
						new URIImpl("urn:field:maker"), 
						new URIImpl(WINE.NAMESPACE+"Longridge")));
		
		expectedStatements.put("urn:field:sugar",
				new StatementImpl(
						new URIImpl(WINE.NAMESPACE+"LongridgeMerlot"),
						new URIImpl("urn:field:sugar"), 
						new URIImpl(WINE.NAMESPACE+"Dry")));
		
		expectedStatements.put("urn:field:location",
				new StatementImpl(
						new URIImpl(WINE.NAMESPACE+"LongridgeMerlot"),
						new URIImpl("urn:field:location"), 
						new URIImpl(WINE.NAMESPACE+"NewZealandRegion")));
		
		expectedStatements.put("urn:field:year",
				new StatementImpl(
						new URIImpl(WINE.NAMESPACE+"LongridgeMerlot"),
						new URIImpl("urn:field:year"), 
						new IntegerMemLiteral(null, BigInteger.valueOf(1998), XMLSchema.POSITIVE_INTEGER)));
		
		expBody = new URIImpl(WINE.NAMESPACE + "Light");
		expFlavor = WINE.NAMESPACE + "Moderate";
		expMaker = WINE.NAMESPACE + "Longridge";
		expSugar = WINE.NAMESPACE + "Dry";
		expLocation = WINE.NAMESPACE + "NewZealandRegion";
		expYear = 1998;
		
		mappingContext = new SemanticMappingContext(sdb.getNamespaces(), sdb.getDefaultNamespace(), true);
		testEntityType = mappingContext.getPersistentEntity(ClassTypeInformation.from(Merlot.class));
		ConversionService conversionService = new DefaultConversionService();
		operations = new SemanticTemplateCRUD(sdb, conversionService, true);
		statementsCollector = new SemanticTemplateStatementsCollector(sdb, mappingContext, new EntityToQueryConverter(mappingContext));
		
	}
	
	@Test
	public void testStatementsIterator() throws QueryEvaluationException, RepositoryException, QueryCreationException, MalformedQueryException {

		/*Iterator<Statement> iter = getTestStatements().iterator();
		while(iter.hasNext()) {
			Statement statement = iter.next();
			Statement expStatement = expectedStatements.remove(statement.getPredicate().toString());
			assertEquals(expStatement, statement);
		}
		assertTrue(expectedStatements.isEmpty());*/
	}

	@Test
	public void testReadEntity() throws QueryInterruptedException, RepositoryException, QueryCreationException, QueryEvaluationException, MalformedQueryException { 
		Model iterator = getTestStatements();
		
		Merlot res = operations.createEntity(iterator, Merlot.class);
		
		assertEquals(expBody, res.getBody().getUri());
		assertEquals(expFlavor, res.getFlavor());
		assertEquals(expMaker, res.getMaker());
		assertEquals(expSugar, res.getSugar());
		assertEquals(expLocation, res.getLocation());
		assertEquals(expYear, res.getYear());
	}

	private Model getTestStatements() throws QueryInterruptedException, RepositoryException, QueryCreationException, QueryEvaluationException, MalformedQueryException {
		URI resource = new URIImpl("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#LongridgeMerlot");
		
		Model statements =  statementsCollector.getStatementsForResource(resource, Merlot.class, MappingPolicyImpl.ALL_POLICY);
		return statements;
		
		
	}

}
