package org.springframework.data.semantic.convert;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.core.convert.converter.Converter;

public class StringToUriConverter implements Converter<String, URI> {

	private final ValueFactory factory = ValueFactoryImpl.getInstance();
	
	@Override
	public URI convert(String source) {
		return factory.createURI(source);
	}

}
