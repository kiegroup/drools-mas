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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Rule", namespace="http://content.body.fipa.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rule extends AbstractMessageContent {
    @XmlElement(required = true)
    private String drl;

    public Rule() {
    }

    
    
//    public Rule(String drl) {
//        this.drl = drl;
//    }

    @Override
    public String toString() {
        return "Rule{" +
                "drl='" + drl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rule rule = (Rule) o;

        if (drl != null ? !drl.equals(rule.drl) : rule.drl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return drl != null ? drl.hashCode() : 0;
    }

    public String getDrl() {
        return drl;
    }

    public void setDrl(String drl) {
        this.drl = drl;
    }

  


}
