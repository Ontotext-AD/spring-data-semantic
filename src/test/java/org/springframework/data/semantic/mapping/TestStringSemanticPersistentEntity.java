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
package org.springframework.data.semantic.mapping;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.annotation.SemanticEntity;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class TestStringSemanticPersistentEntity {
	
	private static final String defaultNS = "urn:forest:default:";
	
	private static final String customNS = "http://custom.namespace/test/";
	
	private static final String id = "one";
	
	private SemanticMappingContext mappingContext = new SemanticMappingContext(new ArrayList<Namespace>(0), new NamespaceImpl("", defaultNS));
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGetId(){
		SemanticPersistentEntity<StringIdEntity> persistentEntity = (SemanticPersistentEntity<StringIdEntity>) mappingContext.getPersistentEntity(StringIdEntity.class);
		
		StringIdEntity instance = new StringIdEntity();
		instance.setId(id);
		
		URI uri = persistentEntity.getResourceId(instance);
		
		assertEquals(defaultNS+id, uri.toString());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGetNamesaceId(){
		SemanticPersistentEntity<StringNamespaceIdEntity> persistentEntity = (SemanticPersistentEntity<StringNamespaceIdEntity>) mappingContext.getPersistentEntity(StringNamespaceIdEntity.class);
		
		StringNamespaceIdEntity instance = new StringNamespaceIdEntity();
		instance.setId(id);
		
		URI uri = persistentEntity.getResourceId(instance);
		
		assertEquals(customNS+id, uri.toString());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSetId(){
		SemanticPersistentEntity<StringIdEntity> persistentEntity = (SemanticPersistentEntity<StringIdEntity>) mappingContext.getPersistentEntity(StringIdEntity.class);
		StringIdEntity instance = new StringIdEntity();
		persistentEntity.setResourceId(instance, new URIImpl(defaultNS+id));
		
		assertEquals(id, instance.getId());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSetNamespaceId(){
		SemanticPersistentEntity<StringNamespaceIdEntity> persistentEntity = (SemanticPersistentEntity<StringNamespaceIdEntity>) mappingContext.getPersistentEntity(StringNamespaceIdEntity.class);
		StringNamespaceIdEntity instance = new StringNamespaceIdEntity();
		persistentEntity.setResourceId(instance, new URIImpl(customNS+id));
		
		assertEquals(id, instance.getId());
	}
	
	
	
	@SemanticEntity
	class StringIdEntity {
		
		@ResourceId
		private String id;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
		
	}
	
	@org.springframework.data.semantic.annotation.Namespace(namespace=customNS)
	@SemanticEntity
	class StringNamespaceIdEntity extends StringIdEntity {
		
	}

}
