package org.springframework.data.semantic.support;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.semantic.core.SemanticExceptionTranslator;
import org.springframework.data.semantic.support.exceptions.UncategorizedSemanticDataAccessException;

public class ExceptionTranslator {

	private static final PersistenceExceptionTranslator EXCEPTION_TRANSLATOR = new SemanticExceptionTranslator();
	
	public static DataAccessException translateExceptionIfPossible(Exception ex) {
        if (ex instanceof RuntimeException) {
            return EXCEPTION_TRANSLATOR.translateExceptionIfPossible((RuntimeException) ex);
        }
        return new UncategorizedSemanticDataAccessException(ex.getMessage(), ex);
    }

}
