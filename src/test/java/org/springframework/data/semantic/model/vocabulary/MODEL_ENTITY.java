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
package org.springframework.data.semantic.model.vocabulary;

import org.openrdf.model.URI;

public class MODEL_ENTITY extends VOCABULARY {
	
	public static final String NAMESPACE = "urn:spring-data-semantic:";
	
	public final static URI ENTITY_ONE = create("entity:1");
	
	public final static URI ENTITY_TWO = create("entity:2");
	
	public final static URI ENTITY_THREE = create("entity:3");
	
	public final static URI ENTITY_NOT_EXISTS = create("entity:33");
	
	public final static URI ENTITY_NOT_EXISTS_TWO = create("entity:66");
	
	public final static URI ENTITY_FIVE = create("entity:5");
	
	public final static URI COLLECTOR_ONE = create("collector:1");
	
	public final static URI COLLECTOR_TWO = create("collector:2");
	
	public final static URI ENTITY_EXTENDED = create("entity:extended:1");

	private static URI create(String localName){
		return create(NAMESPACE, localName);
	}
			
}
