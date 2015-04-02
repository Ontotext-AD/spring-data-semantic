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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.semantic.model.DateEntityRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * A test verifying spring-data-semantic will not throw on initialization when
 * an invalid (or not yet valid) http URL is passed for repository.
 *
 * See https://jira.ontotext.com/browse/DSP-705 for motivation
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/lazy-init-context.xml" })
public class TestLazyInitializationDSP705 {
	@Autowired
	private DateEntityRepository dateEntityRepository;

	/**
	 * If the context doesn't load - the entire class will report error and the test will
	 * effectively fail. The repository is expected to fail when an actual operation is performed
	 */
	@Test(expected = DataAccessResourceFailureException.class)
	public void noop() {
		dateEntityRepository.findAll();
	}
}
