package org.springframework.data.semantic.support.convert.access;

import java.util.Iterator;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.springframework.data.semantic.convert.fieldaccess.FieldAccessor;
import org.springframework.data.semantic.convert.fieldaccess.FieldAccessorFactory;
import org.springframework.data.semantic.core.StatementsIterator;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.SemanticTemplateCRUD;
import org.springframework.data.semantic.support.SemanticTemplateObjectCreator;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

/**
 * Handles the creation of {@link AssociationFieldAccessor}
 * @author petar.kostov
 *
 */
public class AssociationFieldAccessorFactory implements FieldAccessorFactory {
	
	private SemanticTemplateStatementsCollector statementsCollector;
	private SemanticTemplateObjectCreator objectCreator;
	private SemanticMappingContext mappingContext;
	
	public AssociationFieldAccessorFactory(SemanticTemplateStatementsCollector statementsCollector, SemanticTemplateObjectCreator objectCreator) {
		this.statementsCollector = statementsCollector;
		this.objectCreator = objectCreator;
	}
	
	@Override
	public boolean accept(SemanticPersistentProperty property) {
		return property.isAssociation();
	}

	@Override
	public FieldAccessor forField(SemanticPersistentProperty property) {
		return new AssociationFieldAccessor(statementsCollector, objectCreator, property);
	}
	
	/**
	 * Field accessor for associations
	 * getValue populates the association object from the database
	 * @author petar.kostov
	 *
	 */
	public static class AssociationFieldAccessor implements FieldAccessor {
		
		private SemanticPersistentProperty property;
		private List<URI> predicates;
		private Class<?> fieldType;
		private SemanticTemplateStatementsCollector statementsCollector;
		private SemanticTemplateObjectCreator objectCreator;
		
		public AssociationFieldAccessor(SemanticTemplateStatementsCollector statementsCollector, 
				SemanticTemplateObjectCreator objectCreator, SemanticPersistentProperty property) {
			this.statementsCollector = statementsCollector;
			this.objectCreator = objectCreator;
			this.property = property;
			this.predicates = property.getPredicate();
			this.fieldType = property.getType();
		}
		
		@Override
		public Object getDefaultValue() {
			return null;
		}

		@Override
		public Object setValue(Object entity, Object newVal,
				MappingPolicy mappingPolicy) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getValue(Object entity, MappingPolicy mappingPolicy) {
			//TODO multiple values
			StatementsIterator stIterator = statementsCollector.getStatementsForResourceProperty(entity, property);
			Iterator<Statement> statements = stIterator.iterator();
			if(statements.hasNext()) {
				
				URI resource = (URI)statements.next().getObject();
				StatementsIterator iterator = statementsCollector.getStatementsForResourceClass(resource, fieldType);
				
				return objectCreator.createObjectFromStatements(iterator, fieldType, mappingPolicy);
			}
			return null;
		}

		@Override
		public boolean isWritable(Object entity) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

	

}
