package org.springframework.data.semantic.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.support.SemanticTemplateCRUD;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/default-context.xml" })
public class ConfigTest {
	
	@Autowired(required=false)
	SemanticDatabase sdb;
	
	@Autowired(required=false)
	SemanticTemplateCRUD crud;
	
	@Autowired(required=false)
	SemanticTemplateStatementsCollector statementsCollector;
	
	@Test
	public void testConfiguration() throws Exception{
		assertNotNull(sdb);
	}
	
	@Test
	public void testTemplateInstantiation(){
		assertNotNull(crud);
		assertNotNull(statementsCollector);
		//assertNotNull(template.getSemanticDatabase());
	}

}
