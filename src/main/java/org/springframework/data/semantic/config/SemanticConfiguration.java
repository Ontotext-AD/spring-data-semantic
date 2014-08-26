package org.springframework.data.semantic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.semantic.convert.StringToDateConverter;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticExceptionTranslator;
import org.springframework.data.semantic.support.SemanticTemplateCRUD;

@Configuration
public class SemanticConfiguration {
	
	private SemanticDatabase semanticDatabase;
	
	@Autowired(required=false)
	public void setSemanticDatabase(SemanticDatabase semanticDatabase) {
		this.semanticDatabase = semanticDatabase;
	}
	
	
	@Bean
	public SemanticTemplateCRUD semanticTemplateCRUD() {
		return new SemanticTemplateCRUD(semanticDatabase, conversionService());
	}
	
	@Bean
	public ConversionService conversionService() {
		DefaultConversionService conversionService = new DefaultConversionService();
		conversionService.addConverter(new StringToDateConverter());
		return conversionService;
	}
	
	@Bean
    public PersistenceExceptionTranslator persistenceExceptionTranslator() {
		return new SemanticExceptionTranslator();
	}
}
