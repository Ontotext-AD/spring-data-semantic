package org.springframework.data.semantic.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.semantic.core.SemanticDatabaseFactoryBean;
import org.w3c.dom.Element;

public class SemanticDatabaseBeanDefinitionParser extends
		AbstractBeanDefinitionParser {
	
	public static final String CONFIGURATION_DEF = "semanticConfiguration";

	@Override
	protected AbstractBeanDefinition parseInternal(Element element,
			ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.rootBeanDefinition(SemanticDatabaseFactoryBean.class);
		initSemanticDatabaseFactory(element, builder);
		//initConfiguration(element, parserContext);
		return getSourcedBeanDefinition(builder, element, parserContext);
	}

	private void initSemanticDatabaseFactory(Element element,
			BeanDefinitionBuilder builder) {
		builder.addPropertyValue("url", element.getAttribute("url"));
		if (element.hasAttribute("username")) {
			builder.addPropertyValue("username",
					element.getAttribute("username"));
		}
		if (element.hasAttribute("password")) {
			builder.addPropertyValue("password",
					element.getAttribute("password"));
		}
		builder.addPropertyValue("configFile",
					element.getAttribute("configFile"));
		builder.addPropertyValue("maxConnections",
				element.getAttribute("maxConnections"));
		if (element.hasAttribute("defaultNamespace")) {
			builder.addPropertyValue("defaultNamespace",
					element.getAttribute("defaultNamespace"));
		}
		
	}

	private AbstractBeanDefinition getSourcedBeanDefinition(
			BeanDefinitionBuilder builder, Element source, ParserContext context) {

		AbstractBeanDefinition definition = builder.getBeanDefinition();
		definition.setSource(context.extractSource(source));
		return definition;
	}

}
