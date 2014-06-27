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
import org.springframework.data.semantic.support.SemanticTemplateStatementsCollector;

public class PropertyFieldAccessorFactory implements FieldAccessorFactory {
	
	private SemanticTemplateStatementsCollector template;

	public PropertyFieldAccessorFactory(SemanticTemplateStatementsCollector template) {
		this.template = template;
	}
	@Override
	public boolean accept(SemanticPersistentProperty property) {
		//accept if it is really a property, not an association
		/*TODO this is the framework's implementation ({@link AbstractPersistentProperty} ).
		It checks if the property is annoteted with a @Reference annotation.
		Override if necessary.*/
		return !property.isAssociation();
	}

	@Override
	public FieldAccessor forField(SemanticPersistentProperty property) {
		return new PropertyFieldAccessor(property, template);
	}
	
	public static class PropertyFieldAccessor implements FieldAccessor {
		private SemanticPersistentProperty property;
		private List<URI> predicates;
		private Class<?> fieldType;
		private SemanticTemplateStatementsCollector template;
		
		public PropertyFieldAccessor(SemanticPersistentProperty property, SemanticTemplateStatementsCollector template){
			this.property = property;
			this.predicates = property.getPredicate();
			this.fieldType = property.getType();
			this.template = template;
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
		public String getValue(Object entity, MappingPolicy mappingPolicy) {
			//TODO multiple values
			StatementsIterator stIterator = template.getStatementsForResourceProperty(entity, property);
			Iterator<Statement> statements = stIterator.iterator();
			if(statements.hasNext()) {
				return statements.next().getObject().stringValue();
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
