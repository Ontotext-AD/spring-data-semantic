/*******************************************************************************
 * Copyright 2012 Ontotext AD
 ******************************************************************************/
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
