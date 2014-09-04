package org.springframework.data.semantic.convert;

import org.openrdf.model.URI;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.semantic.support.util.ValueUtils;

public class StringToUriConverter implements Converter<String, URI> {

	
	@Override
	public URI convert(String source) {
		return ValueUtils.createUri(source);
	}

}
