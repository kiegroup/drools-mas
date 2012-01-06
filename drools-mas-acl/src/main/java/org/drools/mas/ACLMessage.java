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

package org.drools.mas;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;
import org.drools.mas.body.acts.*;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Info;
import org.drools.mas.body.content.Query;
import org.drools.mas.body.content.Ref;
import org.drools.mas.body.content.Rule;
import org.drools.mas.mappers.MyMapArgsEntryType;
import org.drools.mas.mappers.MyMapReferenceEntryType;



/**
 * Agent Communication Language Message, as defined by the FIPA standard
 *
 * A Message represents a communicative act (aka "performative"), chosen from a predefined standard set.
 * A performative will have a content (e.g. Info, Query), wrapping the actual arguments.
 *
 * Other than that, the message will contain sender and receiver references, in addition to
 * context and metadata information
 */

@XmlType(name = "ACLMessage", namespace="http://fipa.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(value={ Inform.class, QueryIf.class, InformIf.class, 
                    Agree.class, Failure.class, Action.class, Rule.class, 
                    QueryRef.class, Query.class, Info.class, Act.class,
                    Ref.class, InformRef.class, Request.class, RequestWhen.class, Ref.class,
                    Encodings.class,
                    MyMapReferenceEntryType.class, MyMapArgsEntryType.class})
public class ACLMessage implements Serializable {

    public static final String DEFAULT_FIPA_MESSAGE_TYPE = "DEFAULT_FIPA_MESSAGE_TYPE";
    public static final String DROOLS_DRL = "DROOLS_DRL";
    public static final String KMR2 = "KMR2";



    private String id;
    private String version;

    private String messageType = DEFAULT_FIPA_MESSAGE_TYPE;


    @XmlElement(required = true) 
    private String protocol;
    @XmlElement(required = true) 
    private String conversationId;
    @XmlElement(required = true) 
    private String replyWith;
    @XmlElement(required = true) 
    private String inReplyTo;
    @XmlElement(required = true) 
    private long replyBy;

    @XmlElement(required = true) 
    private String ontology = KMR2;
    @XmlElement(required = true) 
    private String language = DROOLS_DRL;
    @XmlElement(required = true) 
    private Encodings encoding;
    @XmlElement(required = true) 
    private AgentID sender;
    @XmlElement(required = true) 
    private List<AgentID> receiver;
    @XmlElement(required = true) 
    private List<AgentID> replyTo;
    @XmlElement(required = true) 
    private Act performative;
    @XmlElement(required=true)
    private AbstractMessageBody body;


    public ACLMessage() {

    }

    public ACLMessage(String id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ACLMessage that = (ACLMessage) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : -1;
    }


    @Override
    public String toString() {
        return "ACLMessage{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", messageType='" + messageType + '\'' +
                ", protocol='" + protocol + '\'' +
                ", conversationId=" + conversationId +
                ", replyWith='" + replyWith + '\'' +
                ", inReplyTo='" + inReplyTo + '\'' +
                ", replyBy='" + replyBy + '\'' +
                ", ontology='" + ontology + '\'' +
                ", language='" + language + '\'' +
                ", encoding='" + encoding + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", replyTo=" + replyTo +
                ", performative=" + performative +
                ", body='" + body + '\'' +
                '}';
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getReplyWith() {
        return replyWith;
    }

    public void setReplyWith(String replyWith) {
        this.replyWith = replyWith;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public long getReplyBy() {
        return replyBy;
    }

    public void setReplyBy(long replyBy) {
        this.replyBy = replyBy;
    }

    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Encodings getEncoding() {
        return encoding;
    }

    public void setEncoding(Encodings encoding) {
        this.encoding = encoding;
    }

    public AgentID getSender() {
        return sender;
    }

    public void setSender(AgentID sender) {
        this.sender = sender;
    }

    public List<AgentID> getReceiver() {
        if(receiver == null){
            receiver = new ArrayList<AgentID>();
        }
        return receiver;
    }

    public void setReceiver(List<AgentID> receiver) {
        this.receiver = receiver;
    }

    public List<AgentID> getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(List<AgentID> replyTo) {
        this.replyTo = replyTo;
    }

    public Act getPerformative() {
        return performative;
    }

    public void setPerformative(Act performative) {
        this.performative = performative;
    }


    public AbstractMessageBody getBody() {
        return body;
    }

    public void setBody(AbstractMessageBody body) {
        this.body =  body;
    }
}
