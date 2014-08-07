package org.springframework.data.semantic.core;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.semantic.support.ValueUtils;
import org.springframework.data.semantic.testutils.Utils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/default-context.xml" })
public class SemanticDatabaseTest {
	@Autowired
	SemanticDatabase sdb;
	
	@Before
	public void prepareTestRepository() {
		Utils.populateTestRepository(sdb);
	}
	
	@Test
	public void testPagingQuery() {
		String source = "SELECT ?o WHERE " +
				"{ <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#Wine> ?p ?o } ORDER BY ?p";
		String orderLimit = " LIMIT 2 OFFSET 2";
		
		List<BindingSet> res1 = null;
		List<BindingSet> res2 = null;
		try {
			res1 = sdb.getQueryResults(source, 2L, 2L);
			res2 = sdb.getQueryResults(source + orderLimit);		
		} catch (Exception e) {
			assertTrue(false);
		}
		
		assertEquals(res1.size(), res2.size());
		for(int i = 0; i < res1.size(); i++) {
			
			String val1 = res1.get(i).getBinding("o").getValue().stringValue();
			String val2 = res2.get(i).getBinding("o").getValue().stringValue();
			assertEquals(val1, val2);
		}
	}
		
	@Test
	public void testDefaultNamespace() {
		try {
			Namespace ns = sdb.getDefaultNamespace();
			assertTrue("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#".equalsIgnoreCase(ns.getName()));
		} catch (RepositoryException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testAddStatement(){
		long count = sdb.count();
		sdb.addStatement(new URIImpl("urn:test:statement"), new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), new URIImpl("unr:type:test-statement"));
		assertEquals(count+1, sdb.count());
	}
	
	@Test
	public void testAddStatements(){
		long count = sdb.count();
		Model model = new LinkedHashModel();
		model.add(new URIImpl("urn:test:statement1"), new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), new URIImpl("unr:type:test-statement"));
		model.add(new URIImpl("urn:test:statement2"), new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), new URIImpl("unr:type:test-statement"));
		sdb.addStatements(model);
		assertEquals(count+2, sdb.count());
	}
}
