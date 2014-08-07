package org.springframework.data.semantic.model.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class MODEL_ENTITY {
	
	public static final String NAMESPACE = "urn:sprind-data-semantic:";
	
	public final static URI ENTITY_ONE = create("entity:1");
	
	public final static URI ENTITY_TWO = create("entity:2");
	
	public final static URI ENTITY_NOT_EXISTS = create("entity:33");

	private static final URI create(String localName) {
		final ValueFactory f = ValueFactoryImpl.getInstance();
		return f.createURI(NAMESPACE, localName);
	}		
			
}
