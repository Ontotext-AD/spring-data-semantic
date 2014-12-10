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
package org.springframework.data.semantic.support.convert;

import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.support.convert.handlers.PropertiesToDeleteStatementsHandler;
import org.springframework.data.semantic.support.convert.handlers.PropertiesToStatementsHandler;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
/**
 * Class that converts domain objects to RDF.
 * 
 * @author konstantin.pentchev
 *
 */
public class EntityToStatementsConverter {
	
	private SemanticMappingContext mappingContext;
	
	
	
	public EntityToStatementsConverter(SemanticMappingContext mappingContext){
		this.mappingContext = mappingContext;
	}
	
	public RDFState convertEntityToStatements(SemanticPersistentEntity<?> persistentEntity, Object entity){
		RDFState statements = new  RDFState();
		PropertiesToStatementsHandler handler = new PropertiesToStatementsHandler(statements, entity, mappingContext);
		persistentEntity.doWithProperties(handler);
		persistentEntity.doWithAssociations(handler);
		return statements;
	}
	
	public RDFState convertEntityToDeleteStatements(SemanticPersistentEntity<?> persistentEntity, Object entity){
		RDFState statements = new  RDFState();
		PropertiesToDeleteStatementsHandler handler = new PropertiesToDeleteStatementsHandler(statements, entity, mappingContext);
		persistentEntity.doWithProperties(handler);
		persistentEntity.doWithAssociations(handler);
		return statements;
	}
	
	public RDFState convertClassToDeleteStatements(SemanticPersistentEntity<?> persistentEntity){
		RDFState statements = new RDFState();
		//TODO
		return statements;
	}
	

}
