package org.springframework.data.semantic.support.repository;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.repository.SemanticRepository;

/**
 * Implementation of SemanticRepository interface (and consequently {@link PagingAndSortingRepository}).
 * @author konstantin.pentchev
 *
 * @param <T>
 */
public class SemanticRepositoryImpl<T> implements SemanticRepository<T> {
	
	protected Class<T> clazz;
	
	protected SemanticOperationsCRUD operations;
	
	public SemanticRepositoryImpl(SemanticOperationsCRUD operations, Class<T> clazz) {
		this.operations = operations;
		this.clazz = clazz;
	}

	@Override
	public List<T> findAll() {
		return this.operations.findAll(clazz);
	}
	
	@Override
	public List<T> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends T> S save(S entity) {
		return operations.save(entity);
	}

	@Override
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		return operations.save(entities);
	}

	@Override
	public T findOne(URI id) {
		return operations.find(id, clazz);
	}

	@Override
	public boolean exists(URI id) {
		return this.operations.exists(id, clazz);
	}

	@Override
	public List<T> findAll(Iterable<URI> ids) {
		List<T> result = new LinkedList<T>();
		for(URI id : ids){
			T entity = this.operations.find(id, clazz);
			if(entity != null){
				result.add(entity);
			}
		}
		return result;
	}

	@Override
	public long count() {
		return this.operations.count(clazz);
	}

	@Override
	public void delete(URI id) {
		operations.delete(id, clazz);
	}

	@Override
	public void delete(T entity) {
		this.operations.delete(entity);
		
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		for(T entity : entities){
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		this.operations.deleteAll(clazz);
	}
	
	

}
