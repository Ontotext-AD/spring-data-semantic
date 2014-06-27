package org.springframework.data.semantic.mapping;

import java.util.List;

import org.openrdf.model.URI;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.semantic.annotation.Context;

public interface SemanticPersistentProperty extends PersistentProperty<SemanticPersistentProperty>{
	
	/*Statement getStatement();
	
	public enum PropertyToStatementConverter implements Converter<SemanticPersistentProperty, Statement> {
		INSTACNE;
		
		public Statement convert(SemanticPersistentProperty source) {
			return source.getStatement();
		}
		
	}*/
	
	/**
	 * Checks whether the property has an explicit predicate associated with it.
	 * @return
	 */
	public boolean hasPredicate();
	
	/**
	 * Returns the associated predicate. This operation includes resolving of namespace.
	 * @return
	 */
	public List<URI> getPredicate();
	
	/**
	 * Returns a dummy/alias predicate to be used in CONSTRUCT query bindings.
	 * @return
	 */
	public String getAliasPredicate();
	
	/**
	 * Checks whether the property has a language associated with it.
	 * @return
	 */
	public boolean hasLanguage();
	
	/**
	 * Returns the associated languages or an empty {@link List}.
	 * @return
	 */
	public List<String> getLanguage();
	
	/**
	 * Checks whether the property has an associated XSD datatype.
	 * @return
	 */
	public boolean hasDatatype();
	
	/**
	 * Checks whether the property is annotated as {@link Context}.
	 * @return
	 */
	public boolean isContext();
	
	
	/**
	 * Returns the associated XSD datatype or null.
	 * @return
	 */
	public String getDatatype();
	
	/**
	 * Returns the value of the property field. Based on the {@link MappingPolicy} this can be the (dirty) value from memory or the (queried) value from the store. 
	 * IdProperties always return from memory.
	 * @param entity
	 * @param mappingPolicy
	 * @return
	 */
	public Object getValue(Object entity, MappingPolicy mappingPolicy);
	
	/**
	 * Set the property value of the given entity to the given new value.
	 * @param entity
	 * @param newValue
	 */
	public void setValue(Object entity, Object newValue) ;

	/**
	 * Return the property-specific {@link MappingPolicy}.
	 * @return
	 */
	public MappingPolicy getMappingPolicy();
	
	
}
