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

/*
 * The Info class is in charge of hosting a piece of data that. Here we are using info as a synonim of Proposition.
 * @TODO: we should rename this class to Proposition and add the truth attr (boolean)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Info", namespace="http://content.body.mas.drools.org/")
public class Info extends AbstractMessageContent  {
    @XmlElement()
    private Object data;
    //@TODO: we should add here a truth value.. like a negation boolean to 
    // explicitely say if the data is a true or false propositon
    public Info() {
    }

    @Override
    public String toString() {
        return "Info{" +
                "data=" + data +
                ", encoded=" + getEncodedContent() +
                '}';
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    } 

}
