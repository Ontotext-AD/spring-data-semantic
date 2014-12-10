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
package org.springframework.data.semantic.support.convert;

import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.semantic.convert.state.EntityState;
import org.springframework.data.semantic.convert.state.EntityStateFactory;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

/**
 * Class that performs transitions between entities and state (
 * {@link StatementsIterator}).
 * 
 * @author konstantin.pentchev
 * 
 */
public class SemanticSourceStateTransmitter {

	private EntityStateFactory<RDFState> entityStateFactory;

	public SemanticSourceStateTransmitter(
			EntityStateFactory<RDFState> entityStateFactory) {
		this.entityStateFactory = entityStateFactory;
	}

	/**
	 * 
	 * @param wrapper
	 * @param source
	 * @param persistentEntity
	 * @param mappingPolicy
	 */
	public <R> void copyPropertiesFrom(
			final BeanWrapper<R> wrapper,
			RDFState source,
			SemanticPersistentEntity<R> persistentEntity,
			final MappingPolicy mappingPolicy) {
		final R entity = wrapper.getBean();
		final EntityState<R, RDFState> entityState = this.entityStateFactory.getEntityState(entity, false);
		entityState.setPersistentState(source);
		persistentEntity.doWithProperties(new PropertyHandler<SemanticPersistentProperty>() {					
			@Override
			public void doWithPersistentProperty(SemanticPersistentProperty property) {
				//Id property is populated on entity instantiation; nothing to do with it here
				if(property.isIdProperty() || property.isContext()) {
					return;
				}

				copyEntityStatePropertyValue(property, entityState,	wrapper, mappingPolicy.combineWith(property.getMappingPolicy()));
			}
		});
	}

	private <R> Object copyEntityStatePropertyValue(SemanticPersistentProperty property, EntityState<R, RDFState> state, 	BeanWrapper<R> wrapper,
			final MappingPolicy mappingPolicy) {
		final Object value = state.getValue(property, mappingPolicy);
		setProperty(wrapper, property, value);
		return value;
	}
	
	public EntityState<Object, RDFState> copyPropertiesTo(BeanWrapper<Object> wrapper, RDFState model){
		final Object entity = wrapper.getBean();
		final EntityState<Object, RDFState> state = this.entityStateFactory.getEntityState(entity, false);
		state.setPersistentState(model);
		return state;
	}

	public <R> void setProperty(BeanWrapper<R> wrapper, SemanticPersistentProperty property, Object value) {
		try {
			wrapper.setProperty(property, value);
		} catch (Exception e) {
			throw new MappingException("Setting property " + property.getName()
					+ " to " + value + " on " + wrapper.getBean(), e);
		}
	}

}
