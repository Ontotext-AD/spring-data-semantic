/**
 * Copyright (C) 2014 Ontotext AD (info@ontotext.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.semantic.core;

import info.aduna.iteration.Iterations;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResults;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.repository.query.QueryCreationException;
import org.springframework.data.semantic.query.BooleanSparqlQuery;
import org.springframework.data.semantic.query.GraphSparqlQuery;
import org.springframework.data.semantic.query.TupleSparqlQuery;
import org.springframework.data.semantic.support.database.SesameConnectionPool;
import org.springframework.data.semantic.support.exceptions.SemanticDatabaseAccessException;
import org.springframework.data.semantic.support.exceptions.UncategorizedSemanticDataAccessException;

/**
 * An implementation of {@link SemanticDatabase} that uses connection pooling.
 * 
 * @author konstantin.pentchev
 *
 */
public class PooledSemanticDatabase implements SemanticDatabase{

	private SesameConnectionPool connectionPool;
	
	private Logger logger = LoggerFactory.getLogger(PooledSemanticDatabase.class);

	public PooledSemanticDatabase(Repository repository, int maxConnections){
		this(new SesameConnectionPool(repository, maxConnections, 6000));			
	}

	public PooledSemanticDatabase(SesameConnectionPool pool){
		this.connectionPool = pool;
	}

	public List<Namespace> getNamespaces() throws RepositoryException {
		RepositoryConnection con = connectionPool.getConnection();
		try {
			RepositoryResult<Namespace> repoResult = con.getNamespaces();
			return Iterations.asList(repoResult);
		} finally {
			con.close();
		}		
	}

	public void addNamespace(String prefix, String namespace)
			throws RepositoryException {
		RepositoryConnection con = connectionPool.getConnection();
		try {
			con.setNamespace(prefix, namespace);
		} finally {
			con.close();
		}		
	}

	public List<Resource> getContexts() throws RepositoryException {
		RepositoryConnection con = connectionPool.getConnection();
		try {
			RepositoryResult<Resource> contexts = con.getContextIDs();
			return Iterations.asList(contexts);
		} finally {
			con.close();
		}

	}

	public List<BindingSet> getQueryResults(String source)
			throws RepositoryException, QueryEvaluationException, MalformedQueryException {
		return getQueryResults(source, null, null);
	}

	public List<BindingSet> getQueryResults(String source, Long offset, Long limit) 
			throws RepositoryException, QueryEvaluationException, MalformedQueryException {

		RepositoryConnection con = connectionPool.getConnection();
		try{
			TupleSparqlQuery query = new TupleSparqlQuery(source, con);
			if(limit != null){
				query.setLimit(limit);
			}
			if(offset != null){
				query.setOffset(offset);
			}

			return Iterations.asList(query.evaluate());
		} finally {
			con.close();
		}
	}

	@Override
	public boolean getBooleanQueryResult(String source) throws RepositoryException, QueryCreationException, QueryEvaluationException,
			QueryInterruptedException, MalformedQueryException {
		RepositoryConnection con = connectionPool.getConnection();
		try {
			BooleanSparqlQuery query = new BooleanSparqlQuery(source, con);
			return query.evaluate();
		} finally {
			con.close();
		}
	}


	//-------------------------------------------------------------------------
	
	public List<Statement> getStatementsForSubject(Resource subject){
		return getStatementsForQuadruplePattern(subject, null, null, null);
	}

	public List<Statement> getStatementsForPredicate(URI predicate){
		return getStatementsForQuadruplePattern(null, predicate, null, null);
	}

	public List<Statement> getStatementsForObject(Value object){
		return getStatementsForQuadruplePattern(null, null, object, null);
	}

	public List<Statement> getStatementsForContext(Resource context){
		return getStatementsForQuadruplePattern(null, null, null, context);
	}

	public List<Statement> getStatementsForTriplePattern(Resource subject,
			URI predicate, Value object){
		return getStatementsForQuadruplePattern(subject, predicate, object, null);
	}

