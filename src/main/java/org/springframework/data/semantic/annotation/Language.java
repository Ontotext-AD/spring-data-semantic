package org.springframework.data.semantic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying a language associated with a literal value. 
 * Will be used both for filtering at retrieval and for serialization.
 * @author konstantin.pentchev
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Language {
	
	Languages[] value(); 

	
	public enum Languages{
		en, de, fr, sp, bg, cz, it;
	}
	
}
