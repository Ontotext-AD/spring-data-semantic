package org.springframework.data.semantic.support.convert.handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.springframework.data.mapping.Association;
import org.springframework.data.semantic.convert.ObjectToLiteralConverter;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class PropertiesToBindingsHandler extends AbstractPropertiesToQueryHandler {

	private StringBuilder sb;
	private String binding;
	private Map<String, Object> propertyToValue;
	private ObjectToLiteralConverter objectToLiteralConverter;
	
	public PropertiesToBindingsHandler(StringBuilder sb, String binding, Map<String, Object> propertyToValue, SemanticMappingContext mappingContext){
		super(mappingContext);
		this.sb = sb;
		this.binding = binding;
		this.propertyToValue = propertyToValue;
		this.objectToLiteralConverter = ObjectToLiteralConverter.getInstance();
	}
	
	@Override
	public void doWithPersistentProperty(
			SemanticPersistentProperty persistentProperty) {
		handlePersistentProperty(persistentProperty);			
	}
	
	@Override
	public void doWithAssociation(
			Association<SemanticPersistentProperty> association) {
		handleAssociation(association.getInverse());
		
	}
	
	private void handlePersistentProperty(SemanticPersistentProperty persistentProperty) {
		if(isRetrivableProperty(persistentProperty)){
			Object objectValue = propertyToValue.get(persistentProperty.getName());
			if(objectValue != null){
				if(objectValue instanceof Collection<?>){
					//TODO
				}
				else{
					Value val = this.objectToLiteralConverter.convert(objectValue);
					if(val instanceof URI){
						appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", "<"+val.toString()+">");
					}
					else{
						appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", val.toString());
					}
				}
			}
			else{
				appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", persistentProperty.getBindingName());
			}
			
		}
	}
	
	private void handleAssociation(SemanticPersistentProperty persistentProperty) {
		String associationBinding = persistentProperty.getBindingName();
		//handle value in propertyToValue
		appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", associationBinding);
		if(persistentProperty.getMappingPolicy().eagerLoad()){
			SemanticPersistentEntity<?> associatedPersistentEntity = mappingContext.getPersistentEntity(persistentProperty.getActualType());
			appendPattern(sb, associationBinding, "a", "<"+associatedPersistentEntity.getRDFType()+">");
			PropertiesToBindingsHandler associationHandler = new PropertiesToBindingsHandler(this.sb, associationBinding, new HashMap<String, Object>(), this.mappingContext);
			associatedPersistentEntity.doWithProperties(associationHandler);
			associatedPersistentEntity.doWithAssociations(associationHandler);
		}
	}
}
