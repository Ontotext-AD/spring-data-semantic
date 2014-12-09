package org.springframework.data.semantic.repository;

import java.util.List;

import org.openrdf.model.URI;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.semantic.core.SemanticDatabase;

@NoRepositoryBean
public interface SemanticRepository<T> extends PagingAndSortingRepository<T, URI> {
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findAll()
	 */
	List<T> findAll();

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
	 */
	List<T> findAll(Sort sort);

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findAll(java.lang.Iterable)
	 */
	List<T> findAll(Iterable<URI> ids);
	
	/**
	 * Persists a new entity in a {@link SemanticDatabase}. Similar to save, but without checking/removing existing statements.
	 * @param entity
	 * @return
	 */
	T create(T entity);
	
	/**
	 * Persists new entities in a {@link SemanticDatabase}. Similar to save, but without checking/removing existing statements.
	 * @param entity
	 * @return
	 */
	Iterable<T> create(Iterable<T> entities);
	
}
