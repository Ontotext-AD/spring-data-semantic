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
package org.springframework.data.semantic.testutils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.query.QueryCreationException;
import org.springframework.data.semantic.core.SemanticDatabase;

public class Utils {

	private static Logger log = LoggerFactory.getLogger(Utils.class);

	public static void populateTestRepository(SemanticDatabase sdb) {
		
		//check if already populated
		try {
			if(hasStatements(sdb)) return;
		} catch (Exception e) {
			log.error("Error checking if repository is empty.");
			return;
		}
		
		String[] filesToLoad = new String[]{"wine.ttl", "model-data.n3", "date-data.n3"};
		for(String fileName : filesToLoad){
			File sampleDataFile = getSampleDataFile(fileName);
			if(sampleDataFile == null) return;		
			try {							
				//populate with sample data			
				sdb.addStatementsFromFile(sampleDataFile);
			} catch (IOException e) {
				log.error("Error loading the data file '"+sampleDataFile.getName()+"'", e);
			} catch(RepositoryException e) {
				log.error("Error accessing the repository", e);
			} catch (RDFParseException e) {
				e.printStackTrace();
			}
		}
		
	}

	//retrieves the sample RDF data file as configured in a properties file
	private static File getSampleDataFile(String name) {
		try {
			File sampleDataFile = new ClassPathResource(name).getFile();
			return sampleDataFile;
		} catch (IOException e) {
			log.error("Error retrieving the sample data file '"+name+"'", e);
		}
		return null;
	}
		
	// Checks if the repository has any statements
	private static boolean hasStatements(SemanticDatabase sdb) 
			throws QueryInterruptedException, RepositoryException, 
			QueryCreationException, QueryEvaluationException, MalformedQueryException {
		
		String testQuery = "SELECT ?x {?x ?y ?z} LIMIT 1";
		List<BindingSet> result;
		result = sdb.getQueryResults(testQuery);
		if (!result.isEmpty()) {
			return true;
		}		
		return false;
	}
}
