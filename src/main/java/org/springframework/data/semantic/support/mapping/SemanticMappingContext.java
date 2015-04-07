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
package org.springframework.data.semantic.support.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.semantic.annotation.SemanticEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.util.ValueUtils;
import org.springframework.data.util.TypeInformation;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author konstantin.pentchev
 *
 */
public class SemanticMappingContext  extends AbstractMappingContext<SemanticPersistentEntity<?>, SemanticPersistentProperty>{
        private static Set<Class<?>> simpleTypes = new HashSet<Class<?>>();
        static {
                simpleTypes.add(Value.class);
                simpleTypes.add(Period.class);
                simpleTypes.add(Duration.class);
                simpleTypes.add(Interval.class);
                simpleTypes.add(Instant.class);
                simpleTypes.add(XMLGregorianCalendar.class);
        }

        private Namespace defaultNS;
        private Map<String, String> prefix2Namespace;
        private final boolean explicitSupertypes;

        public SemanticMappingContext(List<? extends Namespace> namespaces, Namespace defaultNS, boolean explicitSupertypes){
                super();
                setSimpleTypeHolder(new SimpleTypeHolder(simpleTypes, true));
                this.prefix2Namespace = new HashMap<String, String>();
                for(Namespace ns : namespaces){
                        this.prefix2Namespace.put(ns.getPrefix(), ns.getName());
                }
                this.defaultNS = defaultNS;
                this.explicitSupertypes = explicitSupertypes;
        }

        public boolean isSemanticPersistentEntity(Class<?> clazz){
                return clazz.isAnnotationPresent(SemanticEntity.class);
        }

        @Override
        protected <T> SemanticPersistentEntity<T> createPersistentEntity(
                TypeInformation<T> typeInformation) {
                final Class<T> type = typeInformation.getType();
                if (type.isAnnotationPresent(SemanticEntity.class)) {
                        SemanticPersistentEntityImpl<T> persistentEntity = new SemanticPersistentEntityImpl<T>(typeInformation, this);
                        if(this.explicitSupertypes){
                                List<SemanticPersistentEntity<?>> superTypes = new LinkedList<SemanticPersistentEntity<?>>();
                                Class<?> supertype = type.getSuperclass();
                                while(supertype != null){
                                        SemanticPersistentEntity<?> superPersistentEntity = this.getPersistentEntity(supertype);
                                        if(superPersistentEntity != null){
                                                superTypes.add(superPersistentEntity);
                                        }
                                        supertype = supertype.getSuperclass();
                                }
                                persistentEntity.setSupertypes(superTypes);
                        }
                        return persistentEntity;
                }
                throw new IllegalArgumentException("Type " + type + " is not a @SemanticEntity!"); //TODO new Exception type to be used
        }

        @Override
        protected SemanticPersistentProperty createPersistentProperty(Field field,
                                                                      PropertyDescriptor descriptor,
                                                                      SemanticPersistentEntity<?> owner,
                                                                      SimpleTypeHolder simpleTypeHolder) {
                return new SemanticPersistentPropertyImpl(field, descriptor, owner, simpleTypeHolder, this);
        }

        /**
         * Resolves a {@link String} to an {@link URI}. Check for absolute URI, otherwise try to use defined namespace prefix or use default namespace.
         * @param predicate
         * @return
         */
        public URI resolveURI(String predicate){
                if(ValueUtils.isAbsoluteURI(predicate)){
                        return new URIImpl(predicate);
                }
                else{
                        String[] parts = predicate.split(":");
                        if(parts.length != 2){
                                if(parts.length == 1){
                                        return resolveURIDefaultNS(predicate);
                                }
                                throw new IllegalArgumentException("Not a valid relative URI '"+predicate+"'!");
                        }
                        String ns = prefix2Namespace.get(parts[0]);
                        if(ns == null){
                                throw new IllegalArgumentException("Unknown namespace for prefix '"+parts[0]+"'!");
                        }
                        return new URIImpl(ns+parts[1]);
                }
        }

        /**
         * Resolves a {@link String} to an {@link URI} using the default namespace.
         * @param lName
         * @return
         */
        public URI resolveURIDefaultNS(String lName){
                return new URIImpl(defaultNS.getName()+lName);
        }

        @Override public String toString() {
                // useful for debugging...
                return String.format("[SemanticMappingContext; default=%s; other=%s]", defaultNS, prefix2Namespace);
        }
}
