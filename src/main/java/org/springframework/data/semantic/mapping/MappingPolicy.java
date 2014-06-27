package org.springframework.data.semantic.mapping;


import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class for holding access and load strategy information about a persistent property.
 * @author konstantin.pentchev
 *
 */
public interface MappingPolicy {

    enum Option {
        USE_DIRTY, LAZY_LOAD
    }
    
    /**
     * Determines whether or not the field should be accessed directly or using a {@link FieldAccessor}.
     * @return boolean
     */
    boolean useDirty();
    
    /**
     * Determines whether the field should be eagerly loaded or not (e.g. lazy-loading).
     * @return boolean
     */
    boolean eagerLoad();
    
    /**
     * Returns a new {@link MappingPolicy} that is a OR-intersection of the options of the current and given policies.
     * @param mappingPolicy
     * @return
     */
    MappingPolicy combineWith(MappingPolicy mappingPolicy);
    
    
    /**
     * Default implementation.
     * @author konstantin.pentchev
     *
     */
    public class DefaultMappingPolicy implements MappingPolicy {
        private Set<Option> options;

        public DefaultMappingPolicy(Option... options) {
            this(Arrays.asList(options));
        }

        public DefaultMappingPolicy(final Collection<Option> options) {
            this.options = options.isEmpty() ? EnumSet.noneOf(Option.class) : EnumSet.copyOf(options);
        }

        @Override
        public boolean useDirty() {
            return options.contains(Option.USE_DIRTY);
        }

        @Override
        public boolean eagerLoad() {
            return !options.contains(Option.LAZY_LOAD);
        }

        public MappingPolicy with(Option...options) {
            return with(Arrays.asList(options));
        }

        private MappingPolicy with(Collection<Option> optionsList) {
            Collection<Option> combined =new HashSet<Option>(optionsList);
            combined.addAll(this.options);
            combined.remove(null);
            return new DefaultMappingPolicy(combined);
        }

        public MappingPolicy withOut(Option...options) {
            Collection<Option> combined =new HashSet<Option>(this.options);
            combined.removeAll(Arrays.asList(options));
            return new DefaultMappingPolicy(combined);
        }

        @Override
        public MappingPolicy combineWith(MappingPolicy mappingPolicy) {
            if (mappingPolicy instanceof DefaultMappingPolicy) {
                return with(((DefaultMappingPolicy)mappingPolicy).options);
            }
            return with(mappingPolicy.useDirty() ? Option.USE_DIRTY : null, mappingPolicy.eagerLoad() ? Option.LAZY_LOAD : null);
        }

        @Override
        public String toString() {
            return "Policy: "+options.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            DefaultMappingPolicy that = (DefaultMappingPolicy) o;

            return options.equals(that.options);

        }

        @Override
        public int hashCode() {
            return options.hashCode();
        }
    }

    public MappingPolicy LAZY_LOAD_POLICY = new DefaultMappingPolicy(Option.LAZY_LOAD);
    public MappingPolicy DEFAULT_POLICY = new DefaultMappingPolicy();
    public MappingPolicy MAP_FIELD_DIRECT_POLICY = new DefaultMappingPolicy(Option.USE_DIRTY);
}