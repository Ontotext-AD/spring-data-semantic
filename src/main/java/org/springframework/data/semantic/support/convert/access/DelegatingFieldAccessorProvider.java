package org.springframework.data.semantic.support.convert.access;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.semantic.convert.fieldaccess.FieldAccessor;
import org.springframework.data.semantic.convert.fieldaccess.FieldAccessorProvider;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.util.TypeInformation;

public class DelegatingFieldAccessorProvider implements FieldAccessorProvider {

	private final DelegatingFieldAccessorFactory delegatingFactory;
	
	private final Map<TypeInformation<?>, Map<SemanticPersistentProperty, FieldAccessor>> cache = new HashMap<TypeInformation<?>, Map<SemanticPersistentProperty,FieldAccessor>>();
	
	public DelegatingFieldAccessorProvider(DelegatingFieldAccessorFactory factory){
		this.delegatingFactory = factory; 
	}
	
	@Override
	public Map<SemanticPersistentProperty, FieldAccessor> provideFieldAccessors(
			SemanticPersistentEntity<?> entity) {
		
		final TypeInformation<?> typeInformation = entity.getTypeInformation();
		if(cache.containsKey(typeInformation)){
			return cache.get(typeInformation);
		}
		else{
			final Map<SemanticPersistentProperty, FieldAccessor> fieldAccessors =  new HashMap<SemanticPersistentProperty, FieldAccessor>();
			entity.doWithProperties(new PropertyHandler<SemanticPersistentProperty>() {
	            @Override
	            public void doWithPersistentProperty(SemanticPersistentProperty property) {
	            	 final FieldAccessor accessor = delegatingFactory.forField(property);
	                 if(accessor != null){
	                 	fieldAccessors.put(property, accessor);
	                 }
	            }
			});
			
			entity.doWithAssociations(new AssociationHandler<SemanticPersistentProperty>() {
	            @Override
	            public void doWithAssociation(Association<SemanticPersistentProperty> association) {
	                final SemanticPersistentProperty property = association.getInverse();
	                final FieldAccessor accessor = delegatingFactory.forField(property);
	                if(accessor != null){
	                	fieldAccessors.put(property, accessor);
	                }
	            }
	        });
			cache.put(typeInformation, fieldAccessors);
			return fieldAccessors;
		}
	}

}
