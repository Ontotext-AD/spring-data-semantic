package org.springframework.data.semantic.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.model.vocabulary.WINE;
import org.springframework.data.semantic.testutils.Utils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/default-context.xml" })
public class TestSemanticRepository {

	@Autowired
	private TestEntityRepository repository;

	@Autowired
	private SemanticDatabase sdb;
	
	@Before
	public void initRepo() {
		Utils.populateTestRepository(sdb);
	}
	
	@Test
	public void testFindOne() {
		TestEntity entity = repository.findOne(WINE.LIGHT);
		assertNotNull(entity);
		assertEquals(WINE.LIGHT, entity.getUri());
	}

}
