package org.springframework.data.semantic.support.cache;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

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
		if(entity != null && entity instanceof Serializable){
			Ehcache cache = getCache(entity.getClass());
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
			CacheConfiguration config = new CacheConfiguration(cacheName, 1000).copyOnRead(true).copyOnWrite(true);
			cache = new Cache(config);
			cacheManager.addCache(cache);
		}
		return cache;
	}

}
