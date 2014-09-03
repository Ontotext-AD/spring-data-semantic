package org.springframework.data.semantic.support.convert;

import java.util.Iterator;
import java.util.LinkedList;

import org.openrdf.model.URI;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.ValueUtils;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.util.StringUtils;

/**
 * 
 * @author konstantin.pentchev
 *
 */
public class EntityToQueryConverter {
	
	private static String variableChars = "abcdefghijklmnopqrstuvwxyz";
	
	private SemanticMappingContext mappingContext;
	
	public EntityToQueryConverter(SemanticMappingContext mappingContext){
		this.mappingContext = mappingContext;
	}
	
	
	/**
	 * Create a graph query retrieving a specific property of the entity identified by this uri
	 * @param uri 
	 * @param entity
	 * @param property
	 * @return
	 */
	public String getGraphQueryForResourceProperty(URI uri, SemanticPersistentEntity<?> entity, SemanticPersistentProperty property){
		StringBuilder sb = new StringBuilder();
		
		sb.append("CONSTRUCT { ");
		sb.append(getPropertyBinding(uri, property));
		sb.append(" }\n");
		sb.append("WHERE { ");
		sb.append(getPropertyPattern(uri, entity, property));
		sb.append(" }");
		
		return sb.toString();
	}
	
	/**
	 * Create a graph query retrieving the molecule of an entity. 
	 * Only 'retrievable' {@link #isRetrivableProperty(SemanticPersistentProperty)} properties will be fetched
	 * @param uri - the uri of the entity
	 * @param entity - the container which holds the information about that entity
	 * @return
	 */
	public String getGraphQueryForResource(URI uri, SemanticPersistentEntity<?> entity){
		StringBuilder sb = new StringBuilder();
		
		sb.append("CONSTRUCT { ");
		sb.append(getPropertyBindings(uri, entity));
		sb.append(" }\n");
		sb.append("WHERE { ");
		sb.append(getPropertyPatterns(uri, entity));
		sb.append(" }");
		
		return sb.toString();
	}
	
	/**
	 * Create a select count query for a given entity type.
	 * @param entity
	 * @return
	 */
	public String getGraphQueryForResourceCount(SemanticPersistentEntity<?> entity){
		return "SELECT (COUNT (DISTINCT ?uri) as ?count) WHERE { ?uri a <"+entity.getRDFType()+">}";
	}
	
	/**
	 * Create an ask query checking if an entity exists.
	 * @param resourceId
	 * @param entity
	 * @return
	 */
	public String getQueryForResourceExistence(URI resourceId, SemanticPersistentEntity<?> entity){
		return "ASK {<"+resourceId+"> a <"+entity.getRDFType()+">}";
	}
	
	public String getGraphQueryForEntityClass(SemanticPersistentEntity<?> entity){
		StringBuilder sb = new StringBuilder();
		
		sb.append("CONSTRUCT { ");
		sb.append(getPropertyBindings(null, entity));
		sb.append(" }\n");
		sb.append("WHERE { ");
		sb.append(getPropertyPatterns(null, entity));
		sb.append(" }");
		
		return sb.toString();
	}
	
	/**
	 * Get bindings for the retrievable properties of the entity
	 * @param uri - the uri of the entity
	 * @param entity - the container holding the information about the entity's structure
	 * @return
	 */
	protected String getPropertyBindings(URI uri, SemanticPersistentEntity<?> entity){
		StringBuilder sb = new StringBuilder();
		String binding;
		if(uri != null){
			binding = "<"+uri+">";
		}
		else{
			binding = "?"+entity.getRDFType().getLocalName();
			
		}
		appendPattern(sb, binding, "a", "<"+entity.getRDFType()+">");
		PropertiesToBindingsHandler handler = new PropertiesToBindingsHandler(sb, binding);
		entity.doWithProperties(handler);
		entity.doWithAssociations(handler);
		return sb.toString();
	}
	
	/**
	 * Create a binding for a specific property of an entity
	 * @param uri
	 * @param property
	 * @return
	 */
	protected static String getPropertyBinding(URI uri, SemanticPersistentProperty property){
		StringBuilder sb = new StringBuilder();
		appendPattern(sb, "<"+uri+">", "<" + property.getAliasPredicate() + ">", "?"+property.getName());
		return sb.toString();
	}
	
	/**
	 * Create a pattern matching a specific property of an entity
	 * @param uri
	 * @param entity
	 * @param property
	 * @return
	 */
	protected String getPropertyPattern(URI uri, SemanticPersistentEntity<?> entity, SemanticPersistentProperty property){
		StringBuilder sb = new StringBuilder();
		new PropertiesToPatternsHandler(sb, "<"+uri+">").doWithPersistentProperty(property);
		return sb.toString();
	}
	
