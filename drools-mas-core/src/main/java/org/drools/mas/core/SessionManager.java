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

import org.drools.agent.KnowledgeAgent;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.grid.*;
import org.drools.grid.api.ResourceDescriptor;
import org.drools.grid.service.directory.Address;
import org.drools.io.Resource;
import org.drools.io.impl.ByteArrayResource;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.builder.*;
import org.drools.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import org.apache.commons.io.IOUtils;
import org.drools.conf.AssertBehaviorOption;
import org.drools.grid.helper.GridHelper;
import org.drools.grid.service.directory.WhitePages;
import org.drools.io.impl.UrlResource;
import org.drools.io.internal.InternalResource;
import org.drools.xml.ChangeSetSemanticModule;
import org.drools.xml.SemanticModules;
import org.drools.xml.XmlChangeSetReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class SessionManager extends SessionTemplateManager {

    private StatefulKnowledgeSession kSession;
    private static final String DEFAULT_CHANGESET = "org/drools/mas/acl_subsession_def_changeset.xml";
    private static Logger logger = LoggerFactory.getLogger(SessionManager.class);

    public static SessionManager create( DroolsAgentConfiguration conf, DroolsAgentConfiguration.SubSessionDescriptor subDescr, Grid grid, boolean forceRemote ) {
        return create( null, conf, subDescr, grid, forceRemote );
    }
    
    public static SessionManager create( String sessionId, DroolsAgentConfiguration conf, DroolsAgentConfiguration.SubSessionDescriptor subDescr, Grid grid, boolean forceRemote ) {
        String id;
        String changeset;
        String nodeId;


        if ( subDescr == null ) {
            id = conf.getAgentId();
            changeset = conf.getChangeset();
            nodeId = conf.getMindNodeLocation();

        } else {
            id = subDescr.getSessionId();
            changeset = subDescr.getChangeset();
            nodeId = subDescr.getNodeId();
        }
        if( sessionId != null ) {
            id = sessionId;
        }
        int port = conf.getPort();
        try {

            GridNode node = grid.getGridNode( nodeId );
            if ( node == null ) {
                // the node is not "local"
                if ( logger.isDebugEnabled() ) {
                    logger.debug( "  ### Session Manager: Looking for Remote Node: " + nodeId );
                }
                GridServiceDescription<GridNode> n1Gsd = grid.get( WhitePages.class ).lookup( nodeId );
                if ( n1Gsd != null ) {
                    if ( logger.isDebugEnabled() ) {
                        logger.debug( "  ### Session Manager: Remote Node Descriptor Found: " + n1Gsd );
                    }

                    if ( grid.getId().equals( n1Gsd.getOwnerGridId() ) && GridNode.class.equals( n1Gsd.getServiceInterface() ) ) {
                        // the node is theoretically owned by this grid, but is not among the local nodes - it's usually due to a stale WP record, claim it by right!
                        node = grid.claimGridNode( nodeId );
                        grid.get( SocketService.class ).addService( nodeId, port, node );
                    } else if ( n1Gsd.getAddresses().size() > 0 ) {
                        Address socketAddress = n1Gsd.getAddresses().get("socket");
                        if ( socketAddress.getObject() instanceof InetSocketAddress ) {
                            // node exists and is alive
                            GridConnection<GridNode> conn = grid.get( ConnectionFactoryService.class ).createConnection( n1Gsd );
                            if ( logger.isDebugEnabled() ) {
                                logger.debug( "  ### Session Manager: Opened connection to node: " + conn );
                            }
                            // node exists and is hopefully alive
                            node = conn.connect();
                            if ( logger.isDebugEnabled() ) {
                                logger.debug( "  ### Session Manager: Got in touch with the node: " + node + ", now look at service " + grid.get( SocketService.class ) );
                                logger.debug( n1Gsd.toString() + " // " + n1Gsd.getAddresses() );
                            }
                        }
                    }

                    if ( node == null ) {
                        // should indicate that someone else left an orphan row in the WP
                        node = grid.claimGridNode( nodeId );
                        grid.get( SocketService.class ).addService( nodeId, port, node );
                    }


                } else {
                    if ( logger.isDebugEnabled() ) {
                        logger.debug( " ### Session Manager: Creating a new Local Node" );
                    }
                    node = createLocalNode( grid, nodeId );

                    grid.get( SocketService.class ).addService( nodeId, port, node );
                }

            } else {
                if ( logger.isDebugEnabled() ) {
                    logger.debug( " ### Session Manager: I have already found a local node ! " );
                }
            }

            return new SessionManager( id, buildKnowledgeBase(
                    changeset != null ?
                            changeset :
                            ( conf.getDefaultSubsessionChangeSet() != null ?
                                conf.getDefaultSubsessionChangeSet() :
                                DEFAULT_CHANGESET
                            ),
                    node ), node );
        } catch ( SAXException ex ) {
            ex.printStackTrace();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();    
        } catch ( IllegalStateException ise ) {
            logger.error( " FATAL : Could not create a Knowledge Base " );
            ise.printStackTrace();
        }
        return null;
    }



    protected SessionManager( String id, KnowledgeBase kbase, GridNode node ) {
        super();
        if ( logger.isInfoEnabled() ) {
            logger.info(" ### SessionManager : CREATING session " + id + " using " + kbase );
        }
//        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
//        kaConfig.setProperty("drools.agent.newInstance", "false");
//        this.kAgent = KnowledgeAgentFactory.newKnowledgeAgent(id, kbase, kaConfig);

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setProperty( ClockTypeOption.PROPERTY_NAME, ClockType.REALTIME_CLOCK.toExternalForm() );
        
//        this.kSession = kAgent.getKnowledgeBase().newStatefulKnowledgeSession(conf, null);
        this.kSession = kbase.newStatefulKnowledgeSession( conf, null );

        //this.kSession.insert(new SessionLocator(node.getId(), id));
        if ( logger.isInfoEnabled() ) {
            logger.info( " ### SessionManager : Registering session " + id + " in node: " + node.getId() );
        }
        node.set( id, this.kSession );
    }

    private static KnowledgeBase buildKnowledgeBase( String changeset, GridNode node ) throws IOException, SAXException, IllegalStateException {
        if ( logger.isDebugEnabled() ) {
            logger.debug( " ### SessionManager : CREATING kbase with CS: " + changeset );
        }
        KnowledgeBuilderFactoryService kbf = node.get( KnowledgeBuilderFactoryService.class );
        if ( kbf == null ) {
            throw new IllegalStateException( "Could not access the KnowledgeBuilderFactoryService" );
        }
        KnowledgeBuilder kbuilder = null;
        try {
            kbuilder = kbf.newKnowledgeBuilder();
        } catch ( Throwable t ) {
            throw new IllegalStateException( "Could not init the KnowledgeBuilder : " + t.getMessage() );
        }
        SemanticModules semanticModules = new SemanticModules();
        semanticModules.addSemanticModule( new ChangeSetSemanticModule() );
        XmlChangeSetReader reader = new XmlChangeSetReader( semanticModules );

        //InputStream inputStream = new ClassPathResource(changeset, SessionManager.class).getInputStream();
        reader.setClassLoader( SessionManager.class.getClassLoader(),
                                   null );
        ChangeSet cs = reader.read( SessionManager.class.getClassLoader().getResourceAsStream( changeset ) );
        Collection<Resource> resourcesAdded = cs.getResourcesAdded();
        for( Resource res: resourcesAdded ){
            
            String file = ( (InternalResource) res ).getURL().getFile();
            if ( ! file.contains( "file:" ) ) {
                file = "file:" + file;
            }
            if ( file.contains( "jar!" ) ) {
                file = "jar:" + file;
            }
            logger.info( "Resource: " + res + " file: " + file );
            InputStream inputStream = new UrlResource( file ).getInputStream();
            byte[] bytes = IOUtils.toByteArray( inputStream );
        
            kbuilder.add( new ByteArrayResource( bytes ), ( (InternalResource) res ).getResourceType() );
        }
        
        
        
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors != null && errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                logger.error( "### Session Manager: Error: " + error.getMessage() );

            }
            throw new IllegalStateException( " ### Session Manager: There were errors during the knowledge compilation ^^^^ !" );
        }
        KnowledgeBaseConfiguration rbconf = node.get( KnowledgeBaseFactoryService.class ).newKnowledgeBaseConfiguration();
        rbconf.setOption( EventProcessingOption.STREAM );
        rbconf.setOption( AssertBehaviorOption.EQUALITY );
        KnowledgeBase kbase = node.get( KnowledgeBaseFactoryService.class ).newKnowledgeBase( rbconf );

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return kbase;
    }

    public StatefulKnowledgeSession getStatefulKnowledgeSession() {
        return kSession;
    }

    public static void addResource( Grid grid, String nodeId, String sessionId, ResourceDescriptor rd ) {

        UrlResource res = new UrlResource( rd.getResourceURL() );
        res.setResourceType(rd.getType());
        addResource( grid, nodeId, sessionId, rd.getId(), res );

    }

    public static void addResource( Grid grid, String nodeId, String sessionId, String id, Resource res ) {
        try {
            if(logger.isDebugEnabled()){
                logger.debug(" ### Session Manager: Add Resource -> nodeId: "+nodeId +" - sessionId: "+sessionId +" - id: "+id+" - res: "+((InternalResource)res).getURL().toString() +" -  type: "+((InternalResource)res).getResourceType().getName());
            }
             String changeSetString = "<change-set xmlns='http://drools.org/drools-5.0/change-set'>"
                    + "<add>"
                    + "<resource type=\""+((InternalResource)res).getResourceType().getName()+"\" source=\""+((InternalResource)res).getURL().toString()+"\" />"
                    + "</add>"
                    + "</change-set>"
                    + "";
            Resource changeSetRes = new ByteArrayResource( changeSetString.getBytes() );
            ((InternalResource) changeSetRes).setResourceType( ResourceType.CHANGE_SET );
            //resources.put(id, res);
            KnowledgeAgent kAgent = GridHelper.getKnowledgeAgentRemoteClient( grid, nodeId, sessionId );
            kAgent.applyChangeSet( changeSetRes );
        } catch (IOException ex) {
            logger.error( " ### SessionManager: " + ex);
        }
    }

