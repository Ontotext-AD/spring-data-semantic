package org.springframework.data.semantic.support;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.openrdf.model.Namespace;
import org.openrdf.model.impl.NamespaceImpl;
import org.springframework.data.semantic.mapping.SemanticPersistentEntity;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;
import org.springframework.data.semantic.model.NamespaceEntity;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class TestNamespaceAnnotation {
	
	private SemanticMappingContext mappingContext = new SemanticMappingContext((List<? extends Namespace>) new LinkedList<Namespace>(), new NamespaceImpl("", "urn:default:namespace:"));
	
	@SuppressWarnings("unchecked")
	private SemanticPersistentEntity<NamespaceEntity> pe = (SemanticPersistentEntity<NamespaceEntity>) mappingContext.getPersistentEntity(NamespaceEntity.class);
	
	private String namespace = "urn:test:namespace:";
	
	@Test
	public void testGetPredicate(){
		SemanticPersistentProperty pp = pe.getPersistentProperty("name");
		assertEquals(namespace+"name" , pp.getPredicate().get(0).stringValue());
	}
	
	@Test
	public void testGetType(){
		assertEquals(namespace+"NamespaceEntity", pe.getRDFType().stringValue());
	}

}
