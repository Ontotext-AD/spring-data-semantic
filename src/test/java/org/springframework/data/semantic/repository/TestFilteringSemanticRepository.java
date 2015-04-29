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
package org.springframework.data.semantic.repository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.semantic.core.SemanticDatabase;
import org.springframework.data.semantic.modelfilter.LangFilteredEntity;
import org.springframework.data.semantic.modelfilter.LangFilteredRepository;
import org.springframework.data.semantic.model.vocabulary.MODEL_ENTITY;
import org.springframework.data.semantic.testutils.Utils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.data.semantic.filter.ValueFilters.any;
import static org.springframework.data.semantic.filter.ValueFilters.hasLang;

/**
 * Created by itrend on 4/27/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/lang-filter-context.xml" })
public class TestFilteringSemanticRepository {
	@Autowired
	private LangFilteredRepository langFilteredRepository;

	@Autowired
	private SemanticDatabase sdb;

	private static final URI ENTITY_ID = new URIImpl("urn:spring-data-semantic:lang-filter:1");

	@Before
	public void before() throws RepositoryException {
		Utils.populateTestRepository(sdb);
		sdb.addNamespace("", MODEL_ENTITY.NAMESPACE);
	}

	@After
	public void after() {
		sdb.clear();
	}


	@Test
	public void withoutLangFilter() {
		LangFilteredEntity lfe = langFilteredRepository.findOne(ENTITY_ID);
		Assert.assertNotNull(lfe);
		Assert.assertFalse("There isn't a main label", StringUtils.isEmpty(lfe.getMainLabel()));
		Assert.assertFalse("There isn't an optional label", StringUtils.isEmpty(lfe.getOptionalLabel()));
		Assert.assertEquals("Not all optional labels retrieved", 3, lfe.getMultiLabel().size());
		checkIgnoreLanguages(lfe);
	}


	@Test
	public void emptyLanguage() {
		checkForLanguage("", "No Lang");
	}


	@Test
	public void singleLanguage() {
		checkForLanguage("en", "English");
		checkForLanguage("fr", "Francias");
	}


	private void checkForLanguage(String languageCode, String langInData) {
		LangFilteredEntity lfe = langFilteredRepository.findOne(ENTITY_ID, hasLang(languageCode));
		Assert.assertNotNull(lfe);
		Assert.assertEquals("Unexpected main label", langInData, lfe.getMainLabel());
		Assert.assertEquals("Unexpected optional label", "Optional " + langInData, lfe.getOptionalLabel());
		final List<String> expectedMultiLabel = Collections.singletonList("Multi " + langInData);
		Assert.assertEquals("Unexpected multi label", expectedMultiLabel, lfe.getMultiLabel());
		checkIgnoreLanguages(lfe);
	}

	private void checkIgnoreLanguages(LangFilteredEntity lfe) {
		final List<String> expectedIgnores = Arrays.asList("Ignore No Lang", "Ignore English", "Ignore Francias");
		Assert.assertEquals("Unexpected ignore languages property value", expectedIgnores, lfe.getIgnoreLang());
	}

	@Test
	public void multipleLanguages() {


		//The english one
		LangFilteredEntity lfe1 = langFilteredRepository.findOne(ENTITY_ID, any(hasLang("en"), hasLang("fr")));
		Assert.assertNotNull(lfe1);
		Assert.assertFalse("There isn't a main label", StringUtils.isEmpty(lfe1.getMainLabel()));
		Assert.assertTrue("Main label is not correct", "English".equals(lfe1.getMainLabel()));
		Assert.assertFalse("There isn't an optional label", StringUtils.isEmpty(lfe1.getOptionalLabel()));
		final List<String> expectedMultiLabel1 = Arrays.asList("Multi English", "Multi Francias");
		Assert.assertEquals("Unexpected multi label", expectedMultiLabel1, lfe1.getMultiLabel());
		checkIgnoreLanguages(lfe1);

		//The french one
		LangFilteredEntity lfe2 = langFilteredRepository.findOne(ENTITY_ID, any(hasLang("fr"), hasLang("en")));
		Assert.assertNotNull(lfe2);
		Assert.assertFalse("There isn't a main label", StringUtils.isEmpty(lfe2.getMainLabel()));
		Assert.assertTrue("Main label is not correct", "Francias".equals(lfe2.getMainLabel()));
		Assert.assertFalse("There isn't an optional label", StringUtils.isEmpty(lfe2.getOptionalLabel()));
		final List<String> expectedMultiLabel2 = Arrays.asList("Multi Francias", "Multi English");
		Assert.assertEquals("Unexpected multi label", expectedMultiLabel2, lfe2.getMultiLabel());
		checkIgnoreLanguages(lfe2);


		//No lang preferred
		LangFilteredEntity lfe3 = langFilteredRepository.findOne(ENTITY_ID, any(hasLang(""), hasLang("en")));
		Assert.assertNotNull(lfe3);
		Assert.assertFalse("There isn't a main label", StringUtils.isEmpty(lfe3.getMainLabel()));
		Assert.assertTrue("Main label is not correct", "No Lang".equals(lfe3.getMainLabel()));
		Assert.assertFalse("There isn't an optional label", StringUtils.isEmpty(lfe3.getOptionalLabel()));
		final List<String> expectedMultiLabel3 = Arrays.asList("Multi No Lang", "Multi English");
		Assert.assertEquals("Unexpected multi label", expectedMultiLabel3, lfe3.getMultiLabel());
		checkIgnoreLanguages(lfe3);
	}


}