//    public static void addRule( String nodeId, String sessionId, String id, String drl ) {
//
//        Resource bar = new ByteArrayResource( drl.getBytes() );
//        ((InternalResource) bar).setResourceType( ResourceType.DRL );
//
//        KnowledgeAgent kAgent = GridHelper.getKnowledgeAgentRemoteClient( nodeId, sessionId );
////        kAgent.loadVolatileResource( id, bar );
//
//    }
//
//
//    public static void removeRule( String nodeId, String sessionId, String id ) {
//
//        KnowledgeAgent kAgent = GridHelper.getKnowledgeAgentRemoteClient( nodeId, sessionId );
////        kAgent.unloadVolatileResource( id );
//
//    }
//
//    public static void addRuleByTemplate( String nodeId, String sessionId,String id, String templateName, Object context ) {
//        String drl = applyTemplate( templateName, context, null );
//
//        if (logger.isDebugEnabled()) {
//            logger.debug(" ### Session Manager: Adding rule \n" + drl);
//        }
//
//        addRule( nodeId, sessionId, id, drl );
//
//        if (logger.isDebugEnabled()) {
//            logger.debug(" ### Session Manager: RULE ADDED ____________ \n");
//        }
//    }

    

   

    private static GridNode createLocalNode( Grid grid, String nodeName ) {
        if ( logger.isDebugEnabled() ) {
            logger.debug( " ### Session Manager: Creating Local Node called: " + nodeName );
        }
        GridNode localNode = grid.createGridNode( nodeName );
        return localNode;
    }
}
