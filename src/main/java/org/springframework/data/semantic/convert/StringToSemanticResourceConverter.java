package org.springframework.data.semantic.convert;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.semantic.support.model.SemanticResource;

public class StringToSemanticResourceConverter implements Converter<String, SemanticResource>{

	private ValueFactory vf = ValueFactoryImpl.getInstance();
	
	@Override
	public SemanticResource convert(String source) {
		SemanticResource dest = new SemanticResource();
		dest.setId(vf.createURI(source));
		return dest;
	}

}
