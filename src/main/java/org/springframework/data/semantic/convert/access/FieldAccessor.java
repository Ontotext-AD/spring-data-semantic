package org.springframework.data.semantic.convert.access;


import org.springframework.data.semantic.mapping.MappingPolicy;


/**
 * Interface defining common operations to be performed on fields.
 * @author konstantin.pentchev
 *
 */
public interface FieldAccessor {
	
	/**
	 * Returns a default implementation for a field or {@code null} if none is provided.
	 * <p>
	 * This default implementation is returned in detached mode by the {@link DetachedEntityState}
	 * when the value of the field is get. 
	 * @return a default implementation for a field or {@code null} if none is provided.
	 */
	Object getDefaultValue();
	
    /**
     * handles field write modification.
     *
     * @param entity
     * @param newVal
     * @param mappingPolicy
     * @return the written value or a DoReturn wrapper with the written value or null.
     * DoReturn indicates that the aspect should not proceed to the original field access but instead return immediately.
     */
	Object setValue(Object entity, Object newVal, MappingPolicy mappingPolicy);

    /**
     *
     * @param entity
     * @param mappingPolicy
     * @return the value or a DoReturn wrapper with the value for the field.
     * DoReturn indicates that the aspect should not proceed to the original field access but instead return immediately.
     */
	Object getValue(Object entity, MappingPolicy mappingPolicy);

    /**
     * @param entity
     * @return false for read only or computed fields, true otherwise
     */
    boolean isWritable(Object entity);

}
