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

import org.drools.*;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.grid.*;
import org.drools.grid.conf.GridPeerServiceConfiguration;
import org.drools.grid.conf.impl.GridPeerConfiguration;
import org.drools.grid.impl.GridImpl;
import org.drools.grid.impl.MultiplexSocketServerImpl;
import org.drools.grid.io.impl.MultiplexSocketServiceCongifuration;
import org.drools.grid.remote.mina.MinaAcceptorFactoryService;
import org.drools.grid.service.directory.WhitePages;
import org.drools.grid.service.directory.impl.CoreServicesLookupConfiguration;
import org.drools.grid.service.directory.impl.WhitePagesLocalConfiguration;
import org.drools.grid.timer.impl.CoreServicesSchedulerConfiguration;
import org.drools.io.Resource;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.builder.*;
import org.drools.io.impl.*;
import org.drools.*;
import org.drools.runtime.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SessionManager extends SessionTemplateManager {

    private KnowledgeAgent kAgent;
    private StatefulKnowledgeSession kSession;
    private Map<String, Resource> resources;
    private static Grid grid;
    private static GridNode remoteNode;
    private static final String DEFAULT_CHANGESET = "org/drools/mas/acl_subsession_def_changeset.xml";

    public static void initGrid() {
        System.out.println("SM constructor");
        grid = new GridImpl(new HashMap<String, Object>());
        remoteNode = createRemoteNode(grid);
    }

    public static SessionManager create(String id, String changeset) {
        try {

            return new SessionManager(id, buildKnowledgeBase(
                    changeset != null ? changeset : DEFAULT_CHANGESET,
                    remoteNode));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public static SessionManager create(String id) {
        try {

            return new SessionManager(id, buildKnowledgeBase(DEFAULT_CHANGESET, remoteNode));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

//     private static KnowledgeBase buildKnowledgeBase(String changeset, GridNode remoteNode) throws IOException {
//        KnowledgeBuilder kbuilder = remoteNode.get(KnowledgeBuilderFactoryService.class).newKnowledgeBuilder();
//
//        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource(changeset).getInputStream())), ResourceType.CHANGE_SET);
//
//        RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
//        rbconf.setEventProcessingMode(EventProcessingOption.STREAM);
//        rbconf.setAssertBehaviour(RuleBaseConfiguration.AssertBehaviour.EQUALITY);
//
//        KnowledgeBase kbase = remoteNode.get(KnowledgeBaseFactoryService.class).newKnowledgeBase(rbconf);
//
//        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
//
//        return kbase;
//     }
    private static KnowledgeBase buildKnowledgeBase(String changeset, GridNode remoteNode) throws IOException {
        System.out.println("Building the Knowledge Base");
       
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, SessionManager.class.getClassLoader());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        kbuilder.add(new ClassPathResource(changeset, SessionManager.class.getClassLoader()), ResourceType.CHANGE_SET);
        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors());
            System.exit(-1);
        }
//        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource(changeset).getInputStream())), ResourceType.CHANGE_SET);

        RuleBaseConfiguration rbconf = new RuleBaseConfiguration(SessionManager.class.getClassLoader());
        rbconf.setEventProcessingMode(EventProcessingOption.STREAM);
        rbconf.setAssertBehaviour(RuleBaseConfiguration.AssertBehaviour.EQUALITY);

//        KnowledgeBase kbase = remoteNode.get(KnowledgeBaseFactoryService.class).newKnowledgeBase(rbconf);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(rbconf);

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        return kbase;
    }

    protected SessionManager(String id, KnowledgeBase kbase) {
        super();
        System.out.println("SessionManager : CREATING session " + id);

        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        kaConfig.setProperty("drools.agent.newInstance", "false");
       // kaConfig.setProperty("drools.agent.useKBaseClassLoaderForCompiling", "true");
        this.kAgent = KnowledgeAgentFactory.newKnowledgeAgent(id, kbase, kaConfig);

//        ChangeSetImpl changeSet = new ChangeSetImpl();
//        ClassPathResource res1 = new ClassPathResource(changeset);
//        res1.setResourceType(ResourceType.CHANGE_SET);
//        changeSet.setResourcesAdded(Arrays.asList((Resource) res1));
//
//        kAgent.applyChangeSet(changeSet);

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setProperty(ClockTypeOption.PROPERTY_NAME, ClockType.REALTIME_CLOCK.toExternalForm());
        this.kSession = kAgent.getKnowledgeBase().newStatefulKnowledgeSession(conf, null);
        this.kSession.setGlobal("manager", this);

        this.resources = new HashMap<String, Resource>();
    }

//    protected StatefulKnowledgeSession newKnowledgeSession() {
//        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
//        conf.setProperty(ClockTypeOption.PROPERTY_NAME, ClockType.REALTIME_CLOCK.toExternalForm());
//        StatefulKnowledgeSession kSession = kAgent.getKnowledgeBase().newStatefulKnowledgeSession(conf, null);
//        kSession.setGlobal("manager",this);
//
//
//        return kSession;
//    }
    public StatefulKnowledgeSession getStatefulKnowledgeSession() {
        return kSession;
    }

    public void addResource(String id, Resource res) {
        ChangeSetImpl changeSet = new ChangeSetImpl();
        changeSet.setResourcesAdded(Arrays.asList(res));

        resources.put(id, res);

        kAgent.applyChangeSet(changeSet);
        System.out.println("xx");
    }

    public void addRule(String id, String drl) {
        ByteArrayResource bar = new ByteArrayResource(drl.getBytes());
        bar.setResourceType(ResourceType.DRL);
        addResource(id, bar);
    }

    public void removeRule(String id) {
        if (this.resources.containsKey(id)) {
            ChangeSetImpl changeSet = new ChangeSetImpl();
            changeSet.setResourcesRemoved(Arrays.asList((Resource) resources.get(id)));
            kAgent.applyChangeSet(changeSet);
        }
    }

    public void addRuleByTemplate(String id, String templateName, Object context) {
        String drl = this.applyTemplate(templateName, context, null);
        System.err.println("Adding rule \n" + drl);
        addRule(id, drl);

        System.err.println("RULE ADDED ____________ \n");

    }

    public KnowledgeAgent getkAgent() {
        return kAgent;
    }

    protected void setkAgent(KnowledgeAgent kAgent) {
        this.kAgent = kAgent;
    }

    protected static GridNode createRemoteNode(Grid grid1) {
        configureGrid(grid1,
                8000,
                null);

        Grid grid2 = new GridImpl(new HashMap<String, Object>());
        configureGrid(grid2,
                -1,
                grid1.get(WhitePages.class));

        GridNode n1 = grid1.createGridNode("n1");
        grid1.get(SocketService.class).addService("n1", 8000, n1);

        GridServiceDescription<GridNode> n1Gsd = grid2.get(WhitePages.class).lookup("n1");
        GridConnection<GridNode> conn = grid2.get(ConnectionFactoryService.class).createConnection(n1Gsd);
        return conn.connect();

    }

    private static void configureGrid(Grid grid,
            int port,
            WhitePages wp) {

        //Local Grid Configuration, for our client
        GridPeerConfiguration conf = new GridPeerConfiguration();

        //Configuring the Core Services White Pages
        GridPeerServiceConfiguration coreSeviceWPConf = new CoreServicesLookupConfiguration(new HashMap<String, GridServiceDescription>());
        conf.addConfiguration(coreSeviceWPConf);

        //Configuring the Core Services Scheduler
        GridPeerServiceConfiguration coreSeviceSchedulerConf = new CoreServicesSchedulerConfiguration();
        conf.addConfiguration(coreSeviceSchedulerConf);

        //Configuring the WhitePages
        WhitePagesLocalConfiguration wplConf = new WhitePagesLocalConfiguration();
        wplConf.setWhitePages(wp);
        conf.addConfiguration(wplConf);

        if (port >= 0) {
            //Configuring the SocketService
            MultiplexSocketServiceCongifuration socketConf = new MultiplexSocketServiceCongifuration(new MultiplexSocketServerImpl("127.0.0.1",
                    new MinaAcceptorFactoryService(),
                    SystemEventListenerFactory.getSystemEventListener(),
                    grid));
            socketConf.addService(WhitePages.class.getName(), wplConf.getWhitePages(), port);

            conf.addConfiguration(socketConf);
        }
        conf.configure(grid);

    }
}
