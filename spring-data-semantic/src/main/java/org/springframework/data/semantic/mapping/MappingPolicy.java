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
package org.springframework.data.semantic.mapping;


import java.util.Set;

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
    
    Set<Cascade> getCascades();
    
    MappingPolicy combineWith(MappingPolicy other);
}