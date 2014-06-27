package org.springframework.data.semantic.convert.fieldaccess;

import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

/**
 * Factory interface for creating {@link FieldAccessor}s.
 * @author konstantin.pentchev
 *
 */
public interface FieldAccessorFactory {
	
	/**
	 * Checks whether the current implementation is to be used for creating a {@link FieldAccessor} for the provided {@link SemanticPersistentProperty}.
	 * @param property
	 * @return
	 */
	public boolean accept(SemanticPersistentProperty property);

	/**
	 * Creates a {@link FieldAccessor} for the given {@link SemanticPersistentProperty}.
	 * @param property
	 * @return
	 */
	public FieldAccessor forField(SemanticPersistentProperty property);

}
