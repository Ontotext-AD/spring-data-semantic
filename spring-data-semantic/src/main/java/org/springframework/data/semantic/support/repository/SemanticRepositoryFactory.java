/**
 * Copyright (C) 2014 Ontotext AD (info@ontotext.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.semantic.support.repository;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.support.repository.query.SemanticQueryMethod;

public class SemanticRepositoryFactory extends RepositoryFactorySupport{
	
	private final SemanticOperationsCRUD operations;
	
	public SemanticRepositoryFactory(SemanticOperationsCRUD operations){
		this.operations = operations;
	}
	
	

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
	
	@Override
	protected QueryLookupStrategy getQueryLookupStrategy(Key key) {
		return new SemanticQueryLookupStrategy();
	}
	
	private class SemanticQueryLookupStrategy implements QueryLookupStrategy {
		public RepositoryQuery resolveQuery(Method method, RepositoryMetadata repositoryMetadata, NamedQueries namedQueries) {
            final SemanticQueryMethod queryMethod = new SemanticQueryMethod(method,repositoryMetadata);
            return queryMethod.createQuery(operations);
        }
	}

}
