package org.springframework.data.semantic.support.convert.access.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.semantic.convert.access.listener.FieldAccessListener;
import org.springframework.data.semantic.convert.access.listener.FieldAccessListenerProvider;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.util.TypeInformation;

public class DelegatingFieldAccessListenerProvider implements FieldAccessListenerProvider{
	
	private final DelegatingFieldAccessListenerFactory delegatingListenerFactory;
	private final Map<TypeInformation<?>, Map<SemanticPersistentProperty, List<FieldAccessListener>>> fieldAccessListenerCache = new HashMap<TypeInformation<?>, Map<SemanticPersistentProperty, List<FieldAccessListener>>>();
	
	
	public DelegatingFieldAccessListenerProvider(DelegatingFieldAccessListenerFactory delegatingListenerFactory){
		this.delegatingListenerFactory = delegatingListenerFactory;
	}

	@Override
	public Map<SemanticPersistentProperty, List<FieldAccessListener>> provideFieldAccessListeners(SemanticPersistentEntity<?> entity) {
		final TypeInformation<?> typeInformation = entity.getTypeInformation();
		if(fieldAccessListenerCache.containsKey(typeInformation)){
			return fieldAccessListenerCache.get(typeInformation);
		}
		else{
			final Map<SemanticPersistentProperty, List<FieldAccessListener>> fieldAccessors =  new HashMap<SemanticPersistentProperty, List<FieldAccessListener>>();
			entity.doWithProperties(new PropertyHandler<SemanticPersistentProperty>() {
	            @Override
	            public void doWithPersistentProperty(SemanticPersistentProperty property) {
	            	 final List<FieldAccessListener> accessListener = delegatingListenerFactory.forField(property);
	                 if(accessListener != null){
	                 	fieldAccessors.put(property, accessListener);
	                 }
	            }
			});
			
			entity.doWithAssociations(new AssociationHandler<SemanticPersistentProperty>() {
	            @Override
	            public void doWithAssociation(Association<SemanticPersistentProperty> association) {
	                final SemanticPersistentProperty property = association.getInverse();
	                final List<FieldAccessListener> accessListener = delegatingListenerFactory.forField(property);
	                if(accessListener != null){
	                	fieldAccessors.put(property, accessListener);
	                }
	            }
	        });
			fieldAccessListenerCache.put(typeInformation, fieldAccessors);
			return fieldAccessors;
		}
	}

}
