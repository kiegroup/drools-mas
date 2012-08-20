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

package org.drools.mas.util;

import org.drools.mas.body.acts.*;
import org.drools.mas.body.content.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.drools.mas.ACLMessage;
import org.drools.mas.Act;
import org.drools.mas.AgentID;
import org.drools.mas.Encodings;

/**

ACL Message factory
Build messages corresponding to any standard communication act, as defined by the FIPA standard.

accept-proposal( Action action, Rule condition )
agree( Action action, Rule condition )
cancel( Action action )
call-for-proposal( Action action, Rule precondition )
confirm( Object proposition )
disconfirm( Object proposition )
failure( Action action, Object cause )
inform( Object proposition )
inform-if( Object proposition )
inform-ref( Map references )
not-understood( Object act, Object cause )
propagate( Identity[] targets, ACLMesasge message, Rule condition )
propose( Action act, Rule precondition )
proxy( Identity[] targets, ACLMesasge message, Rule condition )
query-if( Object proposition )
query-ref( Query query )
refuse( Action act, Object cause )
reject-proposal( Action call, Action proposal, Object cause )
request( Action action )
request-when( Action action, Rule condition )
request-whenever( Action action, Rule condition )
subscribe( Query query )


Message content is encoded in string format, either XML or JSON
 */
public class ACLMessageFactory implements Serializable {

    private static AtomicLong idCounter = new AtomicLong();
    private static AtomicLong convCounter  = new AtomicLong();
    
    private static ACLMessageFactory instance;
    
    public static ACLMessageFactory getInstance(){
        if(instance == null){
            instance = new ACLMessageFactory(Encodings.XML);
        }
        return instance;
    }
    
    private long newId() {
        return idCounter.incrementAndGet();
    }

    private long newConversationId() {
        return convCounter.incrementAndGet();
    }
    private Encodings defaultEncoding = Encodings.XML;

