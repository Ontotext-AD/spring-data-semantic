package org.springframework.data.semantic.repository.support;

import org.openrdf.model.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.semantic.core.StatementsIterator;
import org.springframework.data.semantic.repository.SemanticRepository;
import org.springframework.data.semantic.support.SemanticTemplateCRUD;
import org.springframework.data.semantic.support.SemanticTemplateObjectCreator;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;

/**
 * Implementation of SemanticRepository interface (and consequently {@link PagingAndSortingRepository}).
 * @author konstantin.pentchev
 *
 * @param <T>
 */
public class SemanticRepositoryImpl<T> implements SemanticRepository<T> {
	
	protected SemanticTemplateStatementsCollector statementsCollector;
	protected SemanticTemplateObjectCreator objectCreator;
	protected Class<T> clazz;
	
	public SemanticRepositoryImpl(SemanticTemplateStatementsCollector statementsCollector, SemanticTemplateObjectCreator objectCreator, Class<T> clazz) {
		this.statementsCollector = statementsCollector;
		this.objectCreator = objectCreator;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T findOne(URI id) {
		return createEntity(statementsCollector.getStatementsForResourceClass(id, clazz));
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
		// TODO Auto-generated method stub
		return 0;
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
	
	protected T createEntity(StatementsIterator statements) {
        return objectCreator.createObjectFromStatements(statements, clazz, statementsCollector.getMappingPolicy(clazz));
    }

}
