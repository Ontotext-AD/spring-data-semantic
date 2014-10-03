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
			//TODO
			return null;
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
