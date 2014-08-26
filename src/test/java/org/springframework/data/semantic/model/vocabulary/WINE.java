package org.springframework.data.semantic.model.vocabulary;

import org.openrdf.model.URI;

/**
 * Vocabulary constants for the wine ontology.
 * 
 * @author jeen
 * 
 */
public class WINE extends VOCABULARY {
	
	public static final String NAMESPACE = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";

	public static final URI WINE = create("Wine");
	public static final URI RED_WINE = create("RedWine");
	public static final URI WHITE_WINE = create("WhiteWine");
	public static final URI LIGHT = create("Light");
	public static final URI RUBIN = create("Rubin");
	
	private static URI create(String localName){
		return create(NAMESPACE, localName);
	}

}
