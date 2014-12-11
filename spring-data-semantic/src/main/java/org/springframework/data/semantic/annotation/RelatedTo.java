/**
 * Copyright (C) 2014 Ontotext AD (info@ontotext.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	Direction direction() default Direction.OUTGOING ;
	
	/**
	 * The mapped property from an associated entity.
	 * @return
	 */
	String mappedProperty() default "";

}
