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

import org.drools.core.common.InternalWorkingMemory;

import java.util.Map;

import org.drools.core.management.DroolsManagementAgent;
import org.drools.mas.AgentID;
import org.drools.mas.core.helpers.SessionHelper;
import org.drools.mas.util.helper.SessionLocator;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class. Creates Drools Agents out of a configuration object defining
 * how many Knowledge Sessions, will be available to a new
 * agent itself. The sessions are built using the resources specified in the
 * configuration file
 *
 *
 */
public class DroolsAgentFactory {

    private static Logger logger = LoggerFactory.getLogger(DroolsAgentFactory.class);
    private static DroolsAgentFactory singleton;

    public static DroolsAgentFactory getInstance() {
        if (singleton == null) {
            singleton = new DroolsAgentFactory();
        }
        return singleton;
    }

    private DroolsAgentFactory() {
    }

    public DroolsAgent spawn( DroolsAgentConfiguration config ) {

        AgentID aid = new AgentID();
        aid.setName( config.getAgentId() );
        aid.setLocalName( config.getAgentId() );

        if (logger.isInfoEnabled()) {
            logger.info(" >>> Spawning Agent => Name: " + aid.getName());
        }

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("  ### Creating Agent Mind: " + config.getAgentId() + "- CS: " + config.getKieBaseId() );
            }


            SessionManager manager = SessionManagerFactory.create( config, null );
            if ( manager == null ) {
                logger.error("SOMETHING BAD HAPPENED WHILE TRYING TO CREATE AN AGENT, could not create sessionManager");
                SessionHelper.getInstance().dispose();
                return null;
            }
            KieSession mind = manager.getKieSession();

            DroolsManagementAgent kmanagement = DroolsManagementAgent.getInstance();
            kmanagement.registerKnowledgeSession( (InternalWorkingMemory) mind );

            if (logger.isDebugEnabled()) {
                logger.debug("  ### Mind Created: " + config.getAgentId() + "- CS: " + config.getKieBaseId() );
                logger.debug("  ### Creating Agent Sub-Sessions ");
            }

            for ( Map.Entry<String, Object> entry : config.getGlobals().entrySet() ) {
                mind.setGlobal( entry.getKey(), entry.getValue() );
            }

            for ( DroolsAgentConfiguration.SubSessionDescriptor descr : config.getSubSessions() ) {
                if (logger.isDebugEnabled()) {
                    logger.debug("  ### Creating Agent Sub-Session: " + descr.getSessionId() + "- CS: " + descr.getKieBaseId() );
                }
                SessionManager sm = SessionManagerFactory.create( config, descr );
                KieSession mindSet = sm.getKieSession();


                for ( Map.Entry<String, Object> entry : descr.getGlobals().entrySet() ) {
                    mindSet.setGlobal(entry.getKey(), entry.getValue());
                }

                mindSet.fireAllRules();

                mindSet.insert( new SessionLocator( config.getAgentId(), true, false ) );
                mindSet.insert( new SessionLocator( descr.getSessionId(), false, true ) );
                mind.insert( new SessionLocator( descr.getSessionId(), false, true ) );

            }

            if ( config.getSubNodes().size() > 0 ) {
                for ( String node : config.getSubNodes() ) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("  ### Creating Additional Node: " + node);
                    }
                }
            }

            mind.insert( aid );
            //Insert configuration as a fact inside the mind session
            mind.insert( config );
            mind.insert( new SessionLocator( config.getAgentId(), true, false ) );
            mind.fireAllRules();

            return new DroolsAgent( aid, mind );
        } catch (Throwable t) {
            if (t.getCause() != null) {
                logger.error("SOMETHING BAD HAPPENED WHILE TRYING TO CREATE AN AGENT " + t.getMessage() + ", due to " + t.getCause().getMessage());
            } else {
                logger.error("SOMETHING BAD HAPPENED WHILE TRYING TO CREATE AN AGENT " + t.getMessage());
            }
            if (logger.isDebugEnabled()) {
                t.printStackTrace();
            }
            SessionHelper.getInstance().dispose();
        }
        return null;

    }

}
