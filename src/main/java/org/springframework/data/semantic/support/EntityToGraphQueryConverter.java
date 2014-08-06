package org.springframework.data.semantic.support;

import java.util.Iterator;
import java.util.LinkedList;

import org.openrdf.model.URI;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.util.StringUtils;

/**
 * 
 * @author konstantin.pentchev
 *
 */
public class EntityToGraphQueryConverter {
	
	private static String variableChars = "abcdefghijklmnopqrstuvwxyz";
	
	
	/**
	 * Create a graph query retrieving a specific property of the entity identified by this uri
	 * @param uri 
	 * @param entity
	 * @param property
	 * @return
	 */
	public static String getGraphQueryForResourceProperty(URI uri, SemanticPersistentEntity<?> entity, SemanticPersistentProperty property){
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
	public static String getGraphQueryForResource(URI uri, SemanticPersistentEntity<?> entity){
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
	 * Get bindings for the retrievable properties of the entity
	 * @param uri - the uri of the entity
	 * @param entity - the container holding the information about the entity's structure
	 * @return
	 */
	protected static String getPropertyBindings(URI uri, SemanticPersistentEntity<?> entity){
		StringBuilder sb = new StringBuilder();
		entity.doWithProperties(new PropertiesToBindingsHandler(sb, uri));

		entity.doWithAssociations(new PropertiesToBindingsHandler(sb, uri));
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
		appendPattern(sb, "<"+uri.stringValue()+">", "<" + property.getAliasPredicate() + ">", "?"+property.getName());
		return sb.toString();
	}
	
	/**
	 * Create a pattern matching a specific property of an entity
	 * @param uri
	 * @param entity
	 * @param property
	 * @return
	 */
	protected static String getPropertyPattern(URI uri, SemanticPersistentEntity<?> entity, SemanticPersistentProperty property){
		StringBuilder sb = new StringBuilder();
		new PropertiesToPatternsHandler(sb, uri).doWithPersistentProperty(property);
		return sb.toString();
	}
	
	protected static String getPropertyPatterns(URI uri, SemanticPersistentEntity<?> entity){
		StringBuilder sb = new StringBuilder();
		/*SemanticPersistentProperty contextP = entity.getContextProperty();
		if(contextP != null){
			sb.append("GRAPH ");
			//sb.append(contextP.); TODO
			sb.append("{ ");
		}*/
		appendPattern(sb, "<"+uri.stringValue()+">", "<"+ValueUtils.RDF_TYPE_PREDICATE+">", "<"+entity.getRDFType()+">");
		entity.doWithProperties(new PropertiesToPatternsHandler(sb, uri));

		entity.doWithAssociations(new PropertiesToPatternsHandler(sb, uri));
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
	
	protected static  String getVar(int input){
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
	
	private static class PropertiesToPatternsHandler 
	implements PropertyHandler<SemanticPersistentProperty>, AssociationHandler<SemanticPersistentProperty>{

		private StringBuilder sb;
		private URI id;
		private int processedProps = 0;

		PropertiesToPatternsHandler(StringBuilder sb, URI id){
			this.sb = sb;
			this.id = id;
		}

		@Override
		public void doWithPersistentProperty(
				SemanticPersistentProperty persistentProperty) {
			handlePersistentProperty(persistentProperty);
		}

		@Override
		public void doWithAssociation(
				Association<SemanticPersistentProperty> association) {
			handlePersistentProperty(association.getInverse());

		}
		
		public void handlePersistentProperty(SemanticPersistentProperty persistentProperty) {
			if(isRetrivableProperty(persistentProperty)){
				Iterator<URI> predicates = persistentProperty.getPredicate().iterator();
				String var = "";
				int i = 0;
				String subj = "";
				String obj = "";
				while(predicates.hasNext()){
					String pred = "<"+predicates.next().stringValue()+">";
					if(var.isEmpty()){
						var = getVar(processedProps++);
						subj = "<"+id.stringValue()+">";
					}
					else{
						subj = "?"+String.valueOf(var)+String.valueOf(i);
					}
					if(!predicates.hasNext()){
						obj = "?"+persistentProperty.getName();
					}
					else{
						obj = "?"+String.valueOf(var)+String.valueOf(i);
					}
					appendPattern(sb, subj, pred, obj);
					i++;
				}
			}
		}		
	}
		
	private static class PropertiesToBindingsHandler implements  PropertyHandler<SemanticPersistentProperty>,  AssociationHandler<SemanticPersistentProperty> {

		private StringBuilder sb;
		private URI id;
		
		PropertiesToBindingsHandler(StringBuilder sb, URI id){
			this.sb = sb;
			this.id = id;
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
				appendPattern(sb, "<"+id.stringValue()+">", "<" + persistentProperty.getAliasPredicate() + ">", "?"+persistentProperty.getName());
			}
		}
		
		private void handleAssociation(SemanticPersistentProperty persistentProperty) {
			if(persistentProperty.getMappingPolicy().eagerLoad()){
				//TODO
			}
			else{
				appendPattern(sb, "<"+id.stringValue()+">", "<" + persistentProperty.getAliasPredicate() + ">", "?"+persistentProperty.getName());
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
