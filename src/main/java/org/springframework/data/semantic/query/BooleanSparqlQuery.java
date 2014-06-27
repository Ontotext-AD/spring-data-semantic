/*******************************************************************************
 * Copyright 2012 Ontotext AD
 ******************************************************************************/
package org.springframework.data.semantic.query;

import org.openrdf.OpenRDFException;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;


public class BooleanSparqlQuery extends AbstractSparqlQuery implements BooleanQuery {

	private BooleanQuery query;
	
	public BooleanSparqlQuery(String source, RepositoryConnection connection) throws MalformedQueryException,
			QueryEvaluationException, RepositoryException {
		super(source, connection);
		if (!(getParsedQuery() instanceof ParsedBooleanQuery)) {
			throw new QueryEvaluationException("Invalid boolean query.");
		}
	}
	
	public boolean evaluate() throws QueryEvaluationException {
		prepareBooleanQuery();
		return query.evaluate();
	}
	
	private void prepareBooleanQuery() throws QueryEvaluationException {
		try {
			prePrepare();
			query = connection.prepareBooleanQuery(QueryLanguage.SPARQL, str);
			postPrepare();
		} catch (OpenRDFException e) {
			throw new QueryEvaluationException(e);
		}
	}
	
	protected BooleanQuery getQuery() {
		return query;
	}

	@Override
	public void setLimit(long limit) {
	}
	
	protected void prePrepare() {	
	}
}
