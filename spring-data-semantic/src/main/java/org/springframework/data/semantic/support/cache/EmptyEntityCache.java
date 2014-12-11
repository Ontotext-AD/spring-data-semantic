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
package org.springframework.data.semantic.support.cache;

import org.openrdf.model.URI;
import org.springframework.data.semantic.cache.EntityCache;

public class EmptyEntityCache implements EntityCache {

	@Override
	public <T> void remove(T entity) {
		return;
	}

	@Override
	public <T> T get(URI id, Class<? extends T> clazz) {
		return null;
	}

	@Override
	public <T> void put(T entity) {
		return;
	}

	@Override
	public <T> void clear(Class<? extends T> clazz) {
		return;
	}

	@Override
	public void clearAll() {
		return;
	}

}
