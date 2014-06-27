package org.springframework.data.semantic.core;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.springframework.data.repository.query.QueryCreationException;

/**
 * SemanticDatabase provides an interface over operations executable by {@link Repository} and {@link RepositoryConnection}.
 * 
 * @author konstantin.pentchev
 *
 */
public interface SemanticDatabase {
	
	//public Query prepareQuery(String source, QueryLanguage ql);
	
	/**
	 * Return a default {@link Namespace} to be used for creating {@link URI}s from class and property names when not explicitly provided.
	 * @return
	 * @throws RepositoryException
	 */
	public Namespace getDefaultNamespace() throws RepositoryException;
	
	/**
	 * Return a {@link List} of {@link Namespace}s defined for the semantic database (repository).
	 * @return
	 * @throws RepositoryException
	 */
	public List<Namespace> getNamespaces() throws RepositoryException;
	
	/**
	 * Add a {@link Namespace} to the semantic database from a prefix and string definition.
	 * @param prefix - the short alias by which to refer to the namespace.
	 * @param namespace - the full namespace URI.
	 * @throws RepositoryException
	 */
	public void addNamespace(String prefix, String namespace) throws RepositoryException;
	
	/**
	 * Retrieve the {@link List} of context {@link Resource}s defined in the semantic database.
	 * @return
	 * @throws RepositoryException
	 */
	public List<Resource> getContexts() throws RepositoryException;
	
	public List<BindingSet> getQueryResults(String source) throws RepositoryException, QueryCreationException, QueryEvaluationException, QueryInterruptedException, MalformedQueryException;
	
	public List<BindingSet> getQueryResults(String source, Integer offset, Integer limit) throws RepositoryException, QueryCreationException, QueryEvaluationException, QueryInterruptedException, MalformedQueryException;
	
	public Long getQueryResultsCount(String source) throws RepositoryException, QueryCreationException, QueryEvaluationException, QueryInterruptedException, MalformedQueryException;
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the given subject.
	 * @param subject
	 * @return
	 * @throws RepositoryException
	 */
	public List<Statement> getStatementsForSubject(Resource subject) throws RepositoryException;
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the given predicate.
	 * @param predicate
	 * @return
	 * @throws RepositoryException
	 */
	public List<Statement> getStatementsForPredicate(URI predicate) throws RepositoryException;
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the given object.
	 * @param object
	 * @return
	 * @throws RepositoryException
	 */
	public List<Statement> getStatementsForObject(Value object) throws RepositoryException;
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the given context.
	 * @param context
	 * @return
	 * @throws RepositoryException
	 */
	public List<Statement> getStatementsForContext(Resource context) throws RepositoryException;
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the pattern defined by the given subject, predicate and object.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 * @throws RepositoryException
	 */
	public List<Statement> getStatementsForTriplePattern(Resource subject, URI predicate, Value object) throws RepositoryException;
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the pattern defined by the given subject, predicate, object and context.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param context
	 * @return
	 * @throws RepositoryException
	 */
	public List<Statement> getStatementsForQuadruplePattern(Resource subject, URI predicate, Value object, Resource context) throws RepositoryException;
	
	/**
	 * Add the given {@link Statement} to the semantic database.
	 * @param statement
	 * @throws RepositoryException
	 */
	public void addStatement(Statement statement) throws RepositoryException;
	
	/**
	 * Add the {@link Statement} defined by the given subject, predicate and object to the semantic database.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @throws RepositoryException
	 */
	public void addStatement(Resource subject, URI predicate, Value object) throws RepositoryException;
	
	/**
	 * Add the {@link Statement} defined by the given subject, predicate, object and context to the semantic database.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param context
	 * @throws RepositoryException
	 */
	public void addStatement(Resource subject, URI predicate, Value object, Resource context) throws RepositoryException;
	
	/**
	 * Add a {@link List} of {@link Statement}s to the semantic database.
	 * @param statements
	 * @throws RepositoryException
	 */
	public void addStatements(List<Statement> statements) throws RepositoryException;
	
	/**
	 * Reads {@link Statement}s from a RDF file (rdf/xml, n3 or turtle) and adds them to the semantic database.
	 * @param rdfSource
	 * @throws RepositoryException
	 * @throws RDFParseException
	 * @throws IOException
	 */
	public void addStatementsFromFile(File rdfSource) throws RepositoryException, RDFParseException, IOException;
	
	/**
	 * Delete the given {@link Statement} from the semantic database.
	 * @param statement
	 * @throws RepositoryException
	 */
	public void removeStatement(Statement statement) throws RepositoryException;
	
	/**
	 * Delete the {@link Statement} defined by the given subject, predicate and object.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @throws RepositoryException
	 */
	public void removeStatement(Resource subject, URI predicate, Value object) throws RepositoryException;
	
	/**
	 * Delete the {@link Statement} defined by the given subject, predicate, object and graph.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param context
	 * @throws RepositoryException
	 */
	public void removeStatement(Resource subject, URI predicate, Value object, Resource context) throws RepositoryException;
	
	/**
	 * Create a {@link GraphQuery} from the given source {@link String} and return the results from its execution.
	 * @param graphQuery
	 * @return
	 * @throws RepositoryException
	 * @throws QueryCreationException
	 * @throws QueryEvaluationException
	 * @throws QueryInterruptedException
	 * @throws MalformedQueryException 
	 */
	public GraphQueryResult getStatementsForGraphQuery(String graphQuery) throws RepositoryException, QueryCreationException, QueryEvaluationException, QueryInterruptedException, MalformedQueryException;
	
	/**
	 * Clear all connections and other resources in use.
	 */
	public void shutdown();

}
