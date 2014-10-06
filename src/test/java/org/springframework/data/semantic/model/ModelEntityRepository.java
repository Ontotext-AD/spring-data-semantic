package org.springframework.data.semantic.model;

import org.openrdf.model.URI;
import org.springframework.data.semantic.repository.SemanticRepository;

import java.util.List;

import org.springframework.data.semantic.model.ModelEntity;

public interface ModelEntityRepository extends SemanticRepository<ModelEntity> {
	
	List<ModelEntity> findByName(String name);
	
	List<ModelEntity> findBySynonyms(List<String> synonyms);
	
	List<ModelEntity> findByRelated(List<ModelEntity> related);
	
	List<ModelEntity> findByRelated(URI related);
	
	Long countByName(String name);
	
	Long countBySynonyms(List<String> synonyms);
	
	Long countByRelated(List<ModelEntity> related);
	
	Long countByRelated(URI related);
	
	ModelEntity findOneByRelated(URI related);

}
