package org.springframework.data.semantic.repository;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.support.repository.SemanticRepositoryFactory;

public class SemanticRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends TransactionalRepositoryFactoryBeanSupport<T, S, ID>{
	
	@Autowired
	private SemanticOperationsCRUD operations;
	
	

	@Override
	protected RepositoryFactorySupport doCreateRepositoryFactory() {
		return new SemanticRepositoryFactory(operations);
	}



	/**
	 * @return the operations
	 */
	public SemanticOperationsCRUD getOperations() {
		return operations;
	}



	/**
	 * @param operations the operations to set
	 */
	public void setOperations(SemanticOperationsCRUD operations) {
		this.operations = operations;
	}

	
	
	
	

}
