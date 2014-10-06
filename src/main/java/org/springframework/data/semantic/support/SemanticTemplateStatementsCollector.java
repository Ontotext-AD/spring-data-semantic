package org.springframework.data.semantic.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryException;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticOperationsStatementsCollector;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.convert.EntityToQueryConverter;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.semantic.support.mapping.SemanticPersistentEntityImpl;

public class SemanticTemplateStatementsCollector implements SemanticOperationsStatementsCollector {
	
	private SemanticDatabase semanticDB;	
	private SemanticMappingContext mappingContext;
	private EntityToQueryConverter entityToQueryConverter;
	
	
	public SemanticTemplateStatementsCollector(SemanticDatabase semanticDB,
			SemanticMappingContext mappingContext, EntityToQueryConverter entityToQueryConverter) throws RepositoryException {
		
		this.semanticDB = semanticDB;
		this.mappingContext = mappingContext != null ? mappingContext 
				: new SemanticMappingContext(semanticDB.getNamespaces(), semanticDB.getDefaultNamespace());
		this.entityToQueryConverter = entityToQueryConverter;
	}	
	
	public MappingPolicy getMappingPolicy(Class<?> clazz){
		SemanticPersistentEntity<?> persistentEntity = getPersistentEntity(clazz);
		return persistentEntity.getMappingPolicy();
	}
	
		
		
	@Override
	public Model getStatementsForResourceProperty(Object entity, SemanticPersistentProperty property){
		SemanticPersistentEntity<?> persistentEntity = getPersistentEntity(entity.getClass());
		URI uri = persistentEntity.getResourceId(entity);
		try {
			return semanticDB.getGraphQueryResults(
				entityToQueryConverter.getGraphQueryForResourceProperty(uri, persistentEntity, property));
		} catch (Exception e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
		}
	}	

	@Override
	public <T> Model getStatementsForResource(URI resource, Class<? extends T> clazz) {
		try {
			return semanticDB.getGraphQueryResults(
					entityToQueryConverter.getGraphQueryForResource(resource, getPersistentEntity(clazz)));
		} catch (Exception e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
		} 
	}

	@Override
	public <T> Collection<Model> getStatementsForResources(Class<? extends T> clazz) {
		return getStatementsForResources(clazz, null, null);
	}

	@Override
	public <T> Collection<Model> getStatementsForResources(Class<? extends T> clazz, Long offset, Long limit) {
		try {
			SemanticPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(clazz);
			Model results = semanticDB.getGraphQueryResults(entityToQueryConverter.getGraphQueryForEntityClass(persistentEntity), offset, limit);
			return assembleModels(persistentEntity.getRDFType(), results);
		} catch (Exception e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
		}
	}

	@Override
	public <T> Collection<Model> getStatementsForResourcesAndProperties(
			Class<? extends T> clazz, Map<String, Object> parameterToValue,
			Long offset, Long limit) {
		try {
			SemanticPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(clazz);
			Model results = semanticDB.getGraphQueryResults(entityToQueryConverter.getGraphQueryForEntityClass(persistentEntity, parameterToValue), offset, limit);
			return assembleModels(persistentEntity.getRDFType(), results);
		} catch (Exception e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
		}
	}
	
	public <T> Collection<Model> assembleModels(URI type, Model allStatements){
		Model subjects = allStatements.filter(null, null, type);
		Map<Resource, Model> entityIdToModel = new HashMap<Resource, Model>();
		for(Statement st : subjects){
			Resource subject = st.getSubject();
			Model statementsForSubject = new TreeModel();
			getStatementsForSubject(allStatements, subject, statementsForSubject);
			entityIdToModel.put(subject, statementsForSubject);
		}
		return entityIdToModel.values();
	}
	
	private void getStatementsForSubject(Model source, Resource subject, Model dest){
		Model directStatements = source.filter(subject, null, null);
		dest.addAll(directStatements);
		for(Statement st : directStatements){
			Value object = st.getObject();
			if(object instanceof Resource && !st.getPredicate().equals(RDF.TYPE) && dest.filter((Resource) object, null, null).isEmpty()){
				getStatementsForSubject(source, (Resource) object, dest);
			}
		}
	}
	
	private SemanticPersistentEntity<?> getPersistentEntity(Class<?> targetClazz){
		return (SemanticPersistentEntityImpl<?>) mappingContext.getPersistentEntity(targetClazz);
	}

	
	@Override
	public <T> Long getCountForResource(Class<? extends T> clazz) {
		List<BindingSet> result;
		try {
			SemanticPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(clazz);
			result = this.semanticDB.getQueryResults(entityToQueryConverter.getGraphQueryForResourceCount(persistentEntity, new HashMap<String, Object>()));
			return Long.valueOf(result.get(0).getValue("count").stringValue());
		} catch (Exception e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
		}
		
	}
	

	@Override
	public <T> Long getCountForResourceAndProperties(Class<? extends T> clazz,
			Map<String, Object> parameterToValue) {
		try {
			SemanticPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(clazz);
			List<BindingSet> results = semanticDB.getQueryResults(entityToQueryConverter.getGraphQueryForResourceCount(persistentEntity, parameterToValue));
			return Long.valueOf(results.get(0).getValue("count").stringValue());
		} catch (Exception e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
		}
	}
	
}
