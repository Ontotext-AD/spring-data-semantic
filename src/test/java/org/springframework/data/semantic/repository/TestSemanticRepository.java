package org.springframework.data.semantic.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.model.DateEntity;
import org.springframework.data.semantic.model.DateEntityRepository;
import org.springframework.data.semantic.model.ModelEntity;
import org.springframework.data.semantic.model.ModelEntityCollector;
import org.springframework.data.semantic.model.ModelEntityCollectorCascadeAll;
import org.springframework.data.semantic.model.ModelEntityCollectorCascadeAllRepository;
import org.springframework.data.semantic.model.ModelEntityCollectorRepository;
import org.springframework.data.semantic.model.ModelEntityRepository;
import org.springframework.data.semantic.model.WineBody;
import org.springframework.data.semantic.model.WineBodyRepository;
import org.springframework.data.semantic.model.vocabulary.DATE_ENTITY;
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
	private ModelEntityCollectorRepository modelEntityCollectorRepository;
	
	@Autowired
	private ModelEntityCollectorCascadeAllRepository modelEntityCascadeAllRepository;

	@Autowired
	private SemanticDatabase sdb;
	
	@Autowired
	private DateEntityRepository dateEntityRepository;
	
	@Before
	public void initRepo() {
		Utils.populateTestRepository(sdb);
	}
	
	@After
	public void clearRepo(){
		sdb.clear();
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
	public void testSaveMultiple(){
		assertNull(wineRepository.findOne(WINE.GAMZA));
		assertNull(wineRepository.findOne(WINE.KADARKA));
		
		WineBody gamza = new WineBody();
		gamza.setLabel("Gamza");
		gamza.setUri(WINE.GAMZA);
		
		WineBody kadarka = new WineBody();
		kadarka.setLabel("Kadarka");
		kadarka.setUri(WINE.KADARKA);
		
		Iterable<WineBody> newWineBodies = wineRepository.save(Arrays.asList(gamza, kadarka));
		assertNotNull(newWineBodies);
		assertTrue(newWineBodies.iterator().hasNext());
		
		assertNotNull(wineRepository.findOne(WINE.GAMZA));
		assertNotNull(wineRepository.findOne(WINE.KADARKA));
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
		assertEquals(count -2, sdb.count());
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
	
	@Test
	public void testFindAll(){
		Iterable<ModelEntity> modelEntities = modelEntityRepository.findAll();
		int count = 0;
		for(ModelEntity entity : modelEntities){
			assertNotNull(entity.getUri());
			count++;
		}
		assertEquals(modelEntityRepository.count(), count);
	}
	
	@Test
	public void testFindPage(){
		int pageSize = 2;
		Iterable<ModelEntity> modelEntities = modelEntityRepository.findAll(new PageRequest(0, pageSize));
		int count = 0;
		for(ModelEntity entity : modelEntities){
			assertNotNull(entity.getUri());
			count++;
		}
		assertEquals(pageSize, count);
	}
	
	@Test
	public void testEagerLoad(){
		ModelEntityCollector collector = modelEntityCollectorRepository.findOne(MODEL_ENTITY.COLLECTOR_ONE);
		assertNotNull(collector);
		assertFalse(collector.getEntities().isEmpty());
		for(ModelEntity modelEntity : collector.getEntities()){
			assertNotNull(modelEntity.getName());
			assertFalse(modelEntity.getRelated().isEmpty());
		}
	}
	
	@Test
	public void testEagerSaveFail(){
		ModelEntityCollector collector = new ModelEntityCollector();
		collector.setUri(MODEL_ENTITY.COLLECTOR_TWO);
		ModelEntity entity = new ModelEntity();
		entity.setName("new entity");
		entity.setUri(MODEL_ENTITY.ENTITY_NOT_EXISTS_TWO);
		collector.setEntities(Arrays.asList(entity));
		assertNotNull(modelEntityCollectorRepository.save(collector));
		assertNull(modelEntityRepository.findOne(MODEL_ENTITY.ENTITY_NOT_EXISTS_TWO));
	}
	
	@Test
	public void testEagerSaveSuccess(){
		ModelEntityCollectorCascadeAll collector = new ModelEntityCollectorCascadeAll();
		collector.setUri(MODEL_ENTITY.COLLECTOR_TWO);
		ModelEntity entity = new ModelEntity();
		entity.setName("new entity");
		entity.setUri(MODEL_ENTITY.ENTITY_NOT_EXISTS_TWO);
		collector.setEntities(Arrays.asList(entity));
		assertNotNull(modelEntityCascadeAllRepository.save(collector));
		assertNotNull(modelEntityRepository.findOne(MODEL_ENTITY.ENTITY_NOT_EXISTS_TWO));
	}
	
	@Test
	public void testDateLoad(){
		DateEntity date = dateEntityRepository.findOne(DATE_ENTITY.DATE_ONE);
		assertNotNull(date);
		assertNotNull(date.getDate());
	}
	
	@Test
	public void testDateSave(){
		Date date = new Date();
		DateEntity dateEntity = new DateEntity();
		dateEntity.setId(DATE_ENTITY.DATE_TWO);
		dateEntity.setDate(date);
		assertNotNull(dateEntityRepository.save(dateEntity));
		
		dateEntity = dateEntityRepository.findOne(DATE_ENTITY.DATE_TWO);
		assertNotNull(dateEntity);
		assertEquals(date, dateEntity.getDate());
	}
	
	@Test
	public void testDelete(){
		ModelEntity toDelete = new ModelEntity();
		toDelete.setUri(MODEL_ENTITY.ENTITY_FIVE);
		toDelete.setName("Model Entity Five");
		
		modelEntityRepository.save(toDelete);
		assertTrue(modelEntityRepository.exists(MODEL_ENTITY.ENTITY_FIVE));
		
		modelEntityRepository.delete(MODEL_ENTITY.ENTITY_FIVE);
		
		assertFalse(modelEntityRepository.exists(MODEL_ENTITY.ENTITY_FIVE));
	}
	
	@Test
	public void testDeleteEntity(){
		ModelEntity toDelete = new ModelEntity();
		toDelete.setUri(MODEL_ENTITY.ENTITY_FIVE);
		toDelete.setName("Model Entity Five");
		
		toDelete = modelEntityRepository.save(toDelete);
		assertTrue(modelEntityRepository.exists(MODEL_ENTITY.ENTITY_FIVE));
		
		modelEntityRepository.delete(toDelete);
		
		assertFalse(modelEntityRepository.exists(MODEL_ENTITY.ENTITY_FIVE));
	}
	
	@Test
	public void testFindBySimpleProperty(){
		List<ModelEntity> entities = modelEntityRepository.findByName("Model Entity One");
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertEquals(MODEL_ENTITY.ENTITY_ONE, entities.get(0).getUri());
	}
	
	@Test
	public void testFindByCollectionProperty(){
		List<ModelEntity> entities = modelEntityRepository.findBySynonyms(Arrays.asList("Model Entity Eins", "Model Entity Uno"));
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		assertEquals(MODEL_ENTITY.ENTITY_ONE, entities.get(0).getUri());
	}
	
	@Test
	public void testFindByAssociation(){
		ModelEntity modelEntityThree = new ModelEntity();
		modelEntityThree.setUri(MODEL_ENTITY.ENTITY_THREE);
		List<ModelEntity> entities = modelEntityRepository.findByRelated(Arrays.asList(modelEntityThree));
		assertNotNull(entities);
		assertEquals(2, entities.size());
		for(ModelEntity entity : entities){
			assertTrue(entity.getUri().equals(MODEL_ENTITY.ENTITY_ONE) || entity.getUri().equals(MODEL_ENTITY.ENTITY_TWO));
		}
	}
	
	@Test
	public void testFindByAssociationURI(){
		List<ModelEntity> entities = modelEntityRepository.findByRelated(MODEL_ENTITY.ENTITY_THREE);
		assertNotNull(entities);
		assertEquals(2, entities.size());
		for(ModelEntity entity : entities){
			assertTrue(entity.getUri().equals(MODEL_ENTITY.ENTITY_ONE) || entity.getUri().equals(MODEL_ENTITY.ENTITY_TWO));
		}
	}
	
	@Test
	public void testOneFindByAssociationURI(){
		ModelEntity entity = modelEntityRepository.findOneByRelated(MODEL_ENTITY.ENTITY_THREE);
		assertNotNull(entity);
		assertTrue(entity.getUri().equals(MODEL_ENTITY.ENTITY_ONE) || entity.getUri().equals(MODEL_ENTITY.ENTITY_TWO));
	}
	
	@Test
	public void testCountBySimpleProperty(){
		long count = modelEntityRepository.countByName("Model Entity One");
		assertEquals(1, count);
	}
	
	@Test
	public void testCountByCollectionProperty(){
		long count = modelEntityRepository.countBySynonyms(Arrays.asList("Model Entity Eins", "Model Entity Uno"));
		assertEquals(1, count);
	}
	
	@Test
	public void testCountByAssociation(){
		ModelEntity modelEntityThree = new ModelEntity();
		modelEntityThree.setUri(MODEL_ENTITY.ENTITY_THREE);
		long count = modelEntityRepository.countByRelated(Arrays.asList(modelEntityThree));
		assertEquals(2, count);
	}
	
	@Test
	public void testCountByAssociationURI(){
		long count = modelEntityRepository.countByRelated(MODEL_ENTITY.ENTITY_THREE);
		assertEquals(2, count);
	}

	@Test
	public void testDeleteAll(){
		modelEntityRepository.deleteAll();
		assertEquals(0, modelEntityRepository.count());
	}
	
}
