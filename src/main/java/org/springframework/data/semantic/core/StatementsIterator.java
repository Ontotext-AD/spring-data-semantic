package org.springframework.data.semantic.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;

/**
 * A final class to hold {@link Statement}s from both retrieval results and DAO conversions.
 * The class is declared final so that it cannot be extended, closing a security loop for the constructor that throws an exception.
 * @author konstantin.pentchev
 *
 */
public final class StatementsIterator implements Iterable<Statement>{
	
	private Iterable<Statement> delegate;
	
	public StatementsIterator(Iterable<? extends Statement> statements){
		List<Statement> collector = new LinkedList<Statement>();
		Iterator<? extends Statement> it = statements.iterator();
		while(it.hasNext()){
			collector.add(it.next());
		}
		this.delegate = collector;
	}
	
	public StatementsIterator(GraphQueryResult statements) throws QueryEvaluationException  {
		List<Statement> collector = new LinkedList<Statement>();
		try {
			while(statements.hasNext()){
				collector.add(statements.next());
			}
			delegate = collector;
		} finally {
			statements.close();
		}
	}

	@Override
	public Iterator<Statement> iterator() {
		return delegate.iterator();
	}
	
	public boolean hasNext(){
		return delegate.iterator().hasNext();
	}

}
