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
	
	public static String getObjectBinding(String subjBinding, SemanticPersistentProperty persistentProperty){
		String obj;
		if(subjBinding.charAt(0) != '<'){
			obj = subjBinding + "_" + persistentProperty.getBindingName();
		}
		else{
			obj = "?" + persistentProperty.getBindingName();
		}
		return obj;
	}
	
}
