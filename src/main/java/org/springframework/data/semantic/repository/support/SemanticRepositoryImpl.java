package org.springframework.data.semantic.repository.support;

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
	public Iterable<T> findAll(Sort sort) {
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
		List<S> result = new LinkedList<S>();
		for(S entity : entities){
			result.add(save(entity));
		}
		return result;
	}

	@Override
	public T findOne(URI id) {
		return operations.find(id, clazz);
	}

	@Override
	public boolean exists(URI id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<T> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<T> findAll(Iterable<URI> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		return operations.count(clazz);
	}

	@Override
	public void delete(URI id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(T entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}
	
	

}
