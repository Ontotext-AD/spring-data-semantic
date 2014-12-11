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
