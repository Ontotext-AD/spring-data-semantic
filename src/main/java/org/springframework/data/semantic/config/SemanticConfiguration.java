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
package org.springframework.data.semantic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.semantic.convert.StringToDateConverter;
import org.springframework.data.semantic.convert.StringToSemanticResourceConverter;
import org.springframework.data.semantic.convert.StringToUriConverter;
import org.springframework.data.semantic.convert.StringToXMLGregorianCalendarConverter;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticExceptionTranslator;
import org.springframework.data.semantic.support.SemanticTemplateCRUD;

@Configuration
public class SemanticConfiguration {
	
	private SemanticDatabase semanticDatabase;
	
	private boolean explicitSupertypes = true;
	
	@Autowired(required=false)
	public void setSemanticDatabase(SemanticDatabase semanticDatabase) {
		this.semanticDatabase = semanticDatabase;
	}
	
	public void setExplicitSupertypes(boolean explicitSupertypes) {
		this.explicitSupertypes = explicitSupertypes;
	}
	
	
	@Bean
	public SemanticTemplateCRUD semanticTemplateCRUD() {
		return new SemanticTemplateCRUD(semanticDatabase, conversionService(), explicitSupertypes);
	}
	
	@Bean
	public ConversionService conversionService() {
		DefaultConversionService conversionService = new DefaultConversionService();
		conversionService.addConverter(new StringToDateConverter());
		conversionService.addConverter(new StringToUriConverter());
		conversionService.addConverter(new StringToSemanticResourceConverter());
        conversionService.addConverter(new StringToXMLGregorianCalendarConverter());
		return conversionService;
	}
	
	@Bean
    public PersistenceExceptionTranslator persistenceExceptionTranslator() {
		return new SemanticExceptionTranslator();
	}
}
