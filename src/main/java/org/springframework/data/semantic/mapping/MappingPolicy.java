package org.springframework.data.semantic.mapping;


import org.springframework.data.semantic.convert.access.FieldAccessor;
import org.springframework.data.semantic.support.Cascade;

/**
 * Helper class for holding access and load strategy information about a persistent property.
 * @author konstantin.pentchev
 *
 */
public interface MappingPolicy {

    enum Option {
        USE_DIRTY
    }
    
    /**
     * Determines whether or not the field should be accessed directly or using a {@link FieldAccessor}.
     * @return boolean
     */
    boolean useDirty();
    
    boolean shouldCascade(Cascade cascade);
    
    MappingPolicy combineWith(MappingPolicy other);
}