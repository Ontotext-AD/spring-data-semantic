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