	protected String getPropertyPatterns(URI uri, SemanticPersistentEntity<?> entity){
		StringBuilder sb = new StringBuilder();
		/*SemanticPersistentProperty contextP = entity.getContextProperty();
		if(contextP != null){
			sb.append("GRAPH ");
			//sb.append(contextP.); TODO
			sb.append("{ ");
		}*/
		String binding;
		if(uri != null){
			binding = "<"+uri.stringValue()+">";
		}
		else{
			binding = "?"+entity.getRDFType().getLocalName();
		}
		appendPattern(sb, binding, "<"+ValueUtils.RDF_TYPE_PREDICATE+">", "<"+entity.getRDFType()+">");
		PropertiesToPatternsHandler handler = new PropertiesToPatternsHandler(sb, binding);
		entity.doWithProperties(handler);
		entity.doWithAssociations(handler);
		return sb.toString();
	}
	
	private static void appendPattern(StringBuilder sb, String subj, String pred, String varName){
		sb.append(subj);
		sb.append(" ");
		sb.append(pred);
		sb.append(" ");
		sb.append(varName);
		sb.append(" . ");
	}
	
	protected String getVar(int input){
		int alphabetSize = variableChars.length();
		int result = input / alphabetSize;
		int remainder = input % alphabetSize;
		LinkedList<Character> var = new LinkedList<Character>();
		var.add(variableChars.charAt(remainder));
		while(result > 0){
			remainder = result % alphabetSize;
			var.addFirst(variableChars.charAt(remainder));
			result /= alphabetSize;
		}
		return StringUtils.collectionToDelimitedString(var, "");
	}
	
	private class PropertiesToPatternsHandler 
	implements PropertyHandler<SemanticPersistentProperty>, AssociationHandler<SemanticPersistentProperty>{

		private StringBuilder sb;
		private String binding;
		private int processedProps = 0;

		PropertiesToPatternsHandler(StringBuilder sb, String binding){
			this.sb = sb;
			this.binding = binding;
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
			handlePersistentProperty(persistentProperty);
			if(persistentProperty.getMappingPolicy().eagerLoad()){
				SemanticPersistentEntity<?> associatedPersistentEntity = mappingContext.getPersistentEntity(persistentProperty.getActualType());
				String associationBinding = persistentProperty.getBindingName();
				appendPattern(sb, associationBinding, "<"+ValueUtils.RDF_TYPE_PREDICATE+">", "<"+associatedPersistentEntity.getRDFType()+">");
				PropertiesToPatternsHandler associationHandler = new PropertiesToPatternsHandler(this.sb, associationBinding);
				associatedPersistentEntity.doWithProperties(associationHandler);
				associatedPersistentEntity.doWithAssociations(associationHandler);
			}

		}
		
		public void handlePersistentProperty(SemanticPersistentProperty persistentProperty) {
			if(isRetrivableProperty(persistentProperty)){
				Iterator<URI> predicates = persistentProperty.getPredicate().iterator();
				String var = "";
				int i = 0;
				String subj = "";
				String obj = "";
				if(persistentProperty.isOptional()){
					sb.append("OPTIONAL { ");
				}
				while(predicates.hasNext()){
					String pred = "<"+predicates.next().stringValue()+">";
					if(var.isEmpty()){
						subj = binding;
						var = getVar(processedProps++);
					}
					else{
						subj = "?"+String.valueOf(var)+String.valueOf(i);
					}
					if(!predicates.hasNext()){
						obj = persistentProperty.getBindingName();
					}
					else{
						obj = "?"+String.valueOf(var)+String.valueOf(i);
					}
					appendPattern(sb, subj, pred, obj);
					i++;
				}
				if(persistentProperty.isOptional()){
					sb.append("} ");
				}
			}
		}		
	}
		
	private class PropertiesToBindingsHandler implements  PropertyHandler<SemanticPersistentProperty>,  AssociationHandler<SemanticPersistentProperty> {

		private StringBuilder sb;
		private String binding;
		
		PropertiesToBindingsHandler(StringBuilder sb, String binding){
			this.sb = sb;
			this.binding = binding;
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
				appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", persistentProperty.getBindingName());
			}
		}
		
		private void handleAssociation(SemanticPersistentProperty persistentProperty) {
			String associationBinding = persistentProperty.getBindingName();
			appendPattern(sb, binding, "<" + persistentProperty.getAliasPredicate() + ">", associationBinding);
			if(persistentProperty.getMappingPolicy().eagerLoad()){
				SemanticPersistentEntity<?> associatedPersistentEntity = mappingContext.getPersistentEntity(persistentProperty.getActualType());
				appendPattern(sb, associationBinding, "a", "<"+associatedPersistentEntity.getRDFType()+">");
				PropertiesToBindingsHandler associationHandler = new PropertiesToBindingsHandler(this.sb, associationBinding);
				associatedPersistentEntity.doWithProperties(associationHandler);
				associatedPersistentEntity.doWithAssociations(associationHandler);
			}
		}
	}
	
	/**
	 * if a SemanticPersistentProperty should be included in the query which retrieves the object
	 * @param persistentProperty
	 * @return
	 */
	private static boolean isRetrivableProperty(SemanticPersistentProperty persistentProperty) {
		//TODO do not include properties which are to be lazy loaded or are attached (always retrieved from the repository)
		return 
				!persistentProperty.isIdProperty() && 
				!persistentProperty.isTransient() && 
				!persistentProperty.isContext() /*&&
				persistentProperty.getMappingPolicy().eagerLoad() && 
				persistentProperty.getMappingPolicy().useDirty()*/;				
	}
	
	

}
