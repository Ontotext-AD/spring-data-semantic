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
package org.springframework.data.semantic.filter;

import org.openrdf.model.Literal;
import org.springframework.data.semantic.mapping.SemanticPersistentProperty;

/**
 * Created by itrend on 4/27/15.
 */
public final class ValueFilters {

	public static ValueFilter any(ValueFilter... filters) {
		return new AnyValueFilter(filters);
	}


	public static ValueFilter all(ValueFilter... filters) {
		return new AllValueFilter(filters);
	}


	public static ValueFilter hasLang(String lang) {
		return new LangFilter(lang);
	}


	private ValueFilters() {
	}

	private static abstract class JoinableMultipleValueFilter implements ValueFilter {
		private final ValueFilter[] filters;
		private final String separator;

		protected JoinableMultipleValueFilter(ValueFilter[] filters, String separator) {
			this.filters = filters;
			this.separator = separator;
		}

		@Override public String toString(SemanticPersistentProperty property) {
			if (filters.length == 0) {
				return null;
			} else if (filters.length == 1) {
				return filters[0].toString(property);
			}
			StringBuilder acc = new StringBuilder("(");
			for (ValueFilter filter : filters) {
				String strFilter = filter.toString(property);
				if (strFilter != null) {
					acc.append(strFilter);
					acc.append(separator);
				}
			}
			if (acc.length() > 1) {
				// something was added - remove trailing separator
				acc.setLength(acc.length() - separator.length());
			} else {
				// nothing was added, return null to indicate no filter
				return null;
			}
			acc.append(")");
			return acc.toString();
		}
	}

	private static final class AnyValueFilter extends JoinableMultipleValueFilter {
		protected AnyValueFilter(ValueFilter[] filters) {
			super(filters, " || ");
		}
	}


	private static final class AllValueFilter extends JoinableMultipleValueFilter {
		protected AllValueFilter(ValueFilter[] filters) {
			super(filters, " && ");
		}
	}

	private static class LangFilter implements ValueFilter {
		private final String lang;

		public LangFilter(String lang) {
			this.lang = lang;
		}

		@Override public String toString(SemanticPersistentProperty property) {
			Class<?> ptype = property.isCollectionLike() ? property.getComponentType() : property.getType();
			if (CharSequence.class.isAssignableFrom(ptype)
					|| Literal.class.isAssignableFrom(ptype)) {
				// we are expecting String or a Literal - the filter is applicable
				return "lang(?" + property.getBindingName() + ") = \"" + lang + "\"";
			}
			return null;
		}
	}
}
