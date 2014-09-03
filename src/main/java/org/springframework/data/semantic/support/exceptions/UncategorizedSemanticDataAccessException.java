package org.springframework.data.semantic.support.exceptions;

import org.springframework.dao.UncategorizedDataAccessException;

public class UncategorizedSemanticDataAccessException extends UncategorizedDataAccessException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2570244835362801905L;

	public UncategorizedSemanticDataAccessException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}

}
