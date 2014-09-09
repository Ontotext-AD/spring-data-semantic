package org.springframework.data.semantic.model;

import org.springframework.data.semantic.repository.SemanticRepository;
import java.util.List;
import org.springframework.data.semantic.model.ModelEntity;

public interface ModelEntityRepository extends SemanticRepository<ModelEntity> {
	
	List<ModelEntity> findByName(String name);
	
	List<ModelEntity> findBySynonyms(List<String> synonyms);

}
