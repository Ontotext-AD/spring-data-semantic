package org.springframework.data.semantic.repository;

import java.io.Serializable;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;

public class SemanticRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends TransactionalRepositoryFactoryBeanSupport<T, S, ID>{

	@Override
	protected RepositoryFactorySupport doCreateRepositoryFactory() {
		// TODO Auto-generated method stub
		return null;
	}

}
