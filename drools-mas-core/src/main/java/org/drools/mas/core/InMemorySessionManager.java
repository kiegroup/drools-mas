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

import org.drools.compiler.kie.builder.impl.InternalKieContainer;
import org.drools.core.ClockType;
import org.drools.core.impl.EnvironmentImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of {@link org.drools.mas.core.AbstractSessionManager} that creates an
 * in-memory ksession.
 * @author esteban
 */
public class InMemorySessionManager extends AbstractSessionManager {

    private static final Logger logger = LoggerFactory.getLogger( InMemorySessionManager.class );

    public InMemorySessionManager() {
    }

    public void init( String sessionId,
                      String kieBaseId,
                      KieContainer kieContainer,
                      KieFileSystem kfs,
                      KieBuilder builder,
                      KieBase kbase,
                      DroolsAgentConfiguration agentConf,
                      DroolsAgentConfiguration.SubSessionDescriptor subDescr,
                      boolean isMind ) {

        this.builder = builder;
        this.container = kieContainer;
        this.kfs = kfs;

        this.sessionId = sessionId;
        if (logger.isInfoEnabled()) {
            logger.info(" ### InMemorySessionManager : CREATING session " + sessionId + " using " + kbase);
        }

        if ( isMind ) {
            // Mind sessions will always be resolved to the default session for the given KB
            ksession = kbase != null ? newKieSession( sessionId, kbase ) : (( InternalKieContainer) kieContainer).getKieSession();
        } else {
            // Subsessions will be looked up by id first
            ksession = lookupSessionInContainer( sessionId, kieBaseId, KieServices.Factory.get().getKieClasspathContainer() );

            if ( ksession == null ) {
                ksession = lookupSessionInContainer( sessionId, kieBaseId, kieContainer );
            }

            if ( ksession == null ) {
                ksession = newKieSession( sessionId, kbase );
            }

            if (logger.isInfoEnabled()) {
                logger.info(" ### InMemorySessionManager : Registering session " + sessionId );
            }
        }
    }

    private KieSession lookupSessionInContainer( String sessionId, String kieBaseId, KieContainer kieContainer ) {
        InternalKieContainer ikc = (InternalKieContainer) kieContainer;
        KieBaseModel kbm = ikc.getKieBaseModel( kieBaseId );
        if ( kbm == null ) {
            return null;
        }
        if ( kbm.getKieSessionModels().containsKey( sessionId ) ) {
            return ikc.getKieSession( sessionId );
        } else {
            return null;
        }
    }

    private KieSession newKieSession( String id, KieBase kbase ) {
        KieSessionConfiguration conf = KieServices.Factory.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.REALTIME_CLOCK.toExternalForm() ) );
        Environment env = new EnvironmentImpl();
        env.set( "sessionId", id );
        return kbase.newKieSession( conf, env );
    }


}
