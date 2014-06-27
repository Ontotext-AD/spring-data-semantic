package org.springframework.data.semantic.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
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
import org.openrdf.query.GraphQueryResult;
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
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.model.Merlot;
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
	private String expBody;
	private String expFlavor;
	private String expMaker;
	private String expSugar;
	private String expLocation;
	private int expYear;

	@Autowired
	SemanticDatabase sdb;
		
	Namespace defaultNs;
	List<Namespace> namespaces;
	
	SemanticMappingContext mappingContext;
	SemanticPersistentEntity<?> testEntityType;
	
	//these are not autowired because they depend on a configured repository
	SemanticTemplateObjectCreator objectCreator;
	SemanticTemplateStatementsCollector statementsCollector;


	@Before
	public void prepare() throws RepositoryException {
		//populate repository with test data in order to have repeatable results
		Utils.populateTestRepository(sdb);	
		defaultNs = sdb.getDefaultNamespace();
		namespaces = sdb.getNamespaces();
		
		expectedStatements = new HashMap<String, Statement>();
				
		expectedStatements.put("urn:field:flavor",
				new StatementImpl(
						new URIImpl(defaultNs.getName()+"LongridgeMerlot"), 
						new URIImpl("urn:field:flavor"), 
						new URIImpl(defaultNs.getName()+"Moderate")));
		
		/*expectedStatements.put("urn:field:body",
				new StatementImpl(
						new URIImpl(defaultNs.getName()+"LongridgeMerlot"), 
						new URIImpl("urn:field:body"), 
						new URIImpl(defaultNs.getName()+"Light")));*/
		
		expectedStatements.put("urn:field:maker",
				new StatementImpl(
						new URIImpl(defaultNs.getName()+"LongridgeMerlot"), 
						new URIImpl("urn:field:maker"), 
						new URIImpl(defaultNs.getName()+"Longridge")));
		
		expectedStatements.put("urn:field:sugar",
				new StatementImpl(
						new URIImpl(defaultNs.getName()+"LongridgeMerlot"), 
						new URIImpl("urn:field:sugar"), 
						new URIImpl(defaultNs.getName()+"Dry")));
		
		expectedStatements.put("urn:field:location",
				new StatementImpl(
						new URIImpl(defaultNs.getName()+"LongridgeMerlot"), 
						new URIImpl("urn:field:location"), 
						new URIImpl(defaultNs.getName()+"NewZealandRegion")));
		
		expectedStatements.put("urn:field:year",
				new StatementImpl(
						new URIImpl(defaultNs.getName()+"LongridgeMerlot"), 
						new URIImpl("urn:field:year"), 
						new IntegerMemLiteral(null, BigInteger.valueOf(1998), XMLSchema.POSITIVE_INTEGER)));
		
		expBody = "Light";
		expFlavor = defaultNs.getName() + "Moderate";
		expMaker = defaultNs.getName() + "Longridge";
		expSugar = defaultNs.getName() + "Dry";
		expLocation = defaultNs.getName() + "NewZealandRegion";
		expYear = 1998;
		
		mappingContext = new SemanticMappingContext(namespaces, defaultNs);
		testEntityType = mappingContext.getPersistentEntity(ClassTypeInformation.from(Merlot.class));
		ConversionService conversionService = new DefaultConversionService();
		objectCreator = new SemanticTemplateObjectCreator(sdb, conversionService);
		statementsCollector = new SemanticTemplateStatementsCollector(sdb, conversionService, mappingContext);
		
	}
	
	@Test
	public void testStatementsIterator() throws QueryEvaluationException, RepositoryException, QueryCreationException, MalformedQueryException {

		Iterator<Statement> iter = getTestStatements().iterator();
		while(iter.hasNext()) {
			Statement statement = iter.next();
			Statement expStatement = expectedStatements.remove(statement.getPredicate().toString());
			assertEquals(expStatement, statement);
		}
		assertTrue(expectedStatements.isEmpty());
	}

	@Test
	public void testReadEntity() throws QueryInterruptedException, RepositoryException, QueryCreationException, QueryEvaluationException, MalformedQueryException { 
		Model iterator = getTestStatements();
		
		Merlot res = objectCreator.createObjectFromStatements(iterator, Merlot.class, null);
		
		assertEquals(expBody, res.getBody().getLabel());
		assertEquals(expFlavor, res.getFlavor());
		assertEquals(expMaker, res.getMaker());
		assertEquals(expSugar, res.getSugar());
		assertEquals(expLocation, res.getLocation());
		assertEquals(expYear, res.getYear());
	}

	private Model getTestStatements() throws QueryInterruptedException, RepositoryException, QueryCreationException, QueryEvaluationException, MalformedQueryException {
		
		URI resource = new URIImpl("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#LongridgeMerlot");
		String queryStr = EntityToGraphQueryConverter.getGraphQueryForResource(resource, testEntityType);

		GraphQueryResult result = sdb.getStatementsForGraphQuery(queryStr);
		Model statements =  statementsCollector.getStatementsForResourceClass(resource, Merlot.class);
		return statements;
		
		
	}

}
