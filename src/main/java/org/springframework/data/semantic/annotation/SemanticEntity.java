package org.springframework.data.semantic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.annotation.Persistent;

/**
 * Annotation to declare a Pojo-Entity.
 * @author konstantin.pentchev
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Persistent
public @interface SemanticEntity {
	
	String rdfType() default "";

}
