/*******************************************************************************
 * Copyright 2012 Ontotext AD
 ******************************************************************************/
package org.springframework.data.semantic.query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.NumericLiteralImpl;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.query.algebra.Count;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.ProjectionElemList;
import org.openrdf.query.algebra.Slice;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.ValueExpr;
import org.openrdf.query.impl.AbstractQuery;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.QueryParserUtil;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailQuery;
import org.openrdf.repository.sail.SailRepositoryConnection;


public abstract class AbstractSparqlQuery extends AbstractQuery {
	
	private static final String DISABLE_SAMEAS_URI = "http://www.ontotext.com/disable-sameAs";

	protected String source;
	protected String str;
	private long limit;
	private long offset;
	private boolean sameAs = false;
	private ParsedQuery parsedQuery;
	
	protected RepositoryConnection connection;
	
	private Log logger = LogFactory.getLog(AbstractSparqlQuery.class);
	
		
	public AbstractSparqlQuery(String source, RepositoryConnection connection) throws MalformedQueryException, UnsupportedQueryLanguageException {
		if (source != null && source.length() > 0) {
			this.source = source;
			this.connection = connection;
			str = normalize(source);
			parsedQuery = QueryParserUtil.parseQuery(QueryLanguage.SPARQL, str, null);
			str = removeComments(str);
			dataset = parsedQuery.getDataset();
			TupleExpr expr = parsedQuery.getTupleExpr();
			if (expr instanceof Slice) {
				Slice slice = ((Slice) expr);
				offset = slice.getOffset();
				limit = slice.getLimit();
			} else {
				limit = -1;
			}
		} else {
			throw new MalformedQueryException();
		}
	}
	
	private String normalize(String source) {
		return source
			.replaceAll("\\u00a0", " ")
			.replaceAll("\\u00b0", "\t")
			.replaceAll("\\u2028", "\n");
	}
	
	private String removeComments(String text) {
		return Pattern.compile("^(\\s*)#.*$", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE)
			.matcher(text).replaceAll("");
	}
	
	private void setSailQueryOffset() {
		SailQuery query = ((SailQuery) getQuery());
		TupleExpr expr = query.getParsedQuery().getTupleExpr();
		Slice slice = null;
		if (expr instanceof Slice) {
			slice = (Slice) expr;
			slice.setOffset(offset);
		} else {
			slice = new Slice(query.getParsedQuery().getTupleExpr(), offset, limit);
		}
		query.getParsedQuery().setTupleExpr(slice);
	}
	
	private void setSailQueryLimit() {
		SailQuery query = ((SailQuery) getQuery());
		TupleExpr expr = query.getParsedQuery().getTupleExpr();
		Slice slice = null;
		if (expr instanceof Slice) {
			slice = (Slice) expr;
			slice.setLimit(limit);
		} else {
			slice = new Slice(query.getParsedQuery().getTupleExpr(), offset, limit);
		}
		query.getParsedQuery().setTupleExpr(slice);
	}
	
	private void setHTTPQueryOffset() {
		Pattern p = Pattern.compile("offset \\d+", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		if (offset > 0 && !m.find()) {
			str = str + " OFFSET " + offset;
		}
		else {
			str = m.replaceAll(offset > 0 ? "OFFSET " + offset : "");
		}
	}
	
	private void setHTTPQueryLimit() {
		Pattern p = Pattern.compile("limit\\s+\\d+", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		if (limit != -1 && !m.find()) {
			str = str + " LIMIT " + limit;
		}
		else {
			str = m.replaceAll(limit == -1 ? "" : "LIMIT " + limit);
		}
	}
	
	public void setSameAs(boolean sameAs) {
		this.sameAs = sameAs;
	}

	public boolean isSameAs() {
		return sameAs;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public long getLimit() {
		return limit;
	}

	public boolean hasLimit() {
		return limit >= 0;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getOffset() {
		return offset;
	}
	
	public Dataset getDataset() {
		if (getQuery() != null) {
			return getQuery().getDataset();
		}
		return dataset;
	}
	
	public BindingSet getBindings() {
		if (getQuery() != null) {
			return getQuery().getBindings();
		}
		return super.getBindings();
	}

	public List<String> getBindingNames() {
		if (parsedQuery != null) {
			return new ArrayList<String>(parsedQuery.getTupleExpr().getBindingNames());
		}
		return null;
	}
	
	public String getSource() {
		return source;
	}
	
	public String toString() {
		return source;
	}
	
	protected void prePrepare() {
		if (!(connection instanceof SailRepositoryConnection)) {
			setHTTPQueryOffset();
			setHTTPQueryLimit();
		}
	}
	
	protected void postPrepare() {
		
		Query query = getQuery();
		query.setIncludeInferred(includeInferred);
		query.setMaxQueryTime(maxQueryTime);
		
		
		for (Binding b : getBindings()) {
			query.setBinding(b.getName(), b.getValue());			
		}
		
		if (dataset != null) {
			if (query.getDataset() == null) {
				query.setDataset(new DatasetImpl());
			}
			for (URI d : dataset.getDefaultGraphs()) {
				((DatasetImpl) query.getDataset()).addDefaultGraph(d);
			} 
			for (URI d : dataset.getNamedGraphs()) {
				((DatasetImpl) query.getDataset()).addNamedGraph(d);
			}
		}
		
		if (sameAs) {
			if (query.getDataset() == null) {
				query.setDataset(new DatasetImpl());
			}
			((DatasetImpl) query.getDataset()).addDefaultGraph(connection.getValueFactory().createURI(DISABLE_SAMEAS_URI));
		}
		
		if (connection instanceof SailRepositoryConnection) {
			setSailQueryLimit();
			setSailQueryOffset();
		}
	}
	
	public ParsedQuery getParsedQuery() {
		return parsedQuery;
	}
	
	protected abstract Query getQuery();
	
	@Override
	public int hashCode() {
		int hash = (int) (str.hashCode() + offset*13 + limit*17);
		if(str.length() > 1){
			if(sameAs){
				hash += 31*str.charAt(0);
			}
			if(includeInferred){
				hash += 7*str.charAt(str.length()-1);
			}
		}
		return hash;
	}
	
	public boolean equals(Object o){
		if(o == null) {
			return false;
		}
		else if(o.getClass().equals(this.getClass())){
			AbstractSparqlQuery q2 = (AbstractSparqlQuery) o;
			if(q2.str.equals(this.str) && (q2.sameAs == this.sameAs) && (q2.includeInferred == this.includeInferred) && (q2.limit == this.limit) && (q2.offset == this.offset)){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public void close(){
		try {
			connection.close();
		} catch (RepositoryException e) {
			logger.warn(e.getMessage(), e);
		}
	}
}
