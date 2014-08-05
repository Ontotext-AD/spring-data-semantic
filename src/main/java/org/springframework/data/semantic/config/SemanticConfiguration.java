package org.springframework.data.semantic.config;

import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.core.SemanticExceptionTranslator;
import org.springframework.data.semantic.support.SemanticTemplateCRUD;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

@Configuration
public class SemanticConfiguration {
	
	private SemanticDatabase semanticDatabase;
	
	@Autowired(required=true)
	public void setSemanticDatabase(SemanticDatabase semanticDatabase) {
		this.semanticDatabase = semanticDatabase;
	}
	
	
	@Bean
	public SemanticTemplateCRUD semanticTemplateCRUD() {
		return new SemanticTemplateCRUD(semanticDatabase, conversionService());
	}
	
	@Bean
	public SemanticTemplateStatementsCollector semanticTemplateStatementsCollector() throws RepositoryException {
		return new SemanticTemplateStatementsCollector(semanticDatabase, conversionService(), 
				new SemanticMappingContext(semanticDatabase.getNamespaces(), semanticDatabase.getDefaultNamespace()));
	}
	
	@SuppressWarnings("deprecation")
	@Bean
	public ConversionService conversionService() {
		GenericConversionService conversionService = new GenericConversionService();
		/*if (conversionService instanceof ConverterRegistry) {
            ConverterRegistry registry = (ConverterRegistry) conversionService;
            registry.addConverter(new DateToStringConverter());
            registry.addConverter(new DateToLongConverter());
            registry.addConverter(new StringToDateConverter());
            registry.addConverter(new NumberToDateConverter());
            registry.addConverter(new EnumToStringConverter());
            registry.addConverterFactory(new StringToEnumConverterFactory());
        }*/
		ConversionServiceFactory.addDefaultConverters(conversionService);
		return conversionService;
	}
	
	@Bean
    public PersistenceExceptionTranslator persistenceExceptionTranslator() {
		return new SemanticExceptionTranslator();
	}
}
