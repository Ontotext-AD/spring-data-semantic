/*******************************************************************************
 * Copyright 2012 Ontotext AD
 ******************************************************************************/
package org.springframework.data.semantic.support.database;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.base.RepositoryConnectionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedRepositoryConnection extends RepositoryConnectionWrapper {
	private SesameConnectionPool connectionPool;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private StackTraceElement[] cause;
	
	public ExtendedRepositoryConnection(SesameConnectionPool connectionPool, Repository repository, RepositoryConnection connection){
		super(repository, connection);
		this.connectionPool = connectionPool;
	}
	
	protected void destroy(){
		try {
			getDelegate().close();
			connectionPool.getOpenConnections().decrementAndGet();
		} catch (RepositoryException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public boolean isOpen() throws RepositoryException {
		return connectionPool.isOpenConnection(this) && getDelegate().isOpen();
	}
	
	@Override
	public void close() throws RepositoryException {
		cause = null;
		connectionPool.closeConnection(this);
	}
	
	public void setCause(StackTraceElement[] cause){
		this.cause = cause;
	}
	
	public StackTraceElement[] getCause(){
		return cause;
	}
}
