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

package org.drools.fipa.core;

import org.drools.fipa.util.MessageContentEncoder;
import org.drools.runtime.StatefulKnowledgeSession;

import java.util.Map;
import org.drools.fipa.ACLMessage;
import org.drools.fipa.AgentID;

/**
 * First implementation of a drools-based intelligent, communicative agent. The
 * agent is capable of receiving messages and reacting adequately
 *
 * Messages are based on the FIPA agent intercommunication standard
 *
 * TODO: agent needs to be interfaced with more standard communication channels
 */
public class DroolsAgent {

    /**
     * Agent Identifier
     */
    private AgentID agentId;
    /**
     * Main Agent Knowledge Session Message management (interpretation, routing)
     * and response management will take place in this session
     */
    private StatefulKnowledgeSession mind;
    /**
     * Response channel
     */
    private DroolsAgentResponseInformer responseInformer;

    public DroolsAgent() {
    }

    
    /**
     * Main constructor
     *
     * @param id
     * @param session
     * @param responseInformer
     */
    public DroolsAgent(AgentID id, StatefulKnowledgeSession session, DroolsAgentResponseInformer responseInformer) {
        this.agentId = id;
        this.mind = session;
        this.responseInformer = responseInformer;
    }

    /**
     * Main interface method, used to accept incoming messages.
     * Messages are simply inserted into the main session and processed there
     * @param msg
     */
    public void tell(ACLMessage msg) {

        MessageContentEncoder.decodeBody(msg.getBody(), msg.getEncoding());
        this.mind.insert(msg);
        this.mind.fireAllRules();
    }

    /**
     * Destructor
     */
    public void dispose() {
        Map<String, StatefulKnowledgeSession> proxies = (Map<String, StatefulKnowledgeSession>) mind.getGlobal("proxies");
        if (proxies != null) {
            for (String sid : proxies.keySet()) {
                StatefulKnowledgeSession subSession = proxies.get(sid);
                if (subSession != null) {
                    subSession.dispose();
                }
            }
        }
        mind.dispose();
    }

    public StatefulKnowledgeSession getInnerSession(String sessionId) {
        if (sessionId == null) {
            return mind;
        } else {
            Map<String, StatefulKnowledgeSession> proxies = (Map<String, StatefulKnowledgeSession>) mind.getGlobal("proxies");
            return proxies.get(sessionId);
        }

    }

    public AgentID getAgentId() {
        return agentId;
    }

    public StatefulKnowledgeSession getMind() {
        return mind;
    }

    public DroolsAgentResponseInformer getResponseInformer() {
        return responseInformer;
    }
}
