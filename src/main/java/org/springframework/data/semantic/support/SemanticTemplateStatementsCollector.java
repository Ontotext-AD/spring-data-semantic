package org.springframework.data.semantic.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.repository.RepositoryException;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticOperationsStatementsCollector;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.semantic.support.mapping.SemanticPersistentEntityImpl;

public class SemanticTemplateStatementsCollector implements SemanticOperationsStatementsCollector {
	
	private SemanticDatabase semanticDB;	
	private SemanticMappingContext mappingContext;
	
	public SemanticTemplateStatementsCollector(SemanticDatabase semanticDB, ConversionService conversionService, 
			SemanticMappingContext mappingContext) throws RepositoryException {
		
		this.semanticDB = semanticDB;
		this.mappingContext = mappingContext != null ? mappingContext 
				: new SemanticMappingContext(semanticDB.getNamespaces(), semanticDB.getDefaultNamespace());
	}	
	
	public MappingPolicy getMappingPolicy(Class<?> clazz){
		SemanticPersistentEntity<?> persistentEntity = getPersistentEntity(clazz);
		return persistentEntity.getMappingPolicy();
	}
	
	private SemanticPersistentEntity<?> getPersistentEntity(Class<?> targetClazz){
		return (SemanticPersistentEntityImpl<?>) mappingContext.getPersistentEntity(targetClazz);
	}	
		
	@Override
	public Model getStatementsForResourceProperty(Object entity, SemanticPersistentProperty property){
		SemanticPersistentEntity<?> persistentEntity = getPersistentEntity(entity.getClass());
		URI uri = persistentEntity.getResourceId(entity);
		try {
			return semanticDB.getGraphQueryResults(
				EntityToQueryConverter.getGraphQueryForResourceProperty(uri, persistentEntity, property));
		} catch (Exception e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
		}
	}	

	@Override
	public Model getStatementsForResource(URI resource, Class<?> clazz) {
		try {
			return semanticDB.getGraphQueryResults(
					EntityToQueryConverter.getGraphQueryForResource(resource, getPersistentEntity(clazz)));
		} catch (Exception e) {
			throw ExceptionTranslator.translateExceptionIfPossible(e);
		} 
	}

	@Override
	public Collection<Model> getStatementsForResources(Class<?> clazz) {
		return getStatementsForResources(clazz, null, null);
	}

	@Override
	public Collection<Model> getStatementsForResources(Class<?> clazz, Long offset, Long limit) {
			try {
				SemanticPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(clazz);
				Model results = semanticDB.getGraphQueryResults(EntityToQueryConverter.getGraphQueryForEntityClass(persistentEntity), offset, limit);
				Set<Resource> subjects = results.filter(null, null, persistentEntity.getRDFType()).subjects();
				Map<Resource, Resource> resourceToSubject = new HashMap<Resource, Resource>();
				Map<Resource, Model> subjectToModel = new HashMap<Resource, Model>();
				for(Statement statement : results){
					Resource subject = statement.getSubject();
					if(subjects.contains(subject)){
						Value object = statement.getObject();
						if(object instanceof Resource){
							resourceToSubject.put((Resource) object, subject);
						}
						Model statementsForSubject = subjectToModel.get(subject);
						if(statementsForSubject == null){
							statementsForSubject = new LinkedHashModel();
							subjectToModel.put(subject, statementsForSubject);
						}
						statementsForSubject.add(statement);
					}
					else{
						Value object = statement.getObject();
						Resource entityId = resourceToSubject.get(subject);
						if(object instanceof Resource){
							resourceToSubject.put((Resource) object, entityId); 
						}
						Model statementsForSubject = subjectToModel.get(entityId);
						statementsForSubject.add(statement);
					}
				}
				return subjectToModel.values();
			} catch (Exception e) {
				throw ExceptionTranslator.translateExceptionIfPossible(e);
			}
	}	
}
