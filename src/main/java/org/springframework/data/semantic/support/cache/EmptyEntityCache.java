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
