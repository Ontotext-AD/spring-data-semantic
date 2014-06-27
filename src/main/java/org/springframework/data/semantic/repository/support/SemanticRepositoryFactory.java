package org.springframework.data.semantic.repository.support;

import java.io.Serializable;

import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.semantic.support.SemanticTemplateObjectCreator;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;

public class SemanticRepositoryFactory extends RepositoryFactorySupport{
	
	public SemanticRepositoryFactory(SemanticTemplateStatementsCollector statementCollector, SemanticTemplateObjectCreator objectCreator){
		this.statementCollector = statementCollector;
		this.objectCreator = objectCreator;
	}
	
	private SemanticTemplateStatementsCollector statementCollector;
	private SemanticTemplateObjectCreator objectCreator;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(
			Class<T> domainClass) {
		return new SemanticMetamodelEntityInformation(domainClass);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Object getTargetRepository(RepositoryMetadata metadata) {
		Class<?> type = metadata.getDomainType();
		return new SemanticRepositoryImpl(statementCollector, objectCreator, type);
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return SemanticRepositoryImpl.class;
	}

}
