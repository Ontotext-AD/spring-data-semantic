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
package org.springframework.data.semantic.model;

import org.openrdf.model.URI;
import org.springframework.data.semantic.annotation.Namespace;
import org.springframework.data.semantic.annotation.Predicate;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.annotation.SemanticEntity;

@Namespace(namespace="urn:test:namespace:")
@SemanticEntity
public class NamespaceEntity {
	
	@ResourceId
	private URI id;
	
	private String name;

	@Predicate("urn:really:absolute")
	private String withAbsolutePredicate;

	@Predicate("relative")
	private String withRelativePredicate;

	/**
	 * @return the id
	 */
	public URI getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(URI id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getWithAbsolutePredicate() {
		return withAbsolutePredicate;
	}

	public void setWithAbsolutePredicate(String withAbsolutePredicate) {
		this.withAbsolutePredicate = withAbsolutePredicate;
	}

	public String getWithRelativePredicate() {
		return withRelativePredicate;
	}

	public void setWithRelativePredicate(String withRelativePredicate) {
		this.withRelativePredicate = withRelativePredicate;
	}
}
