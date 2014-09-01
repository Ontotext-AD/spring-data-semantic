package org.springframework.data.semantic.support.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.openrdf.model.URI;
import org.springframework.data.semantic.cache.EntityCache;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class EhCacheEntityCache implements EntityCache {
	
	private CacheManager cacheManager;
	private SemanticMappingContext mappingContext;

	public EhCacheEntityCache(SemanticMappingContext mappingContext, CacheManager cacheManager) {
		this.mappingContext = mappingContext;
		this.cacheManager = cacheManager;
	}

	
	@Override
	public <T> void remove(T entity) {
		Ehcache cache = getCache(entity.getClass());
		cache.remove(getId(entity).toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(URI id, Class<? extends T> clazz) {
		Ehcache cache = getCache(clazz);
		Element element = cache.get(id.toString());
		if(element != null){
			Object value = element.getObjectValue();
			if(clazz.isAssignableFrom(value.getClass())){
				return (T) value;
			}
		}
		return null;
	}

	@Override
	public <T> void put(T entity) {
		Ehcache cache = getCache(entity.getClass());
		if(entity != null){
			cache.put(new Element(getId(entity).toString(), entity));
		}
	}

	@Override
	public <T> void clear(Class<? extends T> clazz){
		Ehcache cache = getCache(clazz);
		cache.removeAll();
	}
	
	@Override
	public void clearAll() {
		cacheManager.clearAll();
	}
	
	private URI getId(Object entity){
		SemanticPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(entity.getClass());
		return persistentEntity.getResourceId(entity);
	}
	
	private Ehcache getCache(Class<?> clazz){
		String cacheName = clazz.getSimpleName();
		Ehcache cache = cacheManager.getCache(cacheName);
		if(cache == null){
			cacheManager.addCache(cacheName);
		}
		return cacheManager.getCache(cacheName);
	}



}
