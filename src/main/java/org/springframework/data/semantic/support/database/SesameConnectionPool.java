/*******************************************************************************
 * Copyright 2012 Ontotext AD
 ******************************************************************************/
package org.springframework.data.semantic.support.database;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A connection pool implementation for Sesame semantic repositories.
 * 
 * @author konstantin.pentchev
 */
public final class SesameConnectionPool {
	private BlockingQueue<ExtendedRepositoryConnection> availableList;
	private Map<ExtendedRepositoryConnection, Long> inUseList;
	private AtomicInteger openConnections = new AtomicInteger(0);
	private int maxConnections;
	private Repository repo;
	private Logger logger = LoggerFactory.getLogger(getClass());
	//private String config;
	private Thread unusedConnectionsCollectorThread;

	/**
	 * Create a new instance of the connection pool
	 * 
	 * @param repository
	 *            Sesame repository 
	 * @param config
	 * 
	 * @param maxConnections
	 *            maximum number of connections to the repository
	 */
	public SesameConnectionPool(Repository repository,
			int maxConnections) {
		this.maxConnections = maxConnections;
		this.availableList = new ArrayBlockingQueue<ExtendedRepositoryConnection>(
				maxConnections, true);
		//this.config = config;
		this.repo = repository;
		this.inUseList = new ConcurrentHashMap<ExtendedRepositoryConnection, Long>();
		
		unusedConnectionsCollectorThread = new Thread(new UnusedConnectionsCollector(), "UnusedConnectionsCollector");
		unusedConnectionsCollectorThread.start();
	}
	
	public void shutdownThread(){
		unusedConnectionsCollectorThread.interrupt();
	}

	/**
	 * Returns a connection to the repository by taking it from the synchronized
	 * blocking queue. If no connection is available and the connection limit is
	 * not reached, a new connection is opened. Otherwise the method waits until
	 * a connection is made available.
	 * 
	 * @return RepositoryConnection implementation of class
	 *         ExtendedRepositoryConnection
	 * @throws RepositoryException 
	 */
	public RepositoryConnection getConnection() throws RepositoryException {
		return getConnectionFromPool();
	}
	
	

	/**
	 * Adds the repository connection back to the queue of available
	 * connections. All statements are committed before making the connection
	 * available.
	 * 
	 * @param connection
	 *            The ExtendedRepositoryConnection to be closed, i.e. made
	 *            available.
	 * @throws RepositoryException
	 */
	public void closeConnection(ExtendedRepositoryConnection connection)
			throws RepositoryException {
		inUseList.remove(connection);
		availableList.add(connection);
	}

	/**
	 * Checks if a connection is open. By the definition of using a connection
	 * pool a connection is open, i.e. available, if it is in the list of
	 * available connections.
	 * 
	 * @param connection
	 *            The connection to be checked.
	 * @return Returns true if the connection is in the queue of available
	 *         connections.
	 */
	public boolean isOpenConnection(RepositoryConnection connection) {
		return !availableList.contains(connection);
	}
	
	private RepositoryConnection getConnectionFromPool() throws RepositoryException {
		ExtendedRepositoryConnection connection  = availableList.poll();
		if(connection == null){
			if (openConnections.get() < maxConnections) {
				connection = new ExtendedRepositoryConnection(this, repo.getConnection());
				openConnections.getAndIncrement();
				if(openConnections.get() == maxConnections-1) {
					logger.info("Reached maximum number of opened connections: "+maxConnections);
				}
			}
			else {
				try {
					connection = availableList.take();
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		inUseList.put(connection, System.currentTimeMillis());
		return connection;
	}
	
	public void shutdown(){
		while(!availableList.isEmpty()){
			availableList.poll().destroy();
		}
	}

	public AtomicInteger getOpenConnections() {
		return openConnections;
	}

	public void setOpenConnections(AtomicInteger openConnections) {
		this.openConnections = openConnections;
	}
	
	private class UnusedConnectionsCollector implements Runnable {

		@Override
		public void run() {
			try {
				while(true){
					for(Entry<ExtendedRepositoryConnection, Long> entry : inUseList.entrySet()){
						if(System.currentTimeMillis() - entry.getValue() > 900000){
							try {
								entry.getKey().close();
							} catch (RepositoryException e) {
								logger.error(e.getMessage(),e);
							}
						}
					}
				
					Thread.sleep(5000);
				} 
			}catch (InterruptedException e) {
					logger.info("Shutting down thread "+Thread.currentThread().getName());
					Thread.currentThread().interrupt();
			}
			
		}
		
	}

}
