package org.springframework.data.semantic.convert;

import java.util.Date;

import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.core.convert.converter.Converter;

public class ObjectToLiteralConverter implements Converter<Object, Literal> {

	private final ValueFactory factory = ValueFactoryImpl.getInstance();
	
	@Override
	public Literal convert(Object source) {
		if(source instanceof Date){
			return factory.createLiteral((Date) source);
		}
		else if(source instanceof Long){
			return factory.createLiteral((Long) source);
		}
		else if(source instanceof Integer){
			return factory.createLiteral((Integer) source);
		}
		else if(source instanceof Short){
			return factory.createLiteral((Short) source);
		}
		else if(source instanceof Float){
			return factory.createLiteral((Float) source);
		}
		else if(source instanceof Double){
			return factory.createLiteral((Double) source);
		}
		else {
			return factory.createLiteral(source.toString());
		}
	}

}
