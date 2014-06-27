package org.springframework.data.semantic.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.http.protocol.Protocol;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.springframework.core.io.ClassPathResource;

public class SemanticDatabaseManagerTest {

	private static String url;
	private static String securedUrl;
	private static String newRepoID;
	private static String username;
	private static String password;
	private static String localRepositoryBase;
	
	@BeforeClass
	public static void prepare() {
		ClassPathResource propsResource = new ClassPathResource("test-repository.properties"); 
		Properties props = new Properties();
		try {
			props.load(propsResource.getInputStream());
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
		url = props.getProperty("url", "");
		securedUrl = props.getProperty("securedUrl", "");		
		username = props.getProperty("username", "");
		password = props.getProperty("password", "");
		
		localRepositoryBase = props.getProperty("localRepositoryBase", "");		
		newRepoID = "test-repo-"+String.valueOf((int)Math.floor(Math.random()*10000));
		
	}
		
	//----Local repository tests-----------------------------------------------
	
	@Test
	public void testCreateLocalRepoWithDefaultConfigAndCustomID() {
		try {
			Repository repo = SemanticDatabaseManager.getRepository(localRepositoryBase, newRepoID);
			assertTrue(testRepoSimple(repo));
		} catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);			
		}
	}
	
	@Test
	public void testOpenExistingLocalRepo() {
		try {
			Repository repo = SemanticDatabaseManager.getRepository(localRepositoryBase+"/"+Protocol.REPOSITORIES+"/"+newRepoID);
			assertTrue(testRepoSimple(repo));
		}catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testCreateLocalRepoWithCustomConfig() {		
		RepositoryConfig config = null;
		try {
			config = SemanticDatabaseManager.getDefaultConfig();
			config.setID(newRepoID);
			Repository repo = SemanticDatabaseManager.getRepository(localRepositoryBase, "", config);
			assertTrue(testRepoSimple(repo));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}		
	}	
	
	
	//----Remote repository tests----------------------------------------------
		
	//@Test
	public void testCreateRemoteRepoWithDefaultConfig() {
		try {
			Repository repo = SemanticDatabaseManager.getRepository(url, newRepoID);
			assertTrue(testRepoSimple(repo));
		}catch(UnsupportedOperationException e) {
			assertTrue(true);		
		}catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}		
	}
		
	//@Test
	public void testCreateRemoteRepoWithCustomConfig() {		
		try {
			RepositoryConfig config = SemanticDatabaseManager.getDefaultConfig();
			config.setID(newRepoID);
			Repository repo = SemanticDatabaseManager.getRepository(url, newRepoID, config);
			
			//Repository creation for remote locations is not allowed, the operation above should not succeed.
			assertTrue(false);
		}catch(UnsupportedOperationException e) {
			assertTrue(true);
		}catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	//@Test
	public void testOpenExistingRemoteRepo() {
		try {
			Repository repo = SemanticDatabaseManager.getRepository(url, "test");
			assertTrue(testRepoSimple(repo));
		}catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}	
	
	
	//----Auxiliary methods----------------------------------------------------
	
	private boolean testRepoWithQuery(Repository repo) {
		RepositoryConnection conn = null;
		try {
			conn = repo.getConnection();
			ValueFactory f = repo.getValueFactory();

			//----add a new statement----
			URI alice = f.createURI("http://example.org/people/alice");
			URI person = f.createURI("http://example.org/people/person");
			conn.add(alice, RDF.TYPE, person);

			//----get the statement----
			String queryStr = "SELECT DISTINCT ?o WHERE {<http://example.org/people/alice> rdf:type ?o}";
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL,  queryStr);
			TupleQueryResult result = tupleQuery.evaluate();
			while(result.hasNext()) {
				BindingSet resultEntry = result.next();
				if("http://example.org/people/person".equalsIgnoreCase(resultEntry.getBinding("o").getValue().stringValue())) {
					return true;
				}
			}			
			result.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			}catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private boolean testRepoSimple(Repository repo) {
		try {
			return repo.isWritable();
		}catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
