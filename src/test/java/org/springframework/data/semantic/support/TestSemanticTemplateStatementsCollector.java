package org.springframework.data.semantic.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.springframework.data.semantic.support.mapping.SemanticMappingContext;

public class TestSemanticTemplateStatementsCollector {
	
	private SemanticTemplateStatementsCollector collector; 
	
	private SemanticMappingContext mappingContext = new SemanticMappingContext((List<? extends Namespace>) new LinkedList<Namespace>(), new NamespaceImpl("", "urn:default:namespace:"));
	
	private Statement statement = new StatementImpl(new URIImpl("urn:test:d"), new URIImpl("urn:test:has"), new URIImpl("urn:test:j"));
	
	@Test
	public void testAssembleStatements() throws RepositoryException{
		collector = new SemanticTemplateStatementsCollector(null, mappingContext, null);
		Model statements = new TreeModel();
		statements.add(new StatementImpl(new URIImpl("urn:test:a"), RDF.TYPE, new URIImpl("urn:test:type")));
		statements.add(new StatementImpl(new URIImpl("urn:test:a"), new URIImpl("urn:test:has"), new URIImpl("urn:test:c")));
		statements.add(new StatementImpl(new URIImpl("urn:test:a"), new URIImpl("urn:test:has"), new URIImpl("urn:test:e")));
		statements.add(new StatementImpl(new URIImpl("urn:test:c"), new URIImpl("urn:test:has"), new URIImpl("urn:test:b")));
		statements.add(new StatementImpl(new URIImpl("urn:test:b"), new URIImpl("urn:test:has"), new URIImpl("urn:test:d")));
		statements.add(new StatementImpl(new URIImpl("urn:test:k"), RDF.TYPE, new URIImpl("urn:test:type")));
		statements.add(new StatementImpl(new URIImpl("urn:test:k"), new URIImpl("urn:test:has"), new URIImpl("urn:test:d")));
		statements.add(statement);
		
		Collection<Model> assembled = collector.assembleModels(new URIImpl("urn:test:type"), statements);
		assertEquals(2, assembled.size());
		Iterator<Model> it = assembled.iterator();
		assertTrue(it.next().contains(statement) && it.next().contains(statement));
		
	}
	
	

}
