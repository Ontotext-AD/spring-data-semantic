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
package org.springframework.data.semantic.query;

import org.openrdf.OpenRDFException;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;


public class GraphSparqlQuery extends AbstractSparqlQuery implements GraphQuery {

	private GraphQuery query;
	
	public GraphSparqlQuery(String source, RepositoryConnection connection) throws MalformedQueryException, QueryEvaluationException, RepositoryException {
		super(source, connection);
		if (!(getParsedQuery() instanceof ParsedGraphQuery) ) {
			throw new QueryEvaluationException("Invalid graph query.");
		}
	}
	
	public GraphQueryResult evaluate() throws QueryEvaluationException {
		prepareGraphQuery();
		return query.evaluate();
	}

	public void evaluate(RDFHandler handler) throws QueryEvaluationException,
			RDFHandlerException {
		prepareGraphQuery();
		query.evaluate(handler);
	}
	
	private void prepareGraphQuery() throws QueryEvaluationException {
		try {
			prePrepare();
			query = connection.prepareGraphQuery(QueryLanguage.SPARQL, str);
			postPrepare();
		} catch (OpenRDFException e) {
			throw new QueryEvaluationException(e);
		}
	}
	
	protected GraphQuery getQuery() {
		return query;
	}

}
