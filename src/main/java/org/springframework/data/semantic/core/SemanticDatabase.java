package org.springframework.data.semantic.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
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
	
	long count();
	
	/**
	 * Return a default {@link Namespace} to be used for creating {@link URI}s from class and property names when not explicitly provided.
	 * @return
	 * @throws RepositoryException
	 */
	Namespace getDefaultNamespace() throws RepositoryException;
	
	/**
	 * Return a {@link List} of {@link Namespace}s defined for the semantic database (repository).
	 * @return
	 * @throws RepositoryException
	 */
	List<Namespace> getNamespaces() throws RepositoryException;
	
	/**
	 * Add a {@link Namespace} to the semantic database from a prefix and string definition.
	 * @param prefix - the short alias by which to refer to the namespace.
	 * @param namespace - the full namespace URI.
	 * @throws RepositoryException
	 */
	void addNamespace(String prefix, String namespace) throws RepositoryException;
	
	/**
	 * Retrieve the {@link List} of context {@link Resource}s defined in the semantic database.
	 * @return
	 * @throws RepositoryException
	 */
	List<Resource> getContexts() throws RepositoryException;
	
	List<BindingSet> getQueryResults(String source) throws RepositoryException, QueryCreationException, QueryEvaluationException, QueryInterruptedException, MalformedQueryException;
	
	List<BindingSet> getQueryResults(String source, Integer offset, Integer limit) throws RepositoryException, QueryCreationException, QueryEvaluationException, QueryInterruptedException, MalformedQueryException;
	
	Long getQueryResultsCount(String source) throws RepositoryException, QueryCreationException, QueryEvaluationException, QueryInterruptedException, MalformedQueryException;
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the given subject.
	 * @param subject
	 * @return
	 * @throws RepositoryException
	 */
	List<Statement> getStatementsForSubject(Resource subject);
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the given predicate.
	 * @param predicate
	 * @return
	 * @throws RepositoryException
	 */
	List<Statement> getStatementsForPredicate(URI predicate);
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the given object.
	 * @param object
	 * @return
	 * @throws RepositoryException
	 */
	List<Statement> getStatementsForObject(Value object);
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the given context.
	 * @param context
	 * @return
	 * @throws RepositoryException
	 */
	List<Statement> getStatementsForContext(Resource context);
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the pattern defined by the given subject, predicate and object.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 * @throws RepositoryException
	 */
	List<Statement> getStatementsForTriplePattern(Resource subject, URI predicate, Value object);
	
	/**
	 * Retrieve the {@link List} of {@link Statement}s for the pattern defined by the given subject, predicate, object and context.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param context
	 * @return
	 * @throws RepositoryException
	 */
	List<Statement> getStatementsForQuadruplePattern(Resource subject, URI predicate, Value object, Resource context);
	
	/**
	 * Add the given {@link Statement} to the semantic database.
	 * @param statement
	 * @throws RepositoryException
	 */
	void addStatement(Statement statement);
	
	/**
	 * Add the {@link Statement} defined by the given subject, predicate and object to the semantic database.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @throws RepositoryException
	 */
	void addStatement(Resource subject, URI predicate, Value object);
	
	/**
	 * Add the {@link Statement} defined by the given subject, predicate, object and context to the semantic database.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param context
	 * @throws RepositoryException
	 */
	void addStatement(Resource subject, URI predicate, Value object, Resource context);
	
	/**
	 * Add a {@link Collection} of {@link Statement}s to the semantic database.
	 * @param statements
	 * @throws RepositoryException
	 */
	void addStatements(Collection<? extends Statement> statements);

	
	/**
	 * Reads {@link Statement}s from a RDF file (rdf/xml, n3 or turtle) and adds them to the semantic database.
	 * @param rdfSource
	 */
	void addStatementsFromFile(File rdfSource) throws RepositoryException, RDFParseException, IOException;
	
	
	
	/**
	 * Delete the given {@link Statement} from the semantic database.
	 * @param statement
	 */
	void removeStatement(Statement statement);
	
	/**
	 * Delete the {@link Statement} defined by the given subject, predicate and object.
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	void removeStatement(Resource subject, URI predicate, Value object);
	
	/**
	 * Delete the {@link Statement} defined by the given subject, predicate, object and graph.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param context
	 */
	void removeStatement(Resource subject, URI predicate, Value object, Resource context);
	
	/**
	 * Delete the given {@link Collection} of {@link Statement}s from the repository.
	 * @param statements
	 */
	void removeStatement(Collection<? extends Statement> statements);
	
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
	GraphQueryResult getStatementsForGraphQuery(String graphQuery) throws RepositoryException, QueryCreationException, QueryEvaluationException, QueryInterruptedException, MalformedQueryException;
	
	/**
	 * Clear all connections and other resources in use.
	 */
	void shutdown();

}
