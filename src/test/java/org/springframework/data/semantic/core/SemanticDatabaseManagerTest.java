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

import static org.junit.Assert.assertTrue;

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
import org.springframework.core.io.ClassPathResource;

public class SemanticDatabaseManagerTest {

	private static String localRepositoryBase;
	private static String newRepoID;
	
	@BeforeClass
	public static void prepare() {
		ClassPathResource propsResource = new ClassPathResource("test-repository.properties"); 
		Properties props = new Properties();
		try {
			props.load(propsResource.getInputStream());
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		localRepositoryBase = props.getProperty("localRepositoryBase", "");		
		newRepoID = "test-repo-"+String.valueOf((int)Math.floor(Math.random()*10000));
		
	}
		
	//----Local repository tests-----------------------------------------------
	
	@Test
	public void testCreateLocalRepoWithDefaultConfigAndCustomID() {
		try {
			Repository repo = SemanticDatabaseManager.getRepository(localRepositoryBase+"/"+Protocol.REPOSITORIES+"/"+newRepoID, null);
			assertTrue(testRepoSimple(repo));
		} catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);			
		}
	}
	
	@Test
	public void testOpenExistingLocalRepo() {
		try {
			Repository repo = SemanticDatabaseManager.getRepository(localRepositoryBase+"/"+Protocol.REPOSITORIES+"/"+newRepoID, null);
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
