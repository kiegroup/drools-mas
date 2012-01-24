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

import java.io.Serializable;
import javax.xml.bind.annotation.*;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.body.content.*;
import org.drools.mas.mappers.MyMapArgsEntryType;
import org.drools.mas.mappers.MyMapReferenceEntryType;

@XmlSeeAlso(value={ Inform.class, QueryIf.class, InformIf.class, 
                    Agree.class, Failure.class, Action.class, Rule.class, 
                    QueryRef.class, Query.class, InformRef.class, Info.class,
                    Act.class, Encodings.class,
                    Ref.class, InformRef.class, Request.class, RequestWhen.class,
                    MyMapReferenceEntryType.class, MyMapArgsEntryType.class})
@XmlType(name = "AbstractMessageBody", namespace = "http://acts.body.mas.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractMessageBody implements Serializable {
    @XmlElement(required = true)
    public Act performative;
    
    public AbstractMessageBody() {
    }

    public Act getPerformative() {
        return performative;
    }

    public void setPerformative(Act performative) {
        this.performative = performative;
    }
    
    
    
}
