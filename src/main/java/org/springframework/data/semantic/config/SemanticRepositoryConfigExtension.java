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

import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.semantic.repository.SemanticRepositoryFactoryBean;

public class SemanticRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

	@Override
	public String getRepositoryFactoryClassName() {
		return SemanticRepositoryFactoryBean.class.getName();
	}

	@Override
	protected String getModulePrefix() {
		return "semantic";
	}
	
	/*@Override
	public void postProcess(BeanDefinitionBuilder builder,
			XmlRepositoryConfigurationSource config) {
		initSemanticTemplate();
	}
	
	private void initSemanticTemplate(){
		GenericBeanDefinition beanDef = new GenericBeanDefinition();
		beanDef.setBeanClass(SemanticTemplate.class);
		ConstructorArgumentValues ctorArgs = new ConstructorArgumentValues();
		ctorArgs.addGenericArgumentValue(new RuntimeBeanReference("beanName"));
		
		beanDef.setConstructorArgumentValues(constructorArgumentValues)
	}*/

}
