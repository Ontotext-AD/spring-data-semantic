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
package org.springframework.data.semantic.model;

import org.openrdf.model.URI;
import org.springframework.data.semantic.annotation.ResourceId;
import org.springframework.data.semantic.annotation.SemanticEntity;

import javax.xml.datatype.XMLGregorianCalendar;

@SemanticEntity
public class XMLGregorianCalendarEntity {

    @ResourceId
    URI id;

    XMLGregorianCalendar date;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public XMLGregorianCalendar getDate() {
        return date;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.date = date;
    }
}
