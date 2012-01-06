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

import org.drools.mas.ACLMessage;
import org.drools.mas.AgentID;
import org.drools.mas.body.content.Rule;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.drools.mas.Act;

@XmlType(name = "Propagate", namespace = "http://acts.body.mas.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class Propagate extends AbstractMessageBody {

   
    private AgentID[] targets;
    private ACLMessage message;
    private Rule condition;
    
    public Propagate(AgentID[] targets, ACLMessage message, Rule condition) {
        this.targets = targets;
        this.message = message;
        this.condition = condition;
    }

    public Propagate() {
    }

   
    
    
    @Override
    public String toString() {
        return "Propagate{"
                + "targets=" + (targets == null ? null : Arrays.asList(targets))
                + ", message=" + message
                + ", condition=" + condition
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

        Propagate propagate = (Propagate) o;

        if (condition != null ? !condition.equals(propagate.condition) : propagate.condition != null) {
            return false;
        }
        if (message != null ? !message.equals(propagate.message) : propagate.message != null) {
            return false;
        }
        if (!Arrays.equals(targets, propagate.targets)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = targets != null ? Arrays.hashCode(targets) : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        return result;
    }

    public AgentID[] getTargets() {
        return targets;
    }

    public void setTargets(AgentID[] targets) {
        this.targets = targets;
    }

    public ACLMessage getMessage() {
        return message;
    }

    public void setMessage(ACLMessage message) {
        this.message = message;
    }

    public Rule getCondition() {
        return condition;
    }

    public void setCondition(Rule condition) {
        this.condition = condition;
    }

    public Object[] getArguments() {
        return new Object[]{targets, message, condition.getDrl()};
    }    
}
