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
package org.drools.mas.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.impl.EnvironmentImpl;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.*;
import org.drools.mas.core.AbstractSessionManager;
import org.drools.mas.core.DroolsAgentConfiguration;
import org.drools.mas.persistence.entity.SessionIdsMapping;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.EnvironmentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author esteban
 */
public class PersistentSessionManager extends AbstractSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(PersistentSessionManager.class);

    private Environment env;
    private KnowledgeSessionConfiguration conf;

    private KnowledgeBase kbase;
    private StatefulKnowledgeSession ksession;
    private KnowledgeAgent kagent;

    public PersistentSessionManager() {
    }

    @Override
    public synchronized void init(String id, KnowledgeBase kbase, DroolsAgentConfiguration agentConf, DroolsAgentConfiguration.SubSessionDescriptor subDescr) {
        super.init(id, kbase, agentConf, subDescr);

        if (subDescr == null) {
            throw new UnsupportedOperationException("Agent mind can't be persistent. This configuration is not yet implemented");
        }

        if (!(subDescr instanceof PersistentSubSessionDescriptor)) {
            throw new IllegalArgumentException("Expecting a subclass of org.drools.mas.persistence.PersistentSubSessionDescriptor as sub-session descriptor and not " + subDescr.getClass().getName());
        }
        PersistentSubSessionDescriptor subsessionDescriptor = (PersistentSubSessionDescriptor) subDescr;

        if (logger.isInfoEnabled()) {
            logger.info(" ### PersistentSessionManager : CREATING PERSISTENT session " + id + " using " + kbase);
        }

        this.kbase = kbase;
        this.conf = this.createKnowledgeSessionConfiguration(subsessionDescriptor);
        this.env = this.createEnvironment(id, subsessionDescriptor);

        this.ksession = this.getOrCreateStatefulKnowledgeSession();
        this.kagent = createKnowledgeAgent(id, kbase);

        if (logger.isInfoEnabled()) {
            logger.info(" ### PersistentSessionManager : Registering PERSISTENT session " + id);
        }

    }

    @Override
    public synchronized StatefulKnowledgeSession getStatefulKnowledgeSession() {
        if (this.ksession != null) {
            return this.ksession;
        }

        this.ksession = this.getOrCreateStatefulKnowledgeSession();
        this.kagent = createKnowledgeAgent(this.getSessionId(), kbase);

        return this.ksession;
    }

    @Override
    public synchronized KnowledgeAgent getKnowledgeAgent() {
        if (this.kagent == null) {
            this.getStatefulKnowledgeSession();
        }

        return this.kagent;
    }

    @Override
    public synchronized void disposeSession() {
        if (this.ksession != null){
            this.ksession.dispose();
            this.ksession = null;
        }
        if (this.kagent != null){
            this.kagent.dispose();
            this.kagent = null;
        }
    }

    @Override
    public synchronized void finalDispose() {
        this.disposeSession();
    }

    private Environment createEnvironment(String sessionId, PersistentSubSessionDescriptor descriptor) {
        Environment env = new EnvironmentImpl();
        env.set("sessionId", sessionId);
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, descriptor.getEntityManagerFactory());
        env.set(EnvironmentName.TRANSACTION_MANAGER, descriptor.getTransactionManager());

        return env;
    }

    private KnowledgeSessionConfiguration createKnowledgeSessionConfiguration(PersistentSubSessionDescriptor descriptor) {
        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setProperty(ClockTypeOption.PROPERTY_NAME, ClockType.REALTIME_CLOCK.toExternalForm());

        return conf;
    }

    /**
     * Tries to restore a session from the database (using {@link #getSessionId()})
     * or creates a new one if it doesn't exist.
     * @return 
     */
    private StatefulKnowledgeSession getOrCreateStatefulKnowledgeSession() {
        Integer sessionDatabaseId = this.getSessionDatabaseId();

        if (sessionDatabaseId == null) {
            
            if (logger.isInfoEnabled()) {
                logger.info(" ### PersistentSessionManager : Creating NEW PERSISTENT session " + this.getSessionId());
            }
            
            //this is a new session.
            StatefulKnowledgeSession newStatefulKnowledgeSession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, conf, env);

            //persist the ids mapping
            SessionIdsMapping mapping = new SessionIdsMapping();
            mapping.setConfigurationId(this.getSessionId());
            mapping.setDatabaseId(newStatefulKnowledgeSession.getId());

            EntityManager em = ((EntityManagerFactory) this.env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)).createEntityManager();
            try {
                //TODO: this is not going to work with JTA
                em.getTransaction().begin();
                em.persist(mapping);
                em.getTransaction().commit();
            } finally {
                if (em != null) {
                    em.close();
                }
            }
            
            return newStatefulKnowledgeSession;
        } else {
            
            if (logger.isInfoEnabled()) {
                logger.info(" ### PersistentSessionManager : Loading PERSISTENT session with id '" + this.getSessionId()+"' from DB.");
            }
            
            //restore the session from the database.
            return JPAKnowledgeService.loadStatefulKnowledgeSession(sessionDatabaseId, kbase, conf, env);
        }
    }

    /**
     * Returns the corresponding id of the SessionInfo associated to the
     * object's sessionId.
     *
     * @return
     */
    private Integer getSessionDatabaseId() {
        EntityManager em = ((EntityManagerFactory) this.env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)).createEntityManager();
        try {
            SessionIdsMapping mapping = em.find(SessionIdsMapping.class, this.getSessionId());
            if (mapping == null) {
                return null;
            }
            return mapping.getDatabaseId();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

}
