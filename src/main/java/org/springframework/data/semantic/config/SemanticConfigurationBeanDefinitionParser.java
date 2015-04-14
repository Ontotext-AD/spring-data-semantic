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
		if(element.hasAttribute("semantic-database-ref")){
			builder.addPropertyReference("semanticDatabase", element.getAttribute("semantic-database-ref"));
		}
		if(element.hasAttribute("explicit-supertypes")){
			builder.addPropertyValue("explicitSupertypes", element.getAttribute("explicit-supertypes"));
		}
		
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
