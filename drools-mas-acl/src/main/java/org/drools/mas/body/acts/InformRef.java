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

package org.drools.mas.body.acts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.body.content.Ref;

@XmlType(name = "InformRef", namespace = "http://acts.body.mas.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class InformRef extends AbstractMessageBody {

    
    @XmlElement(required = true)
    private Ref references;
    
    public InformRef() {
    }

    @Override
    public String toString() {
        return "InformRef{"
                + "references=" + references
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InformRef informRef = (InformRef) o;

        if (references != null ? !references.equals(informRef.references) : informRef.references != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return references != null ? references.hashCode() : 0;
    }

    public Ref getReferences() {
        return references;
    }

    public void setReferences(Ref references) {
        this.references = references;
    }

}
