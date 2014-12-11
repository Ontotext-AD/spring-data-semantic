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
package org.springframework.data.semantic.support.util;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.URI;
import org.springframework.data.semantic.support.model.SemanticResource;

public class SemanticResourceUtils {
	
	public static List<URI> extractResourceIds(Iterable<? extends SemanticResource> resources){
		List<URI> ids = new LinkedList<URI>();
		for(SemanticResource resource : resources){
			ids.add(resource.getId());
		}
		return ids;
	}

}
