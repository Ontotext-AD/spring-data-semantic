package org.springframework.data.semantic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.semantic.support.Cascade;

/**
 * Annotation for load/fetch strategies, if it is present at a field it will be lazy loaded
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Fetch {
	
	Cascade[] value() default {Cascade.GET};

}
