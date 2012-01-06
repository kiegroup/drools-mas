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

package org.drools.mas.core;

import org.drools.mas.util.ACLMessageFactory;
import org.drools.runtime.StatefulKnowledgeSession;



import java.util.HashMap;
import java.util.Map;
import org.drools.mas.AgentID;
import org.drools.mas.Encodings;


/**
 * Factory class.
 * Creates Drools Agents out of a configuration object
 * defining how many Knowledge Sessions, deployed on grid, will be available to a new agent itself.
 * The sessions are built using the resources specified in the configuration file
 *
 *
 * TODO at the moment, a single default grid node is created locally.
 * TODO the topology is static, i.e. one master session with unidrectional links to "slave" sessions
 * TODO --> full GRID capabilities must be exploited to manage sessions dynamically
 */
public class DroolsAgentFactory {

    private static DroolsAgentFactory singleton;


    public static DroolsAgentFactory getInstance() {
        if (singleton == null) {
            singleton = new DroolsAgentFactory();
        }
        return singleton;
    }
 

    private DroolsAgentFactory() {

    }


    public DroolsAgent spawn(DroolsAgentConfiguration config) {
        AgentID aid = new AgentID();
        aid.setName(config.getAgentId());
        aid.setLocalName(config.getAgentId());

        try {
            ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
            StatefulKnowledgeSession mind = SessionManager.create(config.getAgentId(),config.getChangeset()).getStatefulKnowledgeSession();

            Map<String,StatefulKnowledgeSession> proxies = new HashMap<String,StatefulKnowledgeSession>();
                mind.setGlobal("aclFactory", factory);
                mind.setGlobal("agentName", aid);
                mind.setGlobal("proxies", proxies);
                mind.setGlobal("defaultCS", config.getDefaultSubsessionChangeSet() );
                mind.setGlobal("responseInformer", config.getResponseInformer());

            for (DroolsAgentConfiguration.SubSessionDescriptor descr : config.getSubSessions()) {
                  SessionManager sm = SessionManager.create(descr.getSessionId(),descr.getChangeset());
                  StatefulKnowledgeSession mindSet = sm.getStatefulKnowledgeSession();
                mindSet.fireAllRules();
                proxies.put(descr.getSessionId(),mindSet);
            }

            mind.fireAllRules();

            DroolsAgent agent = new DroolsAgent(aid, mind, config.getResponseInformer());
            return agent;
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return null;

    }








}