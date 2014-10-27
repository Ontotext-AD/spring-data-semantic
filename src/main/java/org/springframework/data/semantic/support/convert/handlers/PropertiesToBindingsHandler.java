package org.springframework.data.semantic.support.convert.handlers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.springframework.data.mapping.Association;
import org.springframework.data.semantic.convert.ObjectToLiteralConverter;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.Cascade;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class PropertiesToBindingsHandler extends AbstractPropertiesToQueryHandler {

	private StringBuilder sb;
	private String binding;
	private Map<String, Object> propertyToValue;
	private ObjectToLiteralConverter objectToLiteralConverter;
	private int depth;
	private final MappingPolicy globalMappingPolicy;
	
	public PropertiesToBindingsHandler(StringBuilder sb, String binding, Map<String, Object> propertyToValue, SemanticMappingContext mappingContext, MappingPolicy globalMappingPolicy){
		this(sb, binding, propertyToValue, mappingContext, 0, globalMappingPolicy);
	}
	
	public PropertiesToBindingsHandler(StringBuilder sb, String binding, Map<String, Object> propertyToValue, SemanticMappingContext mappingContext, int depth, MappingPolicy globalMappingPolicy){
		super(mappingContext);
		this.sb = sb;
		this.binding = binding;
		this.propertyToValue = propertyToValue;
		this.objectToLiteralConverter = ObjectToLiteralConverter.getInstance();
		this.depth = depth;
		this.globalMappingPolicy = globalMappingPolicy;
	}
	
	@Override
	public void doWithPersistentProperty(
			SemanticPersistentProperty persistentProperty) {
		handlePersistentProperty(persistentProperty);			
	}
	
	@Override
	public void doWithAssociation(
			Association<SemanticPersistentProperty> association) {
		if(depth < 50){
			handleAssociation(association.getInverse());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void handlePersistentProperty(SemanticPersistentProperty persistentProperty) {
		if(isRetrivableProperty(persistentProperty)){
			Object objectValue = propertyToValue.get(persistentProperty.getName());
			if(objectValue != null){
				if(objectValue instanceof Collection<?> || objectValue.getClass().isArray()){
					if(objectValue.getClass().isArray()){
						objectValue = Arrays.asList((Object[]) objectValue);
					}
					for(Object o : (Collection<Object>) objectValue){
						Value val = this.objectToLiteralConverter.convert(o);
						String obj = val instanceof URI ? "<"+val+">" : val.toString();
						appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", obj);
					}
				}
				else{
					Value val = this.objectToLiteralConverter.convert(objectValue);
					String obj = val instanceof URI ? "<"+val+">" : val.toString();
					appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", obj);
				}
			}
			else{
				appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", getObjectBinding(binding, persistentProperty));
			}
			
		}
	}
	
	private void handleAssociation(SemanticPersistentProperty persistentProperty) {
		String associationBinding = getObjectBinding(binding, persistentProperty);
		Object objectValue = propertyToValue.get(persistentProperty.getName());
		if(objectValue == null){
			appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", associationBinding);
			if(persistentProperty.getMappingPolicy().combineWith(globalMappingPolicy).shouldCascade(Cascade.GET)){
				SemanticPersistentEntity<?> associatedPersistentEntity = mappingContext.getPersistentEntity(persistentProperty.getActualType());
				appendPattern(sb, associationBinding, "a", "<"+associatedPersistentEntity.getRDFType()+">");
				PropertiesToBindingsHandler associationHandler = new PropertiesToBindingsHandler(this.sb, associationBinding, new HashMap<String, Object>(), this.mappingContext, ++this.depth, this.globalMappingPolicy);
				associatedPersistentEntity.doWithProperties(associationHandler);
				associatedPersistentEntity.doWithAssociations(associationHandler);
			}
		}
	}
}
