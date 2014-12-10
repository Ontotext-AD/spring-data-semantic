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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.support.SemanticTemplateCRUD;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/default-context.xml" })
public class ConfigTest {
	
	@Autowired(required=false)
	SemanticDatabase sdb;
	
	@Autowired(required=false)
	SemanticTemplateCRUD crud;
	
	@Test
	public void testConfiguration() throws Exception{
		assertNotNull(sdb);
	}
	
	@Test
	public void testTemplateInstantiation(){
		assertNotNull(crud);
		//assertNotNull(statementsCollector);
		//assertNotNull(template.getSemanticDatabase());
	}

}
