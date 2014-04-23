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
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.conf.NewInstanceOption;
import org.drools.agent.conf.UseKnowledgeBaseClassloaderOption;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.impl.EnvironmentImpl;
import org.drools.io.Resource;
import org.drools.io.impl.ByteArrayResource;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.builder.*;
import org.drools.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;
import org.drools.conf.AssertBehaviorOption;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.UrlResource;
import org.drools.io.internal.InternalResource;
import org.drools.mas.util.ResourceDescriptor;
import org.drools.mas.util.helper.SessionHelper;
import org.drools.xml.ChangeSetSemanticModule;
import org.drools.xml.SemanticModules;
import org.drools.xml.XmlChangeSetReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class SessionManager extends SessionTemplateManager {

    private String sessionId;
    private static final String DEFAULT_CHANGESET = "org/drools/mas/acl_subsession_def_changeset.xml";
    private static Logger logger = LoggerFactory.getLogger(SessionManager.class);

    public static SessionManager create(DroolsAgentConfiguration conf, DroolsAgentConfiguration.SubSessionDescriptor subDescr) {
        return create(null, conf, subDescr);
    }

    public static SessionManager create(String sessionId, DroolsAgentConfiguration conf, DroolsAgentConfiguration.SubSessionDescriptor subDescr) {
        String id;
        String changeset;


        if (subDescr == null) {
            id = conf.getAgentId();
            changeset = conf.getChangeset();

        } else {
            id = subDescr.getSessionId();
            changeset = subDescr.getChangeset();
        }
        if (sessionId != null) {
            id = sessionId;
        }
        
        try {

            String cs = changeset != null
                    ? changeset
                    : (conf.getDefaultSubsessionChangeSet() != null
                    ? conf.getDefaultSubsessionChangeSet()
                    : DEFAULT_CHANGESET);
            
            return new SessionManager(id, buildKnowledgeBase(cs));
            
        } catch (Exception ise) {
            logger.error(" FATAL : Could not create a Knowledge Base ");
            ise.printStackTrace();
        }
        return null;
    }

    protected SessionManager(String id, KnowledgeBase kbase) {
        super();
        if (logger.isInfoEnabled()) {
            logger.info(" ### SessionManager : CREATING session " + id + " using " + kbase);
        }
        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();

        conf.setProperty(ClockTypeOption.PROPERTY_NAME, ClockType.REALTIME_CLOCK.toExternalForm());
        Environment env = new EnvironmentImpl();
        env.set("sessionId", id);
        this.sessionId = id;
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(conf, env);
        
        KnowledgeAgent kagent = createKnowledgeAgent(id, kbase);

        if (logger.isInfoEnabled()) {
            logger.info(" ### SessionManager : Registering session " + id );
        }
        SessionHelper.getInstance().registerSession(id, ksession, kagent);

    }

    private KnowledgeAgent createKnowledgeAgent(String id, KnowledgeBase kbase) {
        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        kaConfig.setProperty(NewInstanceOption.PROPERTY_NAME, "false");
        kaConfig.setProperty(UseKnowledgeBaseClassloaderOption.PROPERTY_NAME, "true");
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(id, kbase, kaConfig);
        SystemEventListener systemEventListener = new SystemEventListener() {
            public void info(String string) {
                System.out.println("INFO: " + string);
            }

            public void info(String string, Object o) {
                System.out.println("INFO: " + string + ", " + o);
            }

            public void warning(String string) {
                System.out.println("WARN: " + string);
            }

            public void warning(String string, Object o) {
                System.out.println("WARN: " + string + ", " + o);
            }

            public void exception(String string, Throwable thrwbl) {
                System.out.println("EXCEPTION: " + string + ", " + thrwbl);
            }

            public void exception(Throwable thrwbl) {
                System.out.println("EXCEPTION: " + thrwbl);
            }

            public void debug(String string) {
                System.out.println("DEBUG: " + string);
            }

            public void debug(String string, Object o) {
                System.out.println("DEBUG: " + string + ", " + o);
            }
        };

        kagent.setSystemEventListener(systemEventListener);
        
        return kagent;
    }

    private static KnowledgeBase buildKnowledgeBase(String changeset) throws IOException, SAXException, IllegalStateException {
        if (logger.isDebugEnabled()) {
            logger.debug(" ### SessionManager : CREATING kbase with CS: " + changeset);
        }

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        SemanticModules semanticModules = new SemanticModules();
        semanticModules.addSemanticModule(new ChangeSetSemanticModule());
        XmlChangeSetReader reader = new XmlChangeSetReader(semanticModules);

        //InputStream inputStream = new ClassPathResource(changeset, SessionManager.class).getInputStream();
        reader.setClassLoader(SessionManager.class.getClassLoader(),
                null);
        ChangeSet cs = reader.read(SessionManager.class.getClassLoader().getResourceAsStream(changeset));
        Collection<Resource> resourcesAdded = cs.getResourcesAdded();
        for (Resource res : resourcesAdded) {

            /*
            String file = ((InternalResource) res).getURL().getFile();
            if (!file.contains("file:")) {
                file = "file:" + file;
            }
            if (file.contains("jar!")) {
                file = "jar:" + file;
            }
            logger.info("Resource: " + res + " file: " + file);
            InputStream inputStream = new UrlResource(file).getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);

            kbuilder.add(new ByteArrayResource(bytes), ((InternalResource) res).getResourceType());
            */
            
            kbuilder.add(res, ((InternalResource) res).getResourceType());
                    
            KnowledgeBuilderErrors errors = kbuilder.getErrors();
            if (errors != null && errors.size() > 0) {
                for (KnowledgeBuilderError error : errors) {
                    logger.error("### Session Manager: Error: " + error);
                    logger.error("### >>> " + res + " @ " + Arrays.toString(error.getLines()));
                }
                throw new IllegalStateException(" ### Session Manager: There were errors during the knowledge compilation ^^^^ !");
            }
        }


        KnowledgeBaseConfiguration rbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        rbconf.setOption(EventProcessingOption.STREAM);
        rbconf.setOption(AssertBehaviorOption.EQUALITY);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(rbconf);

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        return kbase;
    }

    public StatefulKnowledgeSession getStatefulKnowledgeSession() {
        return SessionHelper.getInstance().getSession(this.sessionId);
    }

    public static void addResource(String sessionId, ResourceDescriptor rd) {

        UrlResource res = new UrlResource(rd.getResourceURL());
        res.setResourceType(rd.getType());
        addResource(sessionId, rd.getId(), res);

    }

    public static void addResource(String sessionId, String resourceId, Resource res) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(" ### Session Manager: Add Resource ->  sessionId: " + sessionId + " - id: " + resourceId + " - res: " + ((InternalResource) res).getURL().toString() + " -  type: " + ((InternalResource) res).getResourceType().getName());
            }
            String changeSetString = "<change-set xmlns='http://drools.org/drools-5.0/change-set'>"
                    + "<add>"
                    + "<resource type=\"" + ((InternalResource) res).getResourceType().getName() + "\" source=\"" + ((InternalResource) res).getURL().toString() + "\" />"
                    + "</add>"
                    + "</change-set>"
                    + "";

            Resource changeSetRes = new ByteArrayResource(changeSetString.getBytes());
            ((InternalResource) changeSetRes).setResourceType(ResourceType.CHANGE_SET);
            //resources.put(id, res);

            KnowledgeAgent kAgent = SessionHelper.getInstance().getKagent(sessionId);
            kAgent.applyChangeSet(changeSetRes);
        } catch (IOException ex) {
            logger.error(" ### SessionManager: " + ex);
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

    public static void addResource(WorkingMemory ksession, ResourceDescriptor rd) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newUrlResource(rd.getResourceURL()), rd.getType());

        if (kbuilder.hasErrors()) {
            Iterator<KnowledgeBuilderError> iterator = kbuilder.getErrors().iterator();
            while (iterator.hasNext()) {
                KnowledgeBuilderError knowledgeBuilderError = iterator.next();
                logger.debug(" ### Session Manager: Error compiling resource '" + rd.getResourceURL() + "': " + knowledgeBuilderError.getMessage());
            }
        }

        org.drools.rule.Package[] packages = new org.drools.rule.Package[kbuilder.getKnowledgePackages().size()];
        int i = 0;
        for (KnowledgePackage knowledgePackage : kbuilder.getKnowledgePackages()) {
            packages[i++] = ((KnowledgePackageImp) knowledgePackage).pkg;
        }

        ksession.getRuleBase().addPackages(packages);

    }
}
