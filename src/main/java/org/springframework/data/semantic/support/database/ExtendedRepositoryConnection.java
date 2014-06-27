/*******************************************************************************
 * Copyright 2012 Ontotext AD
 ******************************************************************************/
package org.springframework.data.semantic.support.database;

import info.aduna.iteration.Iteration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

public class ExtendedRepositoryConnection implements RepositoryConnection{
	private SesameConnectionPool connectionPool;
	private RepositoryConnection connection;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public ExtendedRepositoryConnection(SesameConnectionPool connectionPool, RepositoryConnection connection){
		this.connectionPool = connectionPool;
		this.connection = connection;
	}
	
	protected void destroy(){
		try {
			connection.close();
			connectionPool.getOpenConnections().decrementAndGet();
		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public ValueFactory getValueFactory() {
		return connection.getValueFactory();
	}
	@Override
	public boolean isOpen() throws RepositoryException {
		return connectionPool.isOpenConnection(this) && connection.isOpen();
	}
	@Override
	public void close() throws RepositoryException {
		connectionPool.closeConnection(this);
	}
	@Override
	public Query prepareQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		return connection.prepareQuery(ql, query);
	}
	@Override
	public Query prepareQuery(QueryLanguage ql, String query, String baseURI)
			throws RepositoryException, MalformedQueryException {
		return connection.prepareQuery(ql, query, baseURI);
	}
	@Override
	public TupleQuery prepareTupleQuery(QueryLanguage ql, String query)
			throws RepositoryException, MalformedQueryException {
		return connection.prepareTupleQuery(ql, query);
	}
	@Override
	public TupleQuery prepareTupleQuery(QueryLanguage ql, String query,
			String baseURI) throws RepositoryException, MalformedQueryException {
		return connection.prepareTupleQuery(ql, query, baseURI);
	}
	@Override
	public GraphQuery prepareGraphQuery(QueryLanguage ql, String query)
			throws RepositoryException, MalformedQueryException {
		return connection.prepareGraphQuery(ql, query);
	}
	@Override
	public GraphQuery prepareGraphQuery(QueryLanguage ql, String query,
			String baseURI) throws RepositoryException, MalformedQueryException {
		return connection.prepareGraphQuery(ql, query);
	}
	@Override
	public BooleanQuery prepareBooleanQuery(QueryLanguage ql, String query)
			throws RepositoryException, MalformedQueryException {
		return connection.prepareBooleanQuery(ql, query);
	}
	@Override
	public BooleanQuery prepareBooleanQuery(QueryLanguage ql, String query,
			String baseURI) throws RepositoryException, MalformedQueryException {
		return connection.prepareBooleanQuery(ql, query, baseURI);
	}
	@Override
	public Update prepareUpdate(QueryLanguage ql, String update)
			throws RepositoryException, MalformedQueryException {
		return connection.prepareUpdate(ql, update);
	}
	@Override
	public Update prepareUpdate(QueryLanguage ql, String update, String baseURI)
			throws RepositoryException, MalformedQueryException {
		return connection.prepareUpdate(ql, update, baseURI);
	}
	@Override
	public RepositoryResult<Resource> getContextIDs()
			throws RepositoryException {
		return connection.getContextIDs();
	}
	@Override
	public RepositoryResult<Statement> getStatements(Resource subj, URI pred,
			Value obj, boolean includeInferred, Resource... contexts)
			throws RepositoryException {
		return connection.getStatements(subj, pred, obj, includeInferred, contexts);
	}
	@Override
	public boolean hasStatement(Resource subj, URI pred, Value obj,
			boolean includeInferred, Resource... contexts)
			throws RepositoryException {
		return connection.hasStatement(subj, pred, obj, includeInferred, contexts);
	}
	@Override
	public boolean hasStatement(Statement st, boolean includeInferred,
			Resource... contexts) throws RepositoryException {
		return connection.hasStatement(st, includeInferred, contexts);
	}
	@Override
	public void exportStatements(Resource subj, URI pred, Value obj,
			boolean includeInferred, RDFHandler handler, Resource... contexts)
			throws RepositoryException, RDFHandlerException {
		connection.exportStatements(subj, pred, obj, includeInferred, handler, contexts);
	}
	@Override
	public void export(RDFHandler handler, Resource... contexts)
			throws RepositoryException, RDFHandlerException {
		connection.export(handler, contexts);
	}
	@Override
	public long size(Resource... contexts) throws RepositoryException {
		return connection.size(contexts);
	}
	@Override
	public boolean isEmpty() throws RepositoryException {
		return connection.isEmpty();
	}
	@Override
	public void setAutoCommit(boolean autoCommit) throws RepositoryException {
		connection.setAutoCommit(autoCommit);
	}
	@Override
	public boolean isAutoCommit() throws RepositoryException {
		return connection.isAutoCommit();
	}
	@Override
	public void commit() throws RepositoryException {
		connection.commit();
	}
	@Override
	public void rollback() throws RepositoryException {
		connection.rollback();
	}
	@Override
	public void add(InputStream in, String baseURI, RDFFormat dataFormat,
			Resource... contexts) throws IOException, RDFParseException,
			RepositoryException {
		connection.add(in, baseURI, dataFormat, contexts);
	}
	@Override
	public void add(Reader reader, String baseURI, RDFFormat dataFormat,
			Resource... contexts) throws IOException, RDFParseException,
			RepositoryException {
		connection.add(reader, baseURI, dataFormat, contexts);
	}
	@Override
	public void add(URL url, String baseURI, RDFFormat dataFormat,
			Resource... contexts) throws IOException, RDFParseException,
			RepositoryException {
		connection.add(url, baseURI, dataFormat, contexts);
	}
	@Override
	public void add(File file, String baseURI, RDFFormat dataFormat,
			Resource... contexts) throws IOException, RDFParseException,
			RepositoryException {
		connection.add(file, baseURI, dataFormat, contexts);
	}
	@Override
	public void add(Resource subject, URI predicate, Value object,
			Resource... contexts) throws RepositoryException {
		connection.add(subject, predicate, object, contexts);
	}
	@Override
	public void add(Statement st, Resource... contexts)
			throws RepositoryException {
		connection.add(st, contexts);
	}
	@Override
	public void add(Iterable<? extends Statement> statements,
			Resource... contexts) throws RepositoryException {
		connection.add(statements, contexts);
	}
	@Override
	public <E extends Exception> void add(
			Iteration<? extends Statement, E> statements, Resource... contexts)
			throws RepositoryException, E {
		connection.add(statements, contexts);
	}
	@Override
	public void remove(Resource subject, URI predicate, Value object,
			Resource... contexts) throws RepositoryException {
		connection.remove(subject, predicate, object, contexts);
	}
	@Override
	public void remove(Statement st, Resource... contexts)
			throws RepositoryException {
		connection.remove(st, contexts);
	}
	@Override
	public void remove(Iterable<? extends Statement> statements,
			Resource... contexts) throws RepositoryException {
		connection.remove(statements, contexts);
	}
	@Override
	public <E extends Exception> void remove(
			Iteration<? extends Statement, E> statements, Resource... contexts)
			throws RepositoryException, E {
		connection.remove(statements, contexts);
	}
	@Override
	public void clear(Resource... contexts) throws RepositoryException {
		connection.clear(contexts);
	}
	@Override
	public RepositoryResult<Namespace> getNamespaces()
			throws RepositoryException {
		return connection.getNamespaces();
	}
	@Override
	public String getNamespace(String prefix) throws RepositoryException {
		return connection.getNamespace(prefix);
	}
	@Override
	public void setNamespace(String prefix, String name)
			throws RepositoryException {
		connection.setNamespace(prefix, name);
	}
	@Override
	public void removeNamespace(String prefix) throws RepositoryException {
		connection.removeNamespace(prefix);
	}
	@Override
	public void clearNamespaces() throws RepositoryException {
		connection.clearNamespaces();
	}
	@Override
	public Repository getRepository() {
		return connection.getRepository();
	}

	@Override
	public void setParserConfig(ParserConfig config) {
		connection.setParserConfig(config);
	}

	@Override
	public ParserConfig getParserConfig() {
		return connection.getParserConfig();
	}
}
