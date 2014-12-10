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
package org.springframework.data.semantic.support.repository.query;

import java.lang.reflect.Method;

import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;

public class SemanticQueryMethod extends QueryMethod {
	
	private PartTree tree;
	
	public SemanticQueryMethod(Method method, RepositoryMetadata metadata) {
		super(method, metadata);
		this.tree = new PartTree(method.getName(), metadata.getDomainType());
	}
	
	public RepositoryQuery createQuery(SemanticOperationsCRUD operations){
		if(tree.isCountProjection()){
			return new CountSemanticRepositoryQuery(operations, this.getName(), this.getDomainClass(), this.getParameters());
		}
		else if(tree.isDelete()){
			//TODO
			return null;
		}
		else if(tree.isDistinct()){
			//TODO
			return null;
		}
		else {
			return new FindSemanticRepositoryQuery(operations, this.getName(), this.getDomainClass(), this.getParameters());
		}
		
	}

}
