/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.drools.ChangeSet;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;
import org.drools.io.Resource;
import org.drools.io.internal.InternalResource;
import org.drools.mas.core.helpers.SessionHelper;
import org.drools.xml.ChangeSetSemanticModule;
import org.drools.xml.SemanticModules;
import org.drools.xml.XmlChangeSetReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author esteban
 */
public class SessionManagerFactory {

    private static final Logger logger = LoggerFactory.getLogger(SessionManagerFactory.class);

    public static SessionManager create(DroolsAgentConfiguration conf, DroolsAgentConfiguration.SubSessionDescriptor subDescr) {
        return create(null, conf, subDescr);
    }

    public static SessionManager create(String sessionId, DroolsAgentConfiguration conf, DroolsAgentConfiguration.SubSessionDescriptor subDescr) {
        String id;
        String changeset;
        String sessionManagerClassName;

        if (subDescr == null) {
            id = conf.getAgentId();
            changeset = conf.getChangeset();
            sessionManagerClassName = conf.getSessionManagerClassName();
        } else {
            id = subDescr.getSessionId();
            changeset = subDescr.getChangeset();
            sessionManagerClassName = subDescr.getSessionManagerClassName();
        }
        if (sessionId != null) {
            id = sessionId;
        }

        try {

            String cs = changeset != null ? changeset : conf.getDefaultSubsessionChangeSet();

            if (cs == null) {
                throw new IllegalArgumentException("No change-set specified for session '" + id + "'");
            }
            
            if (sessionManagerClassName == null){
                throw new IllegalArgumentException("No SessionManager concrete implementation class specified for session '" + id + "'");
            }
            
            SessionManager sessionManager = (SessionManager) Class.forName(sessionManagerClassName).newInstance();
            
            sessionManager.init(id, buildKnowledgeBase(cs));
            
            SessionHelper.getInstance().registerSessionManager(id, sessionManager);
            
            return sessionManager;

        } catch (Exception ise) {
            logger.error(" FATAL : Could not create a Knowledge Base ");
            ise.printStackTrace();
        }
        return null;
    }

    private static KnowledgeBase buildKnowledgeBase(String changeset) throws IOException, SAXException, IllegalStateException {
        if (logger.isDebugEnabled()) {
            logger.debug(" ### SessionManagerFactory : CREATING kbase with CS: " + changeset);
        }

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        SemanticModules semanticModules = new SemanticModules();
        semanticModules.addSemanticModule(new ChangeSetSemanticModule());
        XmlChangeSetReader reader = new XmlChangeSetReader(semanticModules);

        //InputStream inputStream = new ClassPathResource(changeset, SessionManager.class).getInputStream();
        reader.setClassLoader(AbstractSessionManager.class.getClassLoader(),
                null);
        ChangeSet cs = reader.read(AbstractSessionManager.class.getClassLoader().getResourceAsStream(changeset));
        Collection<Resource> resourcesAdded = cs.getResourcesAdded();
        for (Resource res : resourcesAdded) {

            kbuilder.add(res, ((InternalResource) res).getResourceType());

            KnowledgeBuilderErrors errors = kbuilder.getErrors();
            if (errors != null && errors.size() > 0) {
                for (KnowledgeBuilderError error : errors) {
                    logger.error("### SessionManagerFactory: Error: " + error);
                    logger.error("### >>> " + res + " @ " + Arrays.toString(error.getLines()));
                }
                throw new IllegalStateException(" ### SessionManagerFactory: There were errors during the knowledge compilation ^^^^ !");
            }
        }

        KnowledgeBaseConfiguration rbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        rbconf.setOption(EventProcessingOption.STREAM);
        rbconf.setOption(AssertBehaviorOption.EQUALITY);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(rbconf);

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        return kbase;
    }
}
