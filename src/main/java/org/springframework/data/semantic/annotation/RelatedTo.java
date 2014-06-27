package org.springframework.data.semantic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.annotation.Reference;
import org.springframework.data.semantic.support.Direction;

/**
 * Declares a relationship to another entity. Can be used for One-To-One and One-To-Many relationships. Can specify the direction of the relationship.
 * @author konstantin.pentchev
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
@Reference
public @interface RelatedTo {
	
	/**
	 * @return the direction of the relation.
	 */
	Direction dicrection() default Direction.OUTGOING ;

}
