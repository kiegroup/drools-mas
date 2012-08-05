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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.drools.grid.*;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.runtime.StatefulKnowledgeSession;

import org.drools.grid.service.directory.WhitePages;
import org.drools.mas.ACLMessage;
import org.drools.mas.AgentID;
import org.drools.mas.util.helper.SessionLocator;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * First implementation of a drools-based intelligent, communicative agent. The
 * agent is capable of receiving messages and reacting adequately
 *
 * Messages are based on the agent intercommunication standard
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
     * Response channel class
     */
    
    private Grid grid;
    private static Logger logger = LoggerFactory.getLogger(DroolsAgent.class);

    public DroolsAgent() {
    }

    /**
     * Main constructor
     *
     * @param id
     * @param session
     */
    public DroolsAgent(Grid grid, AgentID id, StatefulKnowledgeSession session) {
        this.grid = grid;
        this.agentId = id;
        this.mind = session;
        
    }

    /**
     * Main interface method, used to accept incoming messages. Messages are
     * simply inserted into the main session and processed there
     *
     * @param msg
     */
    public void tell( ACLMessage msg ) {
        try {
            if ( logger.isTraceEnabled() ) {
                logger.trace( " +++ Message Inside Tell -> " + msg );
            }
            MessageContentEncoder.decodeBody( msg.getBody(), msg.getEncoding() );
            this.mind.insert( msg );
            this.mind.fireAllRules();
        } catch ( Exception e ) {
            // Should not happen, but in case...
            logger.error( " FATAL -> Agent " + getAgentId() + " can not survive ", e );
            dispose();
        }
    }
    
    

    /**
     * Destructor
     */
    public void dispose() {
        //TODO : what if another agent is using this grid's nodes?
        try {
            if ( logger.isInfoEnabled() ) {
                logger.info( " >>> Disposing Agent " + agentId.getName() );
            }
            QueryResults queryResults = mind.getQueryResults( "getSessions", new Object[] {} );
            Iterator<QueryResultsRow> iterator = queryResults.iterator();
            while ( iterator.hasNext() ) {

                SessionLocator sessionLoc = (SessionLocator) iterator.next().get( "$sessionLocator" );
                if ( logger.isDebugEnabled() ) {
                    logger.debug(" ### \t Disposing Agent Session :" + sessionLoc );
                }
                GridNode node = grid.getGridNode( sessionLoc.getNodeId() );
                GridServiceDescription<GridNode> nGsd = null;
                if ( node == null ) {
                    nGsd = grid.get( WhitePages.class ).lookup( sessionLoc.getNodeId() );
                    if ( nGsd != null ) {
                    GridConnection<GridNode> conn = grid.get( ConnectionFactoryService.class ).createConnection( nGsd );
                        node = conn.connect();
                        if ( logger.isDebugEnabled() ) {
                            logger.debug(" ### \t Session " + sessionLoc + " found in " + node.getId() + "  >> local = " + node.isRemote() + " ( proxy = " + node.isRemote() + " ) " );
                        }

                        StatefulKnowledgeSession ksession = node.get( sessionLoc.getSessionId(), StatefulKnowledgeSession.class );
                        ksession.dispose();
                    }
                } else {
                    // it's a local node ( triple check! ), so we just shut the KS down. The node will be disposed later
                    if ( logger.isDebugEnabled() ) {
                        logger.debug(" ### \t Session " + sessionLoc + " found in " + node.getId() + "  >> local = " + node.isRemote() + " ( proxy = " + node.isRemote() + " ) " );
                    }

                    StatefulKnowledgeSession ksession = node.get( sessionLoc.getSessionId(), StatefulKnowledgeSession.class );
                    ksession.dispose();
                }
            }

        } finally {
            grid.dispose();
        }
    }



    public StatefulKnowledgeSession getInnerSession(String sessionId) {
        if (logger.isDebugEnabled()) {
            logger.debug("Looking for the inner session: " + sessionId);
        }
        if (sessionId == null) {
            return mind;
        } else {
            QueryResults queryResults = mind.getQueryResults("getSessionById", new Object[]{sessionId});
            //size must be 1
            if (queryResults.size() > 1) {
                throw new IllegalStateException("Duplicate sessions ids");
            }
            if (queryResults.size() == 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No Session Found for SessionId: " + sessionId);
                }
                return null;
            }
            QueryResultsRow first = queryResults.iterator().next();
            SessionLocator sessionLoc = (SessionLocator) first.get("$sessionLocator");
            if (logger.isDebugEnabled()) {
                logger.debug("Session Locator Found: " + sessionLoc);
            }
            GridNode node = grid.getGridNode(sessionLoc.getNodeId());
            StatefulKnowledgeSession ksession = node.get(sessionId, StatefulKnowledgeSession.class);
            if (logger.isDebugEnabled()) {
                logger.debug("Session Located: " + ksession);
            }
            return ksession;
        }

    }

    public AgentID getAgentId() {
        return agentId;
    }

    public StatefulKnowledgeSession getMind() {
        return mind;
    }


    public List<ACLMessage> getAgentAnswers( String msgId ) {
        return extractAgentAnswers( msgId );
    }


    public List<ACLMessage> extractAgentAnswers( String msgId ) {

        QueryResults results = mind.getQueryResults( "getAnswers", new Object[] { msgId, Variable.v, Variable.v } );
        Iterator<QueryResultsRow> iterator = results.iterator();

        if ( iterator.hasNext() ) {
            QueryResultsRow row = iterator.next();

            List holders = (List) row.get( "$refList" );
            for ( Object holder : holders ) {
                mind.retract( mind.getFactHandle( holder ) );
            }

            List<ACLMessage> answers = (List<ACLMessage>) row.get( "$list" );
            return ( answers );
        } else {
            return Collections.emptyList();
        }

    }


    public List<ACLMessage> peekAgentAnswers( String msgId ) {

        QueryResults results = mind.getQueryResults( "getAnswers", new Object[] { msgId, Variable.v, Variable.v } );
        Iterator<QueryResultsRow> iterator = results.iterator();

        if ( iterator.hasNext() ) {
            QueryResultsRow row = iterator.next();
            List<ACLMessage> answers = (List<ACLMessage>) row.get( "$list" );
            return ( answers );
        } else {
            return Collections.emptyList();
        }

    }
}
