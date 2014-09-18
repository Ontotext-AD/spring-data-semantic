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
