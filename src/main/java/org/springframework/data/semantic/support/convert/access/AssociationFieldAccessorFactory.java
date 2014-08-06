package org.springframework.data.semantic.support.convert.access;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.springframework.data.semantic.convert.access.FieldAccessor;
import org.springframework.data.semantic.convert.access.FieldAccessorFactory;
import org.springframework.data.semantic.core.SemanticOperationsCRUD;
import org.springframework.data.semantic.mapping.MappingPolicy;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;

/**
 * Handles the creation of {@link AssociationFieldAccessor}
 * @author petar.kostov
 *
 */
public class AssociationFieldAccessorFactory implements FieldAccessorFactory {
	
	private SemanticTemplateStatementsCollector statementsCollector;
	private SemanticOperationsCRUD operations;
	
	public AssociationFieldAccessorFactory(SemanticTemplateStatementsCollector statementsCollector, SemanticOperationsCRUD operations) {
		this.statementsCollector = statementsCollector;
		this.operations = operations;
	}
	
	@Override
	public boolean accept(SemanticPersistentProperty property) {
		return property.isAssociation();
	}

	@Override
	public FieldAccessor forField(SemanticPersistentProperty property) {
		return new AssociationFieldAccessor(statementsCollector, operations, property);
	}
	
	/**
	 * Field accessor for associations
	 * getValue populates the association object from the database
	 * @author petar.kostov
	 *
	 */
	public static class AssociationFieldAccessor implements FieldAccessor {
		
		private SemanticPersistentProperty property;
		//private List<URI> predicates;
		private Class<?> fieldType;
		private SemanticTemplateStatementsCollector statementsCollector;
		private SemanticOperationsCRUD operations;
		
		public AssociationFieldAccessor(SemanticTemplateStatementsCollector statementsCollector, 
				SemanticOperationsCRUD operations, SemanticPersistentProperty property) {
			this.statementsCollector = statementsCollector;
			this.operations = operations;
			this.property = property;
			//this.predicates = property.getPredicate();
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
			Model stIterator = statementsCollector.getStatementsForResourceProperty(entity, property);
			if(this.property.isCollectionLike()){
				Set<Resource> subjects = stIterator.subjects();
				List<Object> result = new ArrayList<Object>(subjects.size());
				for(Resource subject : subjects){
					Model iterator = statementsCollector.getStatementsForResourceClass((URI) subject, fieldType);
					result.add(operations.createEntity(iterator, fieldType));
				}
				if(this.property.isArray()){
					return result.toArray();
				}
				else{
					return result;
				}
			}
			else{
				if(!stIterator.isEmpty()) {
					
					URI resource = (URI) stIterator.objects().iterator().next();
					Model iterator = statementsCollector.getStatementsForResourceClass(resource, fieldType);
					
					return operations.createEntity(iterator, fieldType);
				}
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
