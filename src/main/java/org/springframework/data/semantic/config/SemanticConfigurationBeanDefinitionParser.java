package org.springframework.data.semantic.config;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.w3c.dom.Element;

public class SemanticConfigurationBeanDefinitionParser extends
		AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element,
			ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.rootBeanDefinition(SemanticConfiguration.class);
		builder.addPropertyReference("semanticDatabase", element.getAttribute("semantic-database-ref"));
		builder.setAutowireMode(Autowire.BY_TYPE.value());
		setupConfigurationClassPostProcessor(parserContext);
		return getSourcedBeanDefinition(builder, element, parserContext);
	}
	
	private AbstractBeanDefinition getSourcedBeanDefinition(
			BeanDefinitionBuilder builder, Element source, ParserContext context) {

		AbstractBeanDefinition definition = builder.getBeanDefinition();
		definition.setSource(context.extractSource(source));
		return definition;
	}
	
	private void setupConfigurationClassPostProcessor(final ParserContext parserContext) {
        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();

        BeanDefinitionBuilder configurationClassPostProcessor = BeanDefinitionBuilder.rootBeanDefinition(ConfigurationClassPostProcessor.class);
        BeanNameGenerator beanNameGenerator = parserContext.getReaderContext().getReader().getBeanNameGenerator();
        AbstractBeanDefinition configurationClassPostProcessorBeanDefinition = configurationClassPostProcessor.getBeanDefinition();
        String beanName = beanNameGenerator.generateBeanName(configurationClassPostProcessorBeanDefinition, beanDefinitionRegistry);
        beanDefinitionRegistry.registerBeanDefinition(beanName, configurationClassPostProcessorBeanDefinition);
    }

    @Override
    protected boolean shouldGenerateId() {
        return true;
    }

}
