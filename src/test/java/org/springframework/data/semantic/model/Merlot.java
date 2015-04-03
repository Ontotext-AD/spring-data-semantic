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
import org.springframework.data.semantic.annotation.*;
import org.springframework.data.semantic.model.vocabulary.WINE;

@Namespace(namespace = WINE.NAMESPACE)
@SemanticEntity
public class Merlot {
	
	@ResourceId
	private URI uri;
	
	@RelatedTo
	@Predicate("hasBody")
	private WineBody body;
	
	@Predicate("hasFlavor")
	private String flavor;
	
	@Predicate("hasMaker")
	private String maker;
	
	@Predicate("hasSugar")
	private String sugar;
		
	@Predicate("locatedIn")
	private String location;
	
	@Predicate("hasYear")
	private int year;
	
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	public WineBody getBody() {
		return body;
	}
	public void setBody(WineBody body) {
		this.body = body;
	}

	public String getFlavor() {
		return flavor;
	}
	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}

	public String getMaker() {
		return maker;
	}
	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getSugar() {
		return sugar;
	}
	public void setSugar(String sugar) {
		this.sugar = sugar;
	}

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

}
