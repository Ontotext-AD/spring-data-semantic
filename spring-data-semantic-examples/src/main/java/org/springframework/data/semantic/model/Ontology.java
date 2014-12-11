package org.springframework.data.semantic.model;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;

public interface Ontology {
	public static final String DEMO_NAMESPACE = "http://demo-semantic-data.com/";

	public static final String CLASS_CONCEPT = DEMO_NAMESPACE + "Concept";
	public static final String CLASS_PERSON = DEMO_NAMESPACE + "Person";
	public static final String CLASS_LOCATION = DEMO_NAMESPACE + "Location";
	public static final String CLASS_ORGANIZATION = DEMO_NAMESPACE + "Organization";

	public static final String MAIN_LABEL = DEMO_NAMESPACE + "mainLabel";
	public static final String DESCRIPTION = DEMO_NAMESPACE + "description";
	public static final String LOCATION_LATITUDE = DEMO_NAMESPACE + "latitude";
	public static final String LOCATION_LONGTITUDE = DEMO_NAMESPACE + "longtitude";
	public static final String ORGANIZATION_PARENT_ORGANIZATION = DEMO_NAMESPACE + "parentOrganization";
	public static final String PERSON_BIRTHDATE = DEMO_NAMESPACE + "birthDate";

	public static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
	public static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
}
