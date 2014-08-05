package org.springframework.data.semantic.repository.support;

import java.io.Serializable;

import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;

public class SemanticRepositoryFactory extends RepositoryFactorySupport{
	
	public SemanticRepositoryFactory(SemanticOperationsCRUD operations){
		this.operations = operations;
	}
	
	private SemanticOperationsCRUD operations;

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
		return new SemanticRepositoryImpl(operations, type);
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return SemanticRepositoryImpl.class;
	}

}
