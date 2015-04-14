/**
 * Copyright (C) 2014 Ontotext AD (info@ontotext.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.semantic.convert;

import java.util.Date;

import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.core.convert.converter.Converter;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * 
 * @author konstantin.pentchev
 *
 */
public class ObjectToLiteralConverter implements Converter<Object, Value> {
	
	private static ObjectToLiteralConverter INSTANCE;

	private final ValueFactory factory; 
	
	private ObjectToLiteralConverter() {
		super();
		factory = ValueFactoryImpl.getInstance();
	}
	
	public static ObjectToLiteralConverter getInstance(){
		if(INSTANCE == null){
			synchronized (ObjectToLiteralConverter.class) {
				if(INSTANCE == null){
					INSTANCE = new ObjectToLiteralConverter();
				}
			}
		}
		return INSTANCE;
	}
	
	@Override
	public Value convert(Object source) {
		if(source instanceof Value){
			return (Value) source;
		}
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
		else if(source instanceof String){
			return factory.createLiteral((String) source);
		}
		else if(source instanceof XMLGregorianCalendar){
			return factory.createLiteral((XMLGregorianCalendar)source);
		}
		else {
			return factory.createLiteral(source.toString());
		}
	}
}
