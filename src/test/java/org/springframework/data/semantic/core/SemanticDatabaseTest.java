package org.springframework.data.semantic.core;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Namespace;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
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
	public void testCountQuery() {
		int expected = 2;
		String source = "SELECT ?s WHERE " +
				"{ ?s ?p ?o } LIMIT "+ expected;
		
		Long res = -1l;
		try {
			res = sdb.getQueryResultsCount(source);
		} catch (Exception e) {
			assertTrue(false);
		}
		assertTrue(res == expected);		
	}
	
	@Test
	public void testPagingQuery() {
		String source = "SELECT ?o WHERE " +
				"{ <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#Wine> ?p ?o } ORDER BY ?p";
		String orderLimit = " LIMIT 2 OFFSET 2";
		
		List<BindingSet> res1 = null;
		List<BindingSet> res2 = null;
		try {
			res1 = sdb.getQueryResults(source, 2, 2);
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
}
