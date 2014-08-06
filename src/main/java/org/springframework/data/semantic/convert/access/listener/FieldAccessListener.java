package org.springframework.data.semantic.convert.access.listener;

/**
 * 
 * @author konstantin.pentchev
 *
 */
public interface FieldAccessListener {
	
	/**
     * callback method after modifying field write operation
     * @param entity
     * @param oldVal
     * @param newVal
     */
	void valueChanged(Object entity, Object oldVal, Object newVal);

}
