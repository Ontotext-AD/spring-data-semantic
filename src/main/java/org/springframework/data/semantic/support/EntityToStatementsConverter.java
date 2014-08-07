package org.springframework.data.semantic.support;

import java.util.Arrays;
import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.AssociationHandler;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.semantic.core.RDFState;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;
/**
 * Class that converts domain objects to RDF.
 * 
 * @author konstantin.pentchev
 *
 */
public class EntityToStatementsConverter {
	
	private SemanticMappingContext mappingContext;
	
	public EntityToStatementsConverter(SemanticMappingContext mappingContext){
		this.mappingContext = mappingContext;
	}
	
	public RDFState convertEntityToStatements(URI resourceId, SemanticPersistentEntity<?> persistentEntity, Object entity){
		RDFState statements = new  RDFState();
		PropertiesToStatementsHandler handler = new PropertiesToStatementsHandler(resourceId, statements, entity, persistentEntity);
		persistentEntity.doWithProperties(handler);
		persistentEntity.doWithAssociations(handler);
		return statements;
	}
	
	private class PropertiesToStatementsHandler implements PropertyHandler<SemanticPersistentProperty>, AssociationHandler<SemanticPersistentProperty> {
		
		private RDFState statements;
		private URI resourceId;
		private Object entity;
		private SemanticPersistentEntity<?> persistentEntity;
		
		PropertiesToStatementsHandler(URI resourceId, RDFState statements, Object entity, SemanticPersistentEntity<?> persistentEntity){
			this.statements = statements;
			this.resourceId = resourceId;
			this.entity = entity;
			this.persistentEntity = persistentEntity;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void doWithPersistentProperty(SemanticPersistentProperty persistentProperty) {
			Object value = persistentProperty.getValue(entity, persistentEntity.getMappingPolicy());
			if(persistentProperty.shallBePersisted()){
				if(persistentProperty.isCollectionLike()){
					if(persistentProperty.isArray()){
						Object[] values = (Object[]) value;
						for(Object val : values){
							addToStatements(persistentProperty, val);
						}
					}
					else{
						Iterable<Object> values = (Iterable<Object>) value;
						for(Object val : values){
							addToStatements(persistentProperty, val);
						}
					}
					
				}
				else{
					addToStatements(persistentProperty, value);
				}
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void doWithAssociation(Association<SemanticPersistentProperty> association) {
			SemanticPersistentProperty persistentProperty = association.getInverse();
			Object value = persistentProperty.getValue(entity, persistentEntity.getMappingPolicy());
			if(persistentProperty.isCollectionLike()){
				SemanticPersistentEntity<Object> associatedEntity = (SemanticPersistentEntity<Object>) mappingContext.getPersistentEntity(persistentProperty.getComponentType());
				Collection<Object> associatedEntityInstances;
				if(persistentProperty.isArray()){
					associatedEntityInstances = Arrays.asList((Object[]) value);
				}
				else{
					associatedEntityInstances = (Collection<Object>) value;
				}
				for(Object associatedEntityInstance : associatedEntityInstances){
					URI associatedResourceId = associatedEntity.getResourceId(associatedEntityInstance);
					addToStatements(persistentProperty, associatedResourceId);
					if(persistentProperty.getMappingPolicy().eagerLoad()){
						//TODO
					}
				}
			}
			else{
				SemanticPersistentEntity<Object> associatedEntity = (SemanticPersistentEntity<Object>) mappingContext.getPersistentEntity(persistentProperty.getType());
				URI associatedResourceId = associatedEntity.getResourceId(value);
				addToStatements(persistentProperty, associatedResourceId);
				if(persistentProperty.getMappingPolicy().eagerLoad()){
					//TODO
				}
			}
		}
		
		private void addToStatements(SemanticPersistentProperty persistentProperty, Object value){
			if(persistentProperty.isContext()){
				return;
			}
			else if(persistentProperty.isIdProperty()){
				if(persistentEntity.hasContextProperty() && persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy()) != null){
					statements.addStatement(new ContextStatementImpl((URI) value, new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), persistentEntity.getRDFType(), (Resource) persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy())));
				}
				else{
					statements.addStatement(new StatementImpl((URI) value, new URIImpl(ValueUtils.RDF_TYPE_PREDICATE), persistentEntity.getRDFType()));
				}
			}
			else{
				if(value instanceof Value){
					statements.addStatement(new ContextStatementImpl(resourceId, persistentProperty.getPredicate().get(0), (Value) value, (Resource) persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy())));
				}
				else{
					//TODO handle language tags and data types; test if Resource
					if(persistentEntity.hasContextProperty() && persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy()) != null){
						statements.addStatement(new ContextStatementImpl(resourceId, persistentProperty.getPredicate().get(0), new LiteralImpl(value.toString()), (Resource) persistentEntity.getContextProperty().getValue(entity, persistentProperty.getMappingPolicy())));
					}
					else{
						statements.addStatement(new StatementImpl(resourceId, persistentProperty.getPredicate().get(0), new LiteralImpl(value.toString())));
					}
				}
			}
		}

		
	}

}
