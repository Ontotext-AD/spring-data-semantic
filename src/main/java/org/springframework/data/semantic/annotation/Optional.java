package org.springframework.data.semantic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying if a property is optional. 
 * Will be used both for generating queries at retrieval.
 * @author konstantin.pentchev
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Optional {

}
