package org.springframework.data.semantic.convert.access.listener;

import java.util.List;

import org.springframework.data.semantic.convert.access.FieldAccessor;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

public interface FieldAccessListenerFactory {
	
	/**
	 * Checks whether the current implementation is to be used for creating a {@link FieldAccessor} for the provided {@link SemanticPersistentProperty}.
	 * @param property
	 * @return
	 */
	boolean accept(SemanticPersistentProperty property);

	/**
	 * Creates a {@link FieldAccessor} for the given {@link SemanticPersistentProperty}.
	 * @param property
	 * @return
	 */
	List<FieldAccessListener> forField(SemanticPersistentProperty property);

}
