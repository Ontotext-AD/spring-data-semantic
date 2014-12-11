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
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.repository.RepositoryConnection;


public class TupleSparqlQuery extends AbstractSparqlQuery implements TupleQuery {

	private TupleQuery query;
	
	private boolean count = false;
	
	private static final String COUNT_URI = "http://www.ontotext.com/count";
	
	public TupleSparqlQuery(String source, RepositoryConnection connection) throws MalformedQueryException, QueryEvaluationException, UnsupportedQueryLanguageException {
		super(source, connection);
		if (!(getParsedQuery() instanceof ParsedTupleQuery) ) {
			throw new QueryEvaluationException("Invalid tuple query.");
		}
	}
	
	public TupleQueryResult evaluate() throws QueryEvaluationException {
		prepareTupleQuery();
		return query.evaluate();
	}

	public void evaluate(TupleQueryResultHandler handler)
			throws QueryEvaluationException, TupleQueryResultHandlerException {
		prepareTupleQuery();
		query.evaluate(handler);
	}
	
	private void prepareTupleQuery() throws QueryEvaluationException {
		try {
			prePrepare();
			query = connection.prepareTupleQuery(QueryLanguage.SPARQL, str);
			postPrepare();
		} catch (OpenRDFException e) {
			throw new QueryEvaluationException(e);
		}
	}

	protected TupleQuery getQuery() {
		return query;
	}
	
	public boolean isCount() {
		return count;
	}

	public void setCount(boolean count) {
		this.count = count;
	}

	@Override
	protected void postPrepare() {
		super.postPrepare();
		if (count) {			
			if (query.getDataset() == null) {
				query.setDataset(new DatasetImpl());
			}
			((DatasetImpl) query.getDataset()).addDefaultGraph(new URIImpl(COUNT_URI));
		}		
	}
	
}
