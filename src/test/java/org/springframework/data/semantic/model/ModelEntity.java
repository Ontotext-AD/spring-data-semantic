package org.springframework.data.semantic.model;

import java.util.List;

import org.openrdf.model.URI;
import org.springframework.data.semantic.annotation.Context;
import org.springframework.data.semantic.annotation.Fetch;
import org.springframework.data.semantic.annotation.Language;
import org.springframework.data.semantic.annotation.Optional;
import org.springframework.data.semantic.annotation.Predicate;
import org.springframework.data.semantic.annotation.RelatedTo;
import org.springframework.data.semantic.annotation.SemanticEntity;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.annotation.Language.Languages;
import org.springframework.data.semantic.support.Direction;

@SemanticEntity()
public class ModelEntity {
	
	@ResourceId
	private URI uri;
	
	//@Datatype(XSDDatatype.)
	@Optional
	@Language({Languages.en, Languages.de})
	@Predicate({"http://www.w3.org/2004/02/skos/core#prefLabel"})
	private String name;
	
	@Context
	private String graph;
	
	@Optional
	@Fetch
	@RelatedTo(dicrection=Direction.BOTH)
	private List<ModelEntity> related;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the related
	 */
	public List<ModelEntity> getRelated() {
		return related;
	}

	/**
	 * @param related the related to set
	 */
	public void setRelated(List<ModelEntity> related) {
		this.related = related;
	}

	/**
	 * @return the graph
	 */
	public String getGraph() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(String graph) {
		this.graph = graph;
	}
	
	
	

}
