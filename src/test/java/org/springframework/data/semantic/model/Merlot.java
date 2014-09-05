package org.springframework.data.semantic.model;

import org.openrdf.model.URI;
import org.springframework.data.semantic.annotation.Predicate;
import org.springframework.data.semantic.annotation.RelatedTo;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.annotation.SemanticEntity;

@SemanticEntity
public class Merlot {
	
	@ResourceId
	private URI uri;
	
	@RelatedTo
	@Predicate("hasBody")
	private WineBody body;
	
	@Predicate("hasFlavor")
	private String flavor;
	
	@Predicate("hasMaker")
	private String maker;
	
	@Predicate("hasSugar")
	private String sugar;
		
	@Predicate("locatedIn")
	private String location;
	
	@Predicate("hasYear")
	private int year;
	
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	public WineBody getBody() {
		return body;
	}
	public void setBody(WineBody body) {
		this.body = body;
	}

	public String getFlavor() {
		return flavor;
	}
	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}

	public String getMaker() {
		return maker;
	}
	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getSugar() {
		return sugar;
	}
	public void setSugar(String sugar) {
		this.sugar = sugar;
	}

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

}
