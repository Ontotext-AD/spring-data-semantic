package org.springframework.data.semantic.support.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.semantic.annotation.SemanticEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.ValueUtils;
import org.springframework.data.util.TypeInformation;

/**
 * 
 * @author konstantin.pentchev
 *
 */
public class SemanticMappingContext  extends AbstractMappingContext<SemanticPersistentEntityImpl<?>, SemanticPersistentProperty>{
	private static Set<Class<?>> simpleTypes = new HashSet<Class<?>>();
	static {
		simpleTypes.add(URI.class);
	}
	
	private Namespace defaultNS;
	private Map<String, String> prefix2Namespace; 
	
	/*public SemanticMappingContext(){
		super();
		setSimpleTypeHolder(new SimpleTypeHolder(simpleTypes, true));
		prefix2Namespace = new HashMap<String, String>();
	}*/
	
	public SemanticMappingContext(List<? extends Namespace> namespaces, Namespace defaultNS){
		super();
		setSimpleTypeHolder(new SimpleTypeHolder(simpleTypes, true));
		this.prefix2Namespace = new HashMap<String, String>();
		for(Namespace ns : namespaces){
			this.prefix2Namespace.put(ns.getPrefix(), ns.getName());
		}
		this.defaultNS = defaultNS;
	}

	@Override
	protected <T> SemanticPersistentEntityImpl<T> createPersistentEntity(
			TypeInformation<T> typeInformation) {
		final Class<T> type = typeInformation.getType();
        if (type.isAnnotationPresent(SemanticEntity.class)) {
            return new SemanticPersistentEntityImpl<T>(typeInformation, this);
        }
        throw new IllegalArgumentException("Type " + type + " is not a @SemanticEntity!"); //TODO new Exception type to be used
	}

	@Override
	protected SemanticPersistentProperty createPersistentProperty(Field field,
			PropertyDescriptor descriptor,
			SemanticPersistentEntityImpl<?> owner,
			SimpleTypeHolder simpleTypeHolder) {
		return new SemanticPersistentPropertyImpl(field, descriptor, owner, simpleTypeHolder, this);
	}
	
	/**
	 * Resolves a {@link String} to an {@link URI}. Check for absolute URI, otherwise try to use defined namespace prefix or use default namespace.
	 * @param predicate
	 * @return
	 */
	public URI resolveURI(String predicate){
		if(ValueUtils.isAbsoluteURI(predicate)){
			return new URIImpl(predicate);
		}
		else{
			String[] parts = predicate.split(":");
			if(parts.length != 2){
				if(parts.length == 1){
					return resolveURIDefaultNS(predicate);
				}
				throw new IllegalArgumentException("Not a valid relative URI '"+predicate+"'!");
			}
			String ns = prefix2Namespace.get(parts[0]);
			if(ns == null){
				throw new IllegalArgumentException("Unknown namespace for prefix '"+parts[0]+"'!");
			}
			return new URIImpl(ns+parts[1]);
		}
	}
	
	/**
	 * Resolves a {@link String} to an {@link URI} using the default namespace.
	 * @param lName
	 * @return
	 */
	public URI resolveURIDefaultNS(String lName){
		return new URIImpl(defaultNS.getName()+lName);
	}

}
