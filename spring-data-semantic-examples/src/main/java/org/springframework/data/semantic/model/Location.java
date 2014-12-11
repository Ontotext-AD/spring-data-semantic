package org.springframework.data.semantic.model;

import org.springframework.data.semantic.annotation.Namespace;
import org.springframework.data.semantic.annotation.Predicate;
import org.springframework.data.semantic.annotation.SemanticEntity;

@Namespace(namespace = Ontology.DEMO_NAMESPACE)
@SemanticEntity(rdfType = Ontology.CLASS_LOCATION)
public class Location extends Concept {

	@Predicate(Ontology.LOCATION_LATITUDE)
	Long latitude;

	@Predicate(Ontology.LOCATION_LONGTITUDE)
	Long longtitude;

	public Long getLatitude() {
		return latitude;
	}

	public void setLatitude(Long latitude) {
		this.latitude = latitude;
	}

	public Long getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(Long longtitude) {
		this.longtitude = longtitude;
	}
}
