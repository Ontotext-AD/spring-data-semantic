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
