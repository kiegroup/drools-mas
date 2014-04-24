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
package org.drools.mas.core.inmemory;

import org.drools.agent.KnowledgeAgent;
import org.drools.impl.EnvironmentImpl;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.*;
import org.drools.mas.core.AbstractSessionManager;
import org.drools.mas.core.helpers.SessionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of {@link AbstractSessionManager} that creates an
 * in-memory ksession.
 * @author esteban
 */
public class InMemorySessionManager extends AbstractSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(InMemorySessionManager.class);
    
    private StatefulKnowledgeSession ksession;
    private KnowledgeAgent kagent;

    public InMemorySessionManager() {
    }
    
    @Override
    public void init(String id, KnowledgeBase kbase){
        super.init(id, kbase);
        
        if (logger.isInfoEnabled()) {
            logger.info(" ### InMemorySessionManager : CREATING session " + id + " using " + kbase);
        }
        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();

        conf.setProperty(ClockTypeOption.PROPERTY_NAME, ClockType.REALTIME_CLOCK.toExternalForm());
        Environment env = new EnvironmentImpl();
        env.set("sessionId", id);
        
        ksession = kbase.newStatefulKnowledgeSession(conf, env);
        kagent = createKnowledgeAgent(id, kbase);

        if (logger.isInfoEnabled()) {
            logger.info(" ### InMemorySessionManager : Registering session " + id );
        }
        
    }

    public StatefulKnowledgeSession getStatefulKnowledgeSession() {
        return this.ksession;
    }

    public KnowledgeAgent getKnowledgeAgent() {
        return this.kagent;
    }
    
    public void disposeSession() {
        //do nothing. In-memory sessions are never disposed.
    }

    public void finalDispose() {
        this.ksession.dispose();
        this.kagent.dispose();
    }

    
}
