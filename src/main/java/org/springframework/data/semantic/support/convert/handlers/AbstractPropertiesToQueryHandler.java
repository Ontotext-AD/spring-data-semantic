package org.springframework.data.semantic.support.convert.handlers;

import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public abstract class AbstractPropertiesToQueryHandler implements PropertyHandler<SemanticPersistentProperty>,  AssociationHandler<SemanticPersistentProperty>{

	protected SemanticMappingContext mappingContext;
	
	public AbstractPropertiesToQueryHandler(SemanticMappingContext mappingContext){
		this.mappingContext = mappingContext;
	}
	
	/**
	 * if a SemanticPersistentProperty should be included in the query which retrieves the object
	 * @param persistentProperty
	 * @return
	 */
	public boolean isRetrivableProperty(SemanticPersistentProperty persistentProperty) {
		//TODO do not include properties which are to be lazy loaded or are attached (always retrieved from the repository)
		return 
				!persistentProperty.isIdProperty() && 
				!persistentProperty.isTransient() && 
				!persistentProperty.isContext() /*&&
				persistentProperty.getMappingPolicy().eagerLoad() && 
				persistentProperty.getMappingPolicy().useDirty()*/;				
	}
	
	public static void appendPattern(StringBuilder sb, String subj, String pred, String varName){
		sb.append(subj);
		sb.append(" ");
		sb.append(pred);
		sb.append(" ");
		sb.append(varName);
		sb.append(" . ");
	}
	
}
