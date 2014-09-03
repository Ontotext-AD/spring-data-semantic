package org.springframework.data.semantic.core;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.semantic.support.exceptions.SemanticDatabaseAccessException;
import org.springframework.data.semantic.support.exceptions.UncategorizedSemanticDataAccessException;

/**
 * 
 * @author konstantin.pentchev
 *
 */
public class SemanticExceptionTranslator implements PersistenceExceptionTranslator{

	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		if(ex instanceof SemanticDatabaseAccessException){
			return new DataAccessResourceFailureException(ex.getMessage(), ex);
		}
		else if(ex instanceof InvalidDataAccessApiUsageException){
			return (InvalidDataAccessApiUsageException) ex;
		}
		else if(ex.getCause() instanceof RepositoryException){
			RepositoryException e = (RepositoryException) ex.getCause();
			return new DataAccessResourceFailureException(e.getMessage(), e);
		}
		else if(ex.getCause() instanceof QueryEvaluationException || ex.getCause() instanceof MalformedQueryException){
			Throwable e = ex.getCause();
			return new InvalidDataAccessApiUsageException(e.getMessage(), e);
		}
		return new UncategorizedSemanticDataAccessException(ex.getMessage(), ex);
	}

}
