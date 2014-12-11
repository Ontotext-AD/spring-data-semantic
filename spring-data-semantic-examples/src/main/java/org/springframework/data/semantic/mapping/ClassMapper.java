package org.springframework.data.semantic.mapping;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.openrdf.model.URI;
import org.springframework.data.semantic.model.*;
import org.springframework.stereotype.Service;

@Service
public class ClassMapper {

	private static Map<String, Class<? extends Concept>> mapping = new HashMap<String, Class<? extends Concept>>();
	
	@PostConstruct
	public void init() {
		mapping.put(Ontology.CLASS_PERSON, Person.class);
		mapping.put(Ontology.CLASS_LOCATION, Location.class);
		mapping.put(Ontology.CLASS_ORGANIZATION, Organization.class);
	}
	
	public Class<? extends Concept> get(URI type) {
		return mapping.get(type.stringValue());
	}
	
	public Class<? extends Concept> put(URI type, Class<? extends Concept> clazz) {
		return mapping.put(type.stringValue(), clazz);
	}
	
	public Class<? extends Concept> remove(URI type) {
		return mapping.remove(type);
	}
	
}