	public List<Statement> getStatementsForQuadruplePattern(Resource subject,
			URI predicate, Value object, Resource context){
		RepositoryConnection con = connectionPool.getConnection();
		try {
			RepositoryResult<Statement> repoResult = con.getStatements(subject, predicate, object, true, context);
			return Iterations.asList(repoResult);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
			throw new SemanticDatabaseAccessException(e);
		} finally {
			try {
				con.close();
			} catch (RepositoryException e) {
				logger.error(e.getMessage(), e);
				throw new SemanticDatabaseAccessException(e);
			}
		}		
	}

	public void addStatement(Statement statement) {
		RepositoryConnection con = connectionPool.getConnection();
		try {
			con.add(statement);
			con.commit();
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			try {
				con.rollback();
			} catch (RepositoryException e1) {
				logger.error(e.getMessage(),e);
			}
			throw new SemanticDatabaseAccessException(e);
		}finally {
			try {
				con.close();
			} catch (RepositoryException e) {
				logger.error(e.getMessage(),e);
			}
		}		
	}

	public void addStatement(Resource subject, URI predicate, Value object) {
		addStatement(new StatementImpl(subject, predicate, object));
	}

	public void addStatement(Resource subject, URI predicate, Value object,
			Resource context) {
		addStatement(new ContextStatementImpl(subject, predicate, object, context));	
	}

	public void addStatements(Collection<? extends Statement> statements) {
		RepositoryConnection con = connectionPool.getConnection();
		try {
			con.add(statements);
			con.commit();
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			try {
				con.rollback();
			} catch (RepositoryException e1) {
				logger.error(e.getMessage(),e);
			}
			throw new SemanticDatabaseAccessException(e);
		} finally {
			try {
				con.close();
			} catch (RepositoryException e) {
				logger.error(e.getMessage(),e);
			}
		}		
	}

