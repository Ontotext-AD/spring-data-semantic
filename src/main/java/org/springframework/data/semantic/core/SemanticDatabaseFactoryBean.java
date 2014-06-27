
package org.springframework.data.semantic.core;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigSchema;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

public class SemanticDatabaseFactoryBean implements
		FactoryBean<SemanticDatabase> {

	private String url;

	private String username;

	private String password;

	private int maxConnections;

	private Repository repo;

	private String configFile;

	private SemanticDatabase semanticDB;
	
	private String defaultNamespace;

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the maxConnections
	 */
	public int getMaxConnections() {
		return maxConnections;
	}

	/**
	 * @param maxConnections
	 *            the maxConnections to set
	 */
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	@Override
	public SemanticDatabase getObject() throws Exception {
		if (semanticDB == null) {
			semanticDB = getInstance();
		}
		return semanticDB;
	}

	private SemanticDatabase getInstance() {
		if (repo == null) {
			repo = getRepository();
		}
		SemanticDatabase db = new PooledSemanticDatabase(repo, maxConnections);
		return db;
	}
	
	private Repository getRepository(){
		RepositoryConfig config = getConfig();
		if(StringUtils.hasText(username) && StringUtils.hasText(password)){
			return SemanticDatabaseManager.getRepository(url, username, password, config);
		}
		else{
			return SemanticDatabaseManager.getRepository(url, config);
		}
	}
	
	private RepositoryConfig getConfig(){
		Graph graph = new GraphImpl();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
		rdfParser.setRDFHandler(new StatementCollector(graph));
		
		try {
			rdfParser.parse(resolver.getResource(configFile).getInputStream(), RepositoryConfigSchema.NAMESPACE);
			Resource repositoryNode = GraphUtil.getUniqueSubject(graph, RDF.TYPE, RepositoryConfigSchema.REPOSITORY);
			return RepositoryConfig.create(graph, repositoryNode);
		} catch (IOException e) {
			throw new IllegalArgumentException("The given configuration file cannot be found at '" + configFile
					+ "' - it is not a valid location or the file does not exist.", e);
		} catch (Exception e) {
			throw new IllegalArgumentException("The given location '" + url
					+ "' is not a valid local repository location.", e);
		}
		
	}

	@Override
	public Class<?> getObjectType() {
		return SemanticDatabase.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@PreDestroy
	public void shutdown() {
		if (semanticDB != null) {
			semanticDB.shutdown();
		}
	}

	/**
	 * @return the configFile
	 */
	public String getConfigFile() {
		return configFile;
	}

	/**
	 * @param configFile
	 *            the configFile to set
	 */
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	

}
