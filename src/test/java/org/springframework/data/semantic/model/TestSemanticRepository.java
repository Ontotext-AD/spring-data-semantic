package org.springframework.data.semantic.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.model.vocabulary.MODEL_ENTITY;
import org.springframework.data.semantic.model.vocabulary.WINE;
import org.springframework.data.semantic.testutils.Utils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/default-context.xml" })
public class TestSemanticRepository {

	@Autowired
	private WineBodyRepository wineRepository;
	
	@Autowired
	private ModelEntityRepository modelEntityRepository;

	@Autowired
	private SemanticDatabase sdb;
	
	@Before
	public void initRepo() {
		Utils.populateTestRepository(sdb);
	}
	
	@Test
	public void testFindOne() {
		WineBody entity = wineRepository.findOne(WINE.LIGHT);
		assertNotNull(entity);
		assertEquals(WINE.LIGHT, entity.getUri());
		assertNotNull(entity.getLabel());
	}
	
	@Test
	public void testSaveUpdate(){
		long count = sdb.count();
		WineBody newEntity = new WineBody();
		newEntity.setUri(WINE.RUBIN);
		newEntity.setLabel("Rubin");
		wineRepository.save(newEntity);
		assertTrue(count < sdb.count());
		List<Statement> statementsForResource = sdb.getStatementsForSubject(WINE.RUBIN);
		assertFalse(statementsForResource.isEmpty());
		//System.out.println(statementsForResource);
		WineBody rubin = wineRepository.findOne(WINE.RUBIN);
		assertNotNull(rubin);
	}
	
	@Test
	public void testCollectionOfAssociations(){
		ModelEntity modelEntity = modelEntityRepository.findOne(MODEL_ENTITY.ENTITY_ONE);
		assertNotNull(modelEntity);
		assertNotNull(modelEntity.getRelated());
		assertEquals(2, modelEntity.getRelated().size());
		
	}
	
	@Test
	public void testModificationOfCollectionOfAssociations(){
		long count = sdb.count();
		ModelEntity modelEntity = modelEntityRepository.findOne(MODEL_ENTITY.ENTITY_TWO);
		modelEntity.getRelated().remove(1);
		modelEntityRepository.save(modelEntity);
		assertEquals(count -1, sdb.count());
	}
	
	@Test
	public void testCount(){
		assertEquals(4, modelEntityRepository.count());
	}
	
	@Test
	public void testFindList(){
		List<URI> uris = Arrays.asList(MODEL_ENTITY.ENTITY_ONE, MODEL_ENTITY.ENTITY_TWO);
		Iterable<ModelEntity> entities = modelEntityRepository.findAll(uris);
		int count = 0;
		for(ModelEntity entity : entities){
			assertTrue(uris.contains(entity.getUri()));
			count++;
		}
		assertEquals(uris.size(), count);
	}
	
	@Test
	public void testExists(){
		assertTrue(modelEntityRepository.exists(MODEL_ENTITY.ENTITY_ONE));
		assertFalse(modelEntityRepository.exists(MODEL_ENTITY.ENTITY_NOT_EXISTS));
	}

}
