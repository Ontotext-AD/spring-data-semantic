package org.springframework.data.semantic.model.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Vocabulary constants for the wine ontology.
 * 
 * @author jeen
 * 
 */
public class WINE {

	public static final String NAMESPACE = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";

	public static final URI WINE = create("Wine");
	public static final URI RED_WINE = create("RedWine");
	public static final URI WHITE_WINE = create("WhiteWine");

	public static final URI LIGHT = create("Light");

	private static final URI create(String localName) {
		final ValueFactory f = ValueFactoryImpl.getInstance();
		return f.createURI(NAMESPACE, localName);
	}
}
