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
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Info;

@XmlType(name = "Reject", namespace = "http://acts.body.mas.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class Reject extends AbstractMessageBody {

    private Action call;
    private Action proposal;
    private Info cause;

    public Reject() {
    }

    public Reject(Action call, Action proposal, Info cause) {
        this.call = call;
        this.proposal = proposal;
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "Reject{"
                + "call=" + call
                + ", proposal=" + proposal
                + ", cause=" + cause
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

        Reject reject = (Reject) o;

        if (call != null ? !call.equals(reject.call) : reject.call != null) {
            return false;
        }
        if (cause != null ? !cause.equals(reject.cause) : reject.cause != null) {
            return false;
        }
        if (proposal != null ? !proposal.equals(reject.proposal) : reject.proposal != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = call != null ? call.hashCode() : 0;
        result = 31 * result + (proposal != null ? proposal.hashCode() : 0);
        result = 31 * result + (cause != null ? cause.hashCode() : 0);
        return result;
    }

    public Action getCall() {
        return call;
    }

    public void setCall(Action call) {
        this.call = call;
    }

    public Action getProposal() {
        return proposal;
    }

    public void setProposal(Action proposal) {
        this.proposal = proposal;
    }

    public Info getCause() {
        return cause;
    }

    public void setCause(Info cause) {
        this.cause = cause;
    }
    
}