	public void addStatementsFromFile(File rdfSource) {

		RDFFormat format = RDFFormat.forFileName(rdfSource.getName());
		if(format == null) {
			throw new InvalidParameterException("File should be in a valid RDF format; cannot determine one from the file extension.");
		}
		RepositoryConnection con = connectionPool.getConnection();
		try {
			con.add(rdfSource, null, format, new Resource[]{});
		} catch (RDFParseException e) {
			logger.error(e.getMessage(),e);
			try {
				con.rollback();
			} catch (RepositoryException e1) {
				logger.error(e.getMessage(),e);
			}
			throw new InvalidDataAccessApiUsageException(e.getMessage(), e);
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			try {
				con.rollback();
			} catch (RepositoryException e1) {
				logger.error(e.getMessage(),e);
			}
			throw new SemanticDatabaseAccessException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			try {
				con.rollback();
			} catch (RepositoryException e1) {
				logger.error(e.getMessage(),e);
			}
			throw new UncategorizedSemanticDataAccessException(e.getMessage(), e);
		} finally {
			try {
				con.close();
			} catch (RepositoryException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}

	public void removeStatement(Statement statement) {
		RepositoryConnection con = connectionPool.getConnection();
		try {
			con.remove(statement);
			con.commit();
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			try {
				con.rollback();
			} catch (RepositoryException e1) {
				logger.error(e.getMessage(),e);
			}
			throw new SemanticDatabaseAccessException(e);
		}finally {
			try {
				con.close();
			} catch (RepositoryException e) {
				logger.error(e.getMessage(),e);
			}
		}		
	}

	public void removeStatements(Resource subject, URI predicate, Value object) {
		removeStatements(subject, predicate, object, null);
	}

	public void removeStatements(Resource subject, URI predicate, Value object,
			Resource context) {
		RepositoryConnection con = connectionPool.getConnection();
		try {
			con.begin();
			con.remove(subject, predicate, object, context);
			con.commit();
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			try {
				con.rollback();
			} catch (RepositoryException e1) {
				logger.error(e.getMessage(),e);
			}
			throw new SemanticDatabaseAccessException(e);
		} finally {
			try {
				con.close();
			} catch (RepositoryException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	@Override
	public void removeStatements(Collection<? extends Statement> statements) {
		RepositoryConnection con = connectionPool.getConnection();
		try {
			con.begin();
			con.remove(statements);
			con.commit();
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			try {
				con.rollback();
			} catch (RepositoryException e1) {
				logger.error(e.getMessage(),e);
			}
			throw new SemanticDatabaseAccessException(e);
		} finally {
			try {
				con.close();
			} catch (RepositoryException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}

	@Override
	public void shutdown() {
		this.connectionPool.shutDown();
		this.connectionPool.shutdownThread();
	}

	@Override
	public Namespace getDefaultNamespace() throws RepositoryException {		
		RepositoryConnection con = connectionPool.getConnection();
		try {
			String defaultNSName = con.getNamespace("");
			if(defaultNSName == null){
				return new NamespaceImpl("", "urn:spring-data-semantic:");
			}
			return new NamespaceImpl("", defaultNSName);
		} finally {
			con.close();
		}		
	}

	@Override
	public Model getGraphQueryResults(String graphQuery)
			throws RepositoryException, QueryCreationException,
			QueryEvaluationException, QueryInterruptedException, MalformedQueryException {
		return getGraphQueryResults(graphQuery, null, null);
	}
	
	@Override
	public Model getGraphQueryResults(String graphQuery, Long offset, Long limit) throws RepositoryException, QueryCreationException,
			QueryEvaluationException, QueryInterruptedException, MalformedQueryException {
		logger.info("Executing query \""+graphQuery+"\"");
		RepositoryConnection con = connectionPool.getConnection();		
		try{
			GraphSparqlQuery query = new GraphSparqlQuery(graphQuery, con);
			if(offset != null){
				query.setOffset(offset);
			}
			if(limit != null){
				query.setLimit(limit);
			}
			return QueryResults.asModel(query.evaluate());
		}
		finally {
			con.close();
		}
	}

	@Override
	public long count() {
		long size = 0;
		RepositoryConnection con = connectionPool.getConnection();
		try {
			 size = con.size();
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SemanticDatabaseAccessException(e);
		} finally{
			try {
				con.close();
			} catch (RepositoryException e) {
				logger.error(e.getMessage(),e);
				throw new SemanticDatabaseAccessException(e);
			}
		}
		return size;
	}

	@Override
	public void clear() {
		RepositoryConnection con = connectionPool.getConnection();
		try {
			con.remove(null, null, null, new Resource[0]);
			con.commit();
		} catch (RepositoryException e) {
			try {
				con.rollback();
			} catch (RepositoryException e1) {
				logger.error(e.getMessage(),e);
				throw new SemanticDatabaseAccessException(e);
			}
			logger.error(e.getMessage(),e);
			throw new SemanticDatabaseAccessException(e);
		} finally{
			try {
				con.close();
			} catch (RepositoryException e) {
				logger.error(e.getMessage(),e);
				throw new SemanticDatabaseAccessException(e);
			}
		}
	}

	@Override
	public void executeUpdateStatement(String update) {
		RepositoryConnection con = this.connectionPool.getConnection();
		try {
			Update updateQuery = con.prepareUpdate(QueryLanguage.SPARQL, update);
			updateQuery.execute();
		} catch (RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new SemanticDatabaseAccessException(e);
		} catch (MalformedQueryException e) {
			logger.error(e.getMessage(),e);
			throw new SemanticDatabaseAccessException(e);
		} catch (UpdateExecutionException e) {
			logger.error(e.getMessage(),e);
			throw new SemanticDatabaseAccessException(e);
		} finally {
			try {
				con.close();
			} catch (RepositoryException e) {
				logger.error(e.getMessage(),e);
				throw new SemanticDatabaseAccessException(e);
			}
		}
		
	}


}
