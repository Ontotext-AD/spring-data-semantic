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
import org.springframework.data.semantic.support.Direction;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.semantic.support.util.ValueUtils;

public class PropertiesToPatternsHandler extends AbstractPropertiesToQueryHandler{

	private StringBuilder sb;
	private String binding;
	private Map<String, Object> propertyToValue;
	private ObjectToLiteralConverter objectToLiteralConverter;

	public PropertiesToPatternsHandler(StringBuilder sb, String binding, Map<String, Object> propertyToValue, SemanticMappingContext mappingContext){
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
		SemanticPersistentProperty persistentProperty = association.getInverse();
		//TODO handle existing value in propertyToValue
		handlePersistentProperty(persistentProperty);
		if(persistentProperty.getMappingPolicy().eagerLoad()){
			SemanticPersistentEntity<?> associatedPersistentEntity = mappingContext.getPersistentEntity(persistentProperty.getActualType());
			String associationBinding = persistentProperty.getBindingName();
			appendPattern(sb, associationBinding, "<"+ValueUtils.RDF_TYPE_PREDICATE+">", "<"+associatedPersistentEntity.getRDFType()+">");
			PropertiesToPatternsHandler associationHandler = new PropertiesToPatternsHandler(this.sb, associationBinding, new HashMap<String, Object>(), this.mappingContext);
			associatedPersistentEntity.doWithProperties(associationHandler);
			associatedPersistentEntity.doWithAssociations(associationHandler);
		}

	}
	
	public void handlePersistentProperty(SemanticPersistentProperty persistentProperty) {
		if(isRetrivableProperty(persistentProperty)){
			URI predicate = persistentProperty.getPredicate();
			String subj = "";
			String obj = "";
			String pred = "<"+predicate+">";
			subj = binding;
			Object objectValue = this.propertyToValue.get(persistentProperty.getName());
			Boolean optional = persistentProperty.isOptional() && (objectValue == null);
			if(objectValue != null){
				if(objectValue instanceof Collection<?>){
					//TODO
				}
				else{
					Value val = this.objectToLiteralConverter.convert(objectValue);
					obj = val instanceof URI ? "<"+val.toString()+">" : val.toString();
				}
			}
			else{
				obj = persistentProperty.getBindingName();
			}
			if(optional){
				sb.append("OPTIONAL { ");
			}
			if(persistentProperty.isAssociation()){
				if(Direction.INCOMING.equals(persistentProperty.getDirection())){
					SemanticPersistentProperty associatedProperty = persistentProperty.getInverseProperty();
					if(associatedProperty != null){
						pred = "<"+associatedProperty.getPredicate()+">";
						appendPattern(sb, obj, pred, subj);
					}
					else{
						appendPattern(sb, subj, pred, obj);
					}
				}
				else{
					appendPattern(sb, subj, pred, obj);
				}
			}
			else{
				appendPattern(sb, subj, pred, obj);
			}
			if(optional){
				sb.append("} ");
			}
		}
	}
}
