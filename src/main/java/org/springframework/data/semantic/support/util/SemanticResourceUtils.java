package org.springframework.data.semantic.support.util;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.URI;
import org.springframework.data.semantic.support.model.SemanticResource;

public class SemanticResourceUtils {
	
	public static List<URI> extractResourceIds(Iterable<SemanticResource> resources){
		List<URI> ids = new LinkedList<URI>();
		for(SemanticResource resource : resources){
			ids.add(resource.getId());
		}
		return ids;
	}

}
