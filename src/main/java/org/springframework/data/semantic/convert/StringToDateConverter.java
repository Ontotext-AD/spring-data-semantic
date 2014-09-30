package org.springframework.data.semantic.convert;

import java.util.Date;

import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

public class StringToDateConverter implements Converter<String, Date>{
	
	@Override
	public Date convert(String source) {
		if(!StringUtils.isEmpty(source)){
			return XMLDatatypeUtil.parseCalendar(source).toGregorianCalendar().getTime();
		}
		else {
			return null;
		}
	}

}
