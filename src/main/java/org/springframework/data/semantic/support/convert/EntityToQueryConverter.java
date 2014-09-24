package org.springframework.data.semantic.support.convert;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.openrdf.model.URI;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.convert.handlers.AbstractPropertiesToQueryHandler;
import org.springframework.data.semantic.support.convert.handlers.PropertiesToBindingsHandler;
import org.springframework.data.semantic.support.convert.handlers.PropertiesToPatternsHandler;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
import org.springframework.data.semantic.support.util.ValueUtils;
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
		return getGraphQueryForResource(uri, entity, new HashMap<String, Object>());
	}
	
	/**
	 * Create a graph query retrieving the molecule of an entity. 
	 * Only 'retrievable' {@link #isRetrivableProperty(SemanticPersistentProperty)} properties will be fetched
	 * @param uri - the uri of the entity
	 * @param entity - the container which holds the information about that entity
	 * @param propertiesToValues - the properties with their required values
	 * @return
	 */
	public String getGraphQueryForResource(URI uri, SemanticPersistentEntity<?> entity, Map<String, Object> propertyToValue){
		StringBuilder sb = new StringBuilder();
		
		sb.append("CONSTRUCT { ");
		sb.append(getPropertyBindings(uri, entity, propertyToValue));
		sb.append(" }\n");
		sb.append("WHERE { ");
		sb.append(getPropertyPatterns(uri, entity, propertyToValue));
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
		return getGraphQueryForEntityClass(entity, new HashMap<String, Object>());
	}
	
	public String getGraphQueryForEntityClass(SemanticPersistentEntity<?> entity, Map<String, Object> propertyToValue){
		StringBuilder sb = new StringBuilder();
		
		sb.append("CONSTRUCT { ");
		sb.append(getPropertyBindings(null, entity, propertyToValue));
		sb.append(" }\n");
		sb.append("WHERE { ");
		sb.append(getPropertyPatterns(null, entity, propertyToValue));
		sb.append(" }");
		
		return sb.toString();
	}
	
	/**
	 * Get bindings for the retrievable properties of the entity
	 * @param uri - the uri of the entity
	 * @param entity - the container holding the information about the entity's structure
	 * @return
	 */
	protected String getPropertyBindings(URI uri, SemanticPersistentEntity<?> entity, Map<String, Object> propertyToValue){
		StringBuilder sb = new StringBuilder();
		String subjectBinding;
		if(uri != null){
			subjectBinding = "<"+uri+">";
		}
		else{
			subjectBinding = "?"+entity.getRDFType().getLocalName();
			
		}
		AbstractPropertiesToQueryHandler.appendPattern(sb, subjectBinding, "a", "<"+entity.getRDFType()+">");
		PropertiesToBindingsHandler handler = new PropertiesToBindingsHandler(sb, subjectBinding, propertyToValue, this.mappingContext);
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
		AbstractPropertiesToQueryHandler.appendPattern(sb, "<"+uri+">", "<" + property.getAliasPredicate() + ">", "?"+property.getName());
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
		new PropertiesToPatternsHandler(sb, "<"+uri+">", new HashMap<String, Object>(), this.mappingContext, false).doWithPersistentProperty(property);
		return sb.toString();
	}
	
	protected String getPropertyPatterns(URI uri, SemanticPersistentEntity<?> entity, Map<String, Object> propertyToValue){
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
		AbstractPropertiesToQueryHandler.appendPattern(sb, binding, "<"+ValueUtils.RDF_TYPE_PREDICATE+">", "<"+entity.getRDFType()+">");
		PropertiesToPatternsHandler handler = new PropertiesToPatternsHandler(sb, binding, propertyToValue, this.mappingContext, false);
		entity.doWithProperties(handler);
		entity.doWithAssociations(handler);
		return sb.toString();
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
	
	
	
	

}
