package org.springframework.data.semantic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows the explicit definition of a predicate to be used for retrival and persistence.
 * @author konstantin.pentchev
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Predicate {
	
	/**
	 * @return the predicate to be used.
	 */
	String[] value();

}
