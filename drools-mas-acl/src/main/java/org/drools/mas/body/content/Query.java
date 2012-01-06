/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mas.body.content;






import java.util.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.drools.mas.mappers.MyMapReferenceEntryType;

@XmlType(name = "Query", namespace="http://content.body.mas.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class Query extends AbstractMessageContent  {
    @XmlElement(required = true)
    private String queryName;
    

    @XmlElement(required = true)
    public List<MyMapReferenceEntryType> references = new ArrayList<MyMapReferenceEntryType>(); 
    
    @XmlAnyElement()
    private List<Object> args = new ArrayList<Object>();

    public Query() {
    }

    

    @Override
    public String toString() {
        return "Query{" +
                "queryName='" + queryName + '\'' +
                ", args=" + (args == null ? null : Arrays.asList(args)) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        if (args != null ? !args.equals(query.args) : query.args != null) return false;
        if (queryName != null ? !queryName.equals(query.queryName) : query.queryName != null) return false;
        if (references != null ? !references.equals(query.references) : query.references != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = queryName != null ? queryName.hashCode() : 0;
        result = 31 * result + (references != null ? references.hashCode() : 0);
        result = 31 * result + (args != null ? args.hashCode() : 0);
        return result;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public List<Object> getArgs() {
        return args;
    }

    public void setArgs(List<Object> args) {
        this.args = args;
    }

    public List<MyMapReferenceEntryType> getReferences() {
        return references;
    }

    public void setReferences(List<MyMapReferenceEntryType> references) {
        this.references = references;
    }




}
