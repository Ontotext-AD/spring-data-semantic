package org.springframework.data.semantic.support;

public enum Cascade {
	
	/**
	 * Cascade all operations on related entities.
	 */
	ALL, 
	
	/**
	 * Cascade get operations on related entities.
	 */
	GET, 
	
	/**
	 * Cascade save operations on related entities.
	 */
	SAVE, 
	
	/**
	 * Cascade delete operations on related entities.
	 */
	DELETE;

}
