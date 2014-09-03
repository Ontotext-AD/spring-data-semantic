package org.springframework.data.semantic.support.exceptions;

public class SemanticDatabaseAccessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5823149782104175848L;
	
	public SemanticDatabaseAccessException(Throwable e){
		super(e);
	}

}
