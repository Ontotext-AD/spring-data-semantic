package org.springframework.data.semantic.model.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class VOCABULARY {

	private static final ValueFactory f = ValueFactoryImpl.getInstance();
	
	protected static final URI create(String namespace, String localName) {
		return f.createURI(namespace, localName);
	}

}
