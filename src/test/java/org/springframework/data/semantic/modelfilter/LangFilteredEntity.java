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
package org.springframework.data.semantic.modelfilter;

import org.openrdf.model.URI;
import org.springframework.data.semantic.annotation.Optional;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.annotation.SemanticEntity;

import java.util.List;

@SemanticEntity(rdfType="urn:spring-data-semantic:lang-filtered-entity")
public class LangFilteredEntity {
	@ResourceId
	private URI uri;

	private String mainLabel;
	@Optional
	private String optionalLabel;
	private List<String> multiLabel;
	private URI aUri;

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getMainLabel() {
		return mainLabel;
	}

	public void setMainLabel(String label) {
		this.mainLabel = label;
	}

	public String getOptionalLabel() {
		return optionalLabel;
	}

	public void setOptionalLabel(String optionalLabel) {
		this.optionalLabel = optionalLabel;
	}

	public List<String> getMultiLabel() {
		return multiLabel;
	}

	public void setMultiLabel(List<String> multiLabel) {
		this.multiLabel = multiLabel;
	}

	public URI getaUri() {
		return aUri;
	}

	public void setaUri(URI aUri) {
		this.aUri = aUri;
	}
}
