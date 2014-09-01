package org.springframework.data.semantic.cache;

import org.openrdf.model.URI;

public interface EntityCache {
	
	<T> void remove(T entity);
	
	<T> T get(URI id, Class<? extends T> clazz);
	
	<T> void put(T entity);
	
	<T> void clear(Class<? extends T> clazz);
	
	void clearAll();

}
