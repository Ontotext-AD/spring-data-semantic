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
package org.springframework.data.semantic.model;

import java.util.Collection;

import org.openrdf.model.URI;
import org.springframework.data.semantic.annotation.Fetch;
import org.springframework.data.semantic.annotation.RelatedTo;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.annotation.SemanticEntity;
import org.springframework.data.semantic.support.Cascade;
import org.springframework.data.semantic.support.Direction;

@SemanticEntity
public class ModelEntityCollectorCascadeAll {

	@ResourceId
	private URI uri;
	
	@Fetch(value=Cascade.ALL)
	@RelatedTo(direction=Direction.OUTGOING)
	private Collection<ModelEntity> entities;

	/**
	 * @return the uri
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

	/**
	 * @return the entities
	 */
	public Collection<ModelEntity> getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(Collection<ModelEntity> entities) {
		this.entities = entities;
	}
	
}
