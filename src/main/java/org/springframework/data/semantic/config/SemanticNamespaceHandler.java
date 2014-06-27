package org.springframework.data.semantic.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.data.repository.config.RepositoryBeanDefinitionParser;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

public class SemanticNamespaceHandler extends NamespaceHandlerSupport{

	@Override
	public void init() {
		RepositoryConfigurationExtension extension = new SemanticRepositoryConfigExtension();
		RepositoryBeanDefinitionParser repositoryParser = new RepositoryBeanDefinitionParser(extension);
		registerBeanDefinitionParser("repositories", repositoryParser);
		registerBeanDefinitionParser("semantic-database", new SemanticDatabaseBeanDefinitionParser());
		registerBeanDefinitionParser("config",  new SemanticConfigurationBeanDefinitionParser());
		
	}
}
