package org.springframework.data.semantic.model.vocabulary;

import org.openrdf.model.URI;

public class MODEL_ENTITY extends VOCABULARY {
	
	public static final String NAMESPACE = "urn:sprind-data-semantic:";
	
	public final static URI ENTITY_ONE = create("entity:1");
	
	public final static URI ENTITY_TWO = create("entity:2");
	
	public final static URI ENTITY_THREE = create("entity:3");
	
	public final static URI ENTITY_NOT_EXISTS = create("entity:33");
	
	public final static URI ENTITY_NOT_EXISTS_TWO = create("entity:66");
	
	public final static URI ENTITY_FIVE = create("entity:5");
	
	public final static URI COLLECTOR_ONE = create("collector:1");
	
	public final static URI COLLECTOR_TWO = create("collector:2");

	private static URI create(String localName){
		return create(NAMESPACE, localName);
	}
			
}
