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

import org.drools.mas.util.helper.NodeLocator;
import org.drools.runtime.StatefulKnowledgeSession;



import java.util.HashMap;
import javax.persistence.Persistence;
import org.drools.SystemEventListenerFactory;
import org.drools.grid.Grid;
import org.drools.grid.GridServiceDescription;
import org.drools.grid.conf.GridPeerServiceConfiguration;
import org.drools.grid.conf.impl.GridPeerConfiguration;
import org.drools.grid.impl.GridImpl;
import org.drools.grid.impl.MultiplexSocketServerImpl;
import org.drools.grid.io.impl.MultiplexSocketServiceConfiguration;
import org.drools.grid.remote.mina.MinaAcceptorFactoryService;
import org.drools.grid.service.directory.WhitePages;
import org.drools.grid.service.directory.impl.CoreServicesLookupConfiguration;
import org.drools.grid.service.directory.impl.JpaWhitePages;
import org.drools.grid.service.directory.impl.WhitePagesLocalConfiguration;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.management.DroolsManagementAgent;
import org.drools.mas.AgentID;
import org.drools.mas.util.helper.SessionLocator;
import org.drools.runtime.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class. Creates Drools Agents out of a configuration object defining
 * how many Knowledge Sessions, deployed on grid, will be available to a new
 * agent itself. The sessions are built using the resources specified in the
 * configuration file
 *
 *
 * TODO at the moment, a single default grid node is created locally. TODO the
 * topology is static, i.e. one master session with unidrectional links to
 * "slave" sessions  --> full GRID capabilities are being used to manage
 * sessions dynamically
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
        Grid grid = new GridImpl( "peer-" + config.getAgentId(), new HashMap<String, Object>() );
        configureGrid( grid, config.getPort() );

        AgentID aid = new AgentID();
        aid.setName( config.getAgentId() );
        aid.setLocalName( config.getAgentId() );
        if ( logger.isInfoEnabled() ) {
            logger.info( " >>> Spawning Agent => Name: " + aid.getName() );
        }
        try {
            if ( logger.isDebugEnabled() ) {
                logger.debug("  ### Creating Agent Mind: " + config.getAgentId() + "- CS: " + config.getChangeset() +" - mind location: " +config.getMindNodeLocation() );
            }
            SessionManager manager = SessionManager.create( config, null, grid, false );
            if ( manager == null ) {
                logger.error( "SOMETHING BAD HAPPENED WHILE TRYING TO CREATE AN AGENT, could not create sessionManager" );
                grid.dispose();
                return null;
            }
            StatefulKnowledgeSession mind = manager.getStatefulKnowledgeSession();

            DroolsManagementAgent kmanagement = DroolsManagementAgent.getInstance();

            //kmanagement.registerKnowledgeBase((ReteooRuleBase) ((KnowledgeBaseImpl) mind.get).getRuleBase());

            kmanagement.registerKnowledgeSession( ( (StatefulKnowledgeSessionImpl) mind ).getInternalWorkingMemory() );

            if ( logger.isDebugEnabled() ) {
                logger.debug( "  ### Mind Created: " + config.getAgentId() + "- CS: " + config.getChangeset() + " in "+ config.getMindNodeLocation() );
                logger.debug( "  ### Creating Agent Sub-Sessions " );
            }

            mind.setGlobal( "grid", grid );
            mind.insert( new NodeLocator(config.getMindNodeLocation(), true ) );

            for ( DroolsAgentConfiguration.SubSessionDescriptor descr : config.getSubSessions() ) {
                if ( logger.isDebugEnabled() ) {
                    logger.debug( "  ### Creating Agent Sub-Session: " + descr.getSessionId() + "- CS: " + descr.getChangeset() + " - on node: " + descr.getNodeId() );
                }
                SessionManager sm = SessionManager.create( config, descr, grid, true );
                StatefulKnowledgeSession mindSet = sm.getStatefulKnowledgeSession();

//                try{
//                    mindSet.setGlobal( "grid", grid );
//                } catch (Exception e){
//                    //maybe 'grid' is not even defined in subsession
//                    logger.debug("Global 'grid' not set on session '"+descr.getSessionId()+"' due to "+e.getMessage());
//                }
                
                mindSet.fireAllRules();
                
                mindSet.insert( new SessionLocator( config.getMindNodeLocation(), config.getAgentId(), true, false ) );
                mindSet.insert( new SessionLocator( descr.getNodeId(), descr.getSessionId(), false, true ) );
                mind.insert( new SessionLocator( descr.getNodeId(), descr.getSessionId(), false, true ) );
                mind.insert( new NodeLocator( descr.getNodeId(), true ) );

            }

            if ( config.getSubNodes().size() > 0 ) {
                for ( String node : config.getSubNodes() ) {
                    if ( logger.isDebugEnabled() ) {
                     logger.debug( "  ### Creating Additional Node: " + node );
                    }
                    SessionManager.createNode( node, grid, config.getPort(), true );
                    mind.insert( new NodeLocator( node, false ) );
                }
            }

            mind.insert( aid );
            //Insert configuration as a fact inside the mind session
            mind.insert( config );
            mind.insert( new SessionLocator( config.getMindNodeLocation(), config.getAgentId(), true, false ) );
            mind.fireAllRules();

            return new DroolsAgent( grid, aid, mind );
        } catch ( Throwable t ) {
            if (t.getCause() != null){
                logger.error( "SOMETHING BAD HAPPENED WHILE TRYING TO CREATE AN AGENT " + t.getMessage() + ", due to " + t.getCause().getMessage() );
            }else{
                logger.error( "SOMETHING BAD HAPPENED WHILE TRYING TO CREATE AN AGENT " + t.getMessage());
            }
            if ( logger.isDebugEnabled() ) {
                t.printStackTrace();
            }
            grid.dispose();
        }
        return null;

    }


    
    private void configureGrid( Grid grid, int port ) {
        //Local Grid Configuration, for our client
        GridPeerConfiguration conf = new GridPeerConfiguration();

        //Configuring the Core Services White Pages
        GridPeerServiceConfiguration coreSeviceWPConf = new CoreServicesLookupConfiguration( new HashMap<String, GridServiceDescription>() );
        conf.addConfiguration( coreSeviceWPConf );

        //Configuring the a local WhitePages service that is being shared with all the grid peers
        WhitePagesLocalConfiguration wplConf = new WhitePagesLocalConfiguration();
        //wplConf.setWhitePages(new WhitePagesImpl());
        wplConf.setWhitePages( new JpaWhitePages( Persistence.createEntityManagerFactory( "org.drools.grid" ) ) );
        conf.addConfiguration( wplConf );


        if ( port >= 0 ) {
            //Configuring the SocketService
            MultiplexSocketServiceConfiguration socketConf = new MultiplexSocketServiceConfiguration( new MultiplexSocketServerImpl( "127.0.0.1",
                    new MinaAcceptorFactoryService(),
                    SystemEventListenerFactory.getSystemEventListener(),
                    grid ) );
            socketConf.addService( WhitePages.class.getName(), wplConf.getWhitePages(), port );
//            socketConf.addService( SchedulerService.class.getName(), schlConf.getSchedulerService(), port );

            conf.addConfiguration( socketConf );
        }

        conf.configure( grid );

    }
}