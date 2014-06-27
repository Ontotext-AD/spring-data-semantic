package org.springframework.data.semantic.repository;

import org.openrdf.model.URI;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface SemanticRepository<T> extends PagingAndSortingRepository<T, URI> {
	
	

}
