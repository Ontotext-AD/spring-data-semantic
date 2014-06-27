package org.springframework.data.semantic.repository;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.data.semantic.repository.support.SemanticRepositoryFactory;
import org.springframework.data.semantic.support.SemanticTemplateObjectCreator;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;

public class SemanticRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends TransactionalRepositoryFactoryBeanSupport<T, S, ID>{
	
	@Autowired
	private SemanticTemplateStatementsCollector statementsCollector;
	
	@Autowired
	private SemanticTemplateObjectCreator objectCreator;
	
	

	@Override
	protected RepositoryFactorySupport doCreateRepositoryFactory() {
		return new SemanticRepositoryFactory(statementsCollector, objectCreator);
	}

	/**
	 * @param statementsCollector the statementsCollector to set
	 */
	public void setStatementsCollector(SemanticTemplateStatementsCollector statementsCollector) {
		this.statementsCollector = statementsCollector;
	}

	/**
	 * @param objectCreator the objectCreator to set
	 */
	public void setObjectCreator(SemanticTemplateObjectCreator objectCreator) {
		this.objectCreator = objectCreator;
	}
	
	
	

}
