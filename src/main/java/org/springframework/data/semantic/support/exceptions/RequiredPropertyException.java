package org.springframework.data.semantic.support.exceptions;

import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public class RequiredPropertyException extends RuntimeException{
	
	public RequiredPropertyException(SemanticPersistentProperty persistentProperty){
		super("The required property "+persistentProperty.getName() + " is null or empty. Please provide a value for it or mark it as @Opional.");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3785673302471683649L;

}