    public Encodings getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(Encodings defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public ACLMessageFactory(Encodings defEncoding) {
        this.setDefaultEncoding(defEncoding);
    }

    public ACLMessage newMessage() {
        return new ACLMessage( UUID.randomUUID().toString()+"-"+newId() );
    }

    protected ACLMessage newMessage( String sender, String receiver ) {

        ACLMessage msg = new ACLMessage();

        AgentID senderAgent = new AgentID();
        senderAgent.setName( sender );
        msg.setSender( senderAgent );

        msg.setConversationId( senderAgent.toString() +"-"+ newConversationId());
        
        msg.setId( senderAgent.toString() +"-"+ newId());

        List<AgentID> recSet = msg.getReceiver();
        AgentID receiverAgent = new AgentID();
        receiverAgent.setName( receiver );
        recSet.add( receiverAgent );

        msg.setEncoding( getDefaultEncoding() );

        return msg;
    }

    protected ACLMessage createReply(ACLMessage inMsg, AgentID sender) {

        ACLMessage msg = newMessage();
        msg.setEncoding(inMsg.getEncoding());
        msg.setSender(sender);

        List<AgentID> recSet = new ArrayList<AgentID>();
        recSet.add(inMsg.getSender());
        msg.setReceiver(recSet);

        msg.setConversationId(inMsg.getConversationId());
        msg.setInReplyTo(inMsg.getId());

        return msg;
    }

    private boolean setMessageBody(ACLMessage msg, AbstractMessageBody body) {

        msg.setPerformative(body.getPerformative());
        if (getDefaultEncoding() != Encodings.NONE) {
            MessageContentEncoder.encodeBody(body, getDefaultEncoding());
        }
        msg.setBody(body);
        
        return true;
    }

    public ACLMessage newAcceptProposalMessage(String sender, String receiver, Action action, Rule condition) {
        ACLMessage msg = newMessage(sender, receiver);
        AcceptProposal body = new AcceptProposal();
        body.setPerformative(Act.ACCEPT);
        action.setMsgId(msg.getId());
        condition.setMsgId(msg.getId());
        body.setAction(action);
        body.setCondition(condition);
        
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newAgreeMessage(String sender, String receiver, Action action, Rule condition) {
        ACLMessage msg = newMessage(sender, receiver);
        Agree body = new Agree();
        body.setPerformative(Act.AGREE);
        action.setMsgId(msg.getId());
        condition.setMsgId(msg.getId());
        body.setAction(action);
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newCancelMessage(String sender, String receiver, Action action) {
        ACLMessage msg = newMessage(sender, receiver);
        Cancel body = new Cancel();
        body.setPerformative(Act.CANCEL);
        action.setMsgId(msg.getId());
        body.setAction(action);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newCallForProposalMessage(String sender, String receiver, Action action, Rule condition) {
        ACLMessage msg = newMessage(sender, receiver);
        CallForProposal body = new CallForProposal();
        body.setPerformative(Act.CALL_FOR_PROPOSAL);
        action.setMsgId(msg.getId());
        condition.setMsgId(msg.getId());
        body.setAction(action);
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newConfirmMessage(String sender, String receiver, Object proposition) {
        ACLMessage msg = newMessage(sender, receiver);
        Confirm body = new Confirm();
        body.setPerformative(Act.CONFIRM);
        Info info = new Info();
        info.setData(proposition);
        info.setMsgId(msg.getId());
        body.setProposition(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newDisconfirmMessage(String sender, String receiver, Object proposition) {
        ACLMessage msg = newMessage(sender, receiver);
        Disconfirm body = new Disconfirm();
        body.setPerformative(Act.DISCONFIRM);
        Info info = new Info();
        info.setData(proposition);
        info.setMsgId(msg.getId());
        body.setProposition(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newFailureMessage(String sender, String receiver, Action action, Object cause) {
        ACLMessage msg = newMessage(sender, receiver);
        Failure body = new Failure();
        body.setPerformative(Act.FAILURE);
        body.setAction(action);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(cause);
        body.setAction(action);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newInformMessage(String sender, String receiver, Object proposition) {
        ACLMessage msg = newMessage(sender, receiver);
        Inform body = new Inform();
        body.setPerformative(Act.INFORM);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(proposition);
        body.setProposition(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newInformIfMessage(String sender, String receiver, Object proposition) {
        ACLMessage msg = newMessage(sender, receiver);
        InformIf body = new InformIf();
        body.setPerformative(Act.INFORM_IF);
        Info info = new Info();
        info.setData(proposition);
        info.setMsgId(msg.getId());
        body.setProposition(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newInformRefMessage(String sender, String receiver, Ref references) {
        ACLMessage msg = newMessage(sender, receiver);
        InformRef body = new InformRef();
        body.setPerformative(Act.INFORM_REF);
        body.setReferences(references);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newNotUnderstoodMessage(String sender, String receiver, Action action, Object cause) {
        ACLMessage msg = newMessage(sender, receiver);
        NotUnderstood body = new NotUnderstood();
        body.setPerformative(Act.NOT_UNDERSTOOD);
        body.setAction(action);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(cause);
        body.setCause(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newPropagateMessage(String sender, String receiver, AgentID[] targets, ACLMessage innerMsg, Rule condition) {
        ACLMessage msg = newMessage(sender, receiver);
        Propagate body = new Propagate();
        body.setPerformative(Act.PROPAGATE);
        body.setTargets(targets);
        body.setMessage(msg);
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newProposeMessage(String sender, String receiver, Action action, Rule precondition) {
        ACLMessage msg = newMessage(sender, receiver);
        Propose body = new Propose();
        body.setPerformative(Act.PROPOSE);
        action.setMsgId(msg.getId());
        body.setAction(action);
        body.setCondition(precondition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newProxyMessage(String sender, String receiver, AgentID[] targets, ACLMessage innerMsg, Rule condition) {
        ACLMessage msg = newMessage(sender, receiver);
        Proxy body = new Proxy();
        body.setPerformative(Act.PROXY);
        body.setTargets(targets);
        body.setMessage(innerMsg);
        condition.setMsgId(msg.getId());
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newQueryIfMessage(String sender, String receiver, Object proposition) {
        ACLMessage msg = newMessage(sender, receiver);
        QueryIf body = new QueryIf();
        body.setPerformative(Act.QUERY_IF);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(proposition);
        body.setProposition(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newQueryRefMessage(String sender, String receiver, Query query) {
        ACLMessage msg = newMessage(sender, receiver);
        QueryRef body = new QueryRef();
        body.setPerformative(Act.QUERY_REF);
        query.setMsgId(msg.getId());
        body.setQuery(query);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newRefuseMessage(String sender, String receiver, Action action, Object cause) {
        ACLMessage msg = newMessage(sender, receiver);
        Refuse body = new Refuse();
        body.setPerformative(Act.REFUSE);
        action.setMsgId(msg.getId());
        body.setAction(action);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(cause);
        body.setCause(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newRejectProposalMessage(String sender, String receiver, Action call, Action proposal, Object cause) {
        ACLMessage msg = newMessage(sender, receiver);
        Reject body = new Reject();
        body.setPerformative(Act.REJECT);
        call.setMsgId(msg.getId());
        body.setCall(call);
        proposal.setMsgId(msg.getId());
        body.setProposal(proposal);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(cause);
        body.setCause(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newRequestMessage(String sender, String receiver, Action action) {
        ACLMessage msg = newMessage(sender, receiver);
        Request body = new Request();
        body.setPerformative(Act.REQUEST);
        action.setMsgId(msg.getId());
        body.setAction(action);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newRequestWhenMessage(String sender, String receiver, Action action, Rule condition) {
        ACLMessage msg = newMessage(sender, receiver);
        RequestWhen body = new RequestWhen();
        body.setPerformative(Act.REQUEST_WHEN);
        action.setMsgId(msg.getId());
        body.setAction(action);
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newRequestWheneverMessage(String sender, String receiver, Action action, Rule condition) {
        ACLMessage msg = newMessage(sender, receiver);
        RequestWhenever body = new RequestWhenever();
        body.setPerformative(Act.REQUEST_WHENEVER);
        action.setMsgId(msg.getId());
        condition.setMsgId(msg.getId());
        body.setAction(action);
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newSubscribeMessage(String sender, String receiver, Query query) {
        ACLMessage msg = newMessage(sender, receiver);
        Subscribe body = new Subscribe();
        body.setPerformative(Act.SUBSCRIBE);
        query.setMsgId(msg.getId());
        body.setQuery(query);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithAcceptProposalMessage(ACLMessage origin, AgentID sender, Action action, Rule condition) {
        ACLMessage msg = createReply(origin, sender);
        AcceptProposal body = new AcceptProposal();
        body.setPerformative(Act.ACCEPT);
        action.setMsgId(msg.getId());
        body.setAction(action);
        condition.setMsgId(msg.getId());
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithAgreeMessage(ACLMessage origin, AgentID sender, Action action, Rule condition) {
        ACLMessage msg = createReply(origin, sender);
        Agree body = new Agree();
        body.setPerformative(Act.AGREE);
        action.setMsgId(msg.getId());
        body.setAction(action);
        condition.setMsgId(msg.getId());
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithCancelMessage(ACLMessage origin, AgentID sender, Action action) {
        ACLMessage msg = createReply(origin, sender);
        Cancel body = new Cancel();
        body.setPerformative(Act.CANCEL);
        action.setMsgId(msg.getId());
        body.setAction(action);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithCallForProposalMessage(ACLMessage origin, AgentID sender, Action action, Rule condition) {
        ACLMessage msg = createReply(origin, sender);
        action.setMsgId(msg.getId());
        condition.setMsgId(msg.getId());
        CallForProposal body = new CallForProposal(action, condition);
        body.setPerformative(Act.CALL_FOR_PROPOSAL);
        body.setAction(action);
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithConfirmMessage(ACLMessage origin, AgentID sender, Object proposition) {
        ACLMessage msg = createReply(origin, sender);
        Confirm body = new Confirm();
        body.setPerformative(Act.CONFIRM);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(proposition);
        body.setProposition(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithDisconfirmMessage(ACLMessage origin, AgentID sender, Object proposition) {
        ACLMessage msg = createReply(origin, sender);
        Disconfirm body = new Disconfirm();
        body.setPerformative(Act.DISCONFIRM);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(proposition);
        body.setProposition(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithFailureMessage(ACLMessage origin, AgentID sender, Action action, Object cause) {
        ACLMessage msg = createReply(origin, sender);
        Failure body = new Failure( );
        body.setPerformative(Act.FAILURE);
        body.setAction(action);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(cause);
        body.setCause(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithInformMessage(ACLMessage origin, AgentID sender, Object proposition) {
        ACLMessage msg = createReply(origin, sender);
        Inform body = new Inform();
        body.setPerformative(Act.INFORM);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(proposition);
        body.setProposition(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithInformIfMessage(ACLMessage origin, AgentID sender, Object proposition) {
        ACLMessage msg = createReply(origin, sender);
        InformIf body = new InformIf();
        body.setPerformative(Act.INFORM_IF);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(proposition);
        body.setProposition(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithInformRefMessage(ACLMessage origin, AgentID sender, Ref references) {
        ACLMessage msg = createReply(origin, sender);
        InformRef body = new InformRef();
        body.setPerformative(Act.INFORM_REF);
        references.setMsgId(msg.getId());
        body.setReferences(references);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithNotUnderstoodMessage(ACLMessage origin, AgentID sender, Action action, Object cause) {
        ACLMessage msg = createReply(origin, sender);
        NotUnderstood body = new NotUnderstood();
        body.setPerformative(Act.NOT_UNDERSTOOD);
        body.setAction(action);
        Info info = new Info();
        info.setData(cause);
        info.setMsgId(msg.getId());
        body.setCause(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithPropagateMessage(ACLMessage origin, AgentID sender, AgentID[] targets, ACLMessage innerMsg, Rule condition) {
        ACLMessage msg = createReply(origin, sender);
        Propagate body = new Propagate();
        body.setPerformative(Act.PROPAGATE);
        body.setTargets(targets);
        body.setMessage(innerMsg);
        condition.setMsgId(msg.getId());
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithProposeMessage(ACLMessage origin, AgentID sender, Action action, Rule precondition) {
        ACLMessage msg = createReply(origin, sender);
        Propose body = new Propose();
        body.setPerformative(Act.PROPOSE);
        action.setMsgId(msg.getId());
        body.setAction(action);
        precondition.setMsgId(msg.getId());
        body.setCondition(precondition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithProxyMessage(ACLMessage origin, AgentID sender, AgentID[] targets, ACLMessage innerMsg, Rule condition) {
        ACLMessage msg = createReply(origin, sender);
        Proxy body = new Proxy();
        body.setPerformative(Act.PROXY);
        body.setTargets(targets);
        body.setMessage(innerMsg);
        condition.setMsgId(msg.getId());
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithQueryIfMessage(ACLMessage origin, AgentID sender, Object proposition) {
        ACLMessage msg = createReply(origin, sender);
        QueryIf body = new QueryIf();
        body.setPerformative(Act.QUERY_IF);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(proposition);
        body.setProposition(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithQueryRefMessage(ACLMessage origin, AgentID sender, Query query) {
        ACLMessage msg = createReply(origin, sender);
        QueryRef body = new QueryRef();
        body.setPerformative(Act.QUERY_IF);
        query.setMsgId(msg.getId());
        body.setQuery(query);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithRefuseMessage(ACLMessage origin, AgentID sender, Action action, Object cause) {
        ACLMessage msg = createReply(origin, sender);
        Refuse body = new Refuse();
        body.setPerformative(Act.REFUSE);
        body.setAction(action);
        Info info = new Info();
        info.setMsgId(msg.getId());
        info.setData(cause);
        body.setCause(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithRejectProposalMessage(ACLMessage origin, AgentID sender, Action call, Action proposal, Object cause) {
        ACLMessage msg = createReply(origin, sender);
        Reject body = new Reject();
        body.setPerformative(Act.REJECT);
        body.setCall(call);
        body.setProposal(proposal);
        Info info = new Info();
        info.setData(cause);
        body.setCause(info);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithRequestMessage(ACLMessage origin, AgentID sender, Action action) {
        ACLMessage msg = createReply(origin, sender);
        Request body = new Request();
        body.setPerformative(Act.REQUEST);
        action.setMsgId(msg.getId());
        body.setAction(action);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithRequestWhenMessage(ACLMessage origin, AgentID sender, Action action, Rule condition) {
        ACLMessage msg = createReply(origin, sender);
        RequestWhen body = new RequestWhen(action, condition);
        body.setPerformative(Act.REQUEST_WHEN);
        action.setMsgId(msg.getId());
        body.setAction(action);
        condition.setMsgId(msg.getId());
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithRequestWheneverMessage(ACLMessage origin, AgentID sender, Action action, Rule condition) {
        ACLMessage msg = createReply(origin, sender);
        RequestWhenever body = new RequestWhenever();
        body.setPerformative(Act.REQUEST_WHENEVER);
        action.setMsgId(msg.getId());
        body.setAction(action);
        condition.setMsgId(msg.getId());
        body.setCondition(condition);
        setMessageBody(msg, body);
        return msg;
    }

    public ACLMessage newReplyWithSubscribeMessage(ACLMessage origin, AgentID sender, Query query) {
        ACLMessage msg = createReply(origin, sender);
        Subscribe body = new Subscribe();
        body.setPerformative(Act.SUBSCRIBE);
        query.setMsgId(msg.getId());
        body.setQuery(query);
        setMessageBody(msg, body);
        return msg;
    }
}
