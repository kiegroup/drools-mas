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

import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSessionManager extends SessionTemplateManager implements SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSessionManager.class);

    protected String sessionId;
    protected KieSession ksession;
    protected KieFileSystem kfs;
    protected KieBuilder builder;
    protected KieContainer container;


    public AbstractSessionManager() {

    }

    public abstract void init( String sessionId,
                               String kBaseId,
                               KieContainer kieContainer,
                               KieFileSystem kfs,
                               KieBuilder builder,
                               KieBase kbase,
                               DroolsAgentConfiguration conf,
                               DroolsAgentConfiguration.SubSessionDescriptor subDescr,
                               boolean isMind );

    @Override
    public void addResource( Resource res ) {
        addResource( res.getSourcePath(), res );
    }

    public void removeResource( String resourceId ) {
        System.out.println( "TODO remove" );
    }

    @Override
    public void addResource( String resourceId, Resource res ) {
        if (logger.isDebugEnabled()) {
            logger.debug( " ### AbstractSessionManager: Add Resource ->  sessionId: " +
                          this.getSessionId() + " - id: " + resourceId + " - res: " +
                          res + " -  type: " +
                          res.getResourceType().getName() );
        }

        if ( builder != null ) {
            InternalKieBuilder ikb = (InternalKieBuilder) builder;
            kfs.write( res );
            IncrementalResults results = ikb.incrementalBuild();
            if ( ! results.getAddedMessages().isEmpty() ) {
                throw new IllegalStateException( results.getAddedMessages().toString() );
            }
            if ( ! results.getRemovedMessages().isEmpty() ) {
                throw new IllegalStateException( results.getRemovedMessages().toString() );
            }

            container.updateToVersion( builder.getKieModule().getReleaseId() );
        } else {
            throw new UnsupportedOperationException( "Unable to modify session without a builder " );
        }

    }

    @Override
    public String getSessionId(){
        return this.sessionId;
    }


    public KieSession getKieSession() {
        return this.ksession;
    }

    public void finalDispose() {
        this.ksession.dispose();
    }

    public void addRuleByTemplate( String sessionId, String templateName, String id, Object context ) {
        String drl = applyTemplate( templateName, context, null );

        if (logger.isDebugEnabled()) {
            logger.debug(" ### Session Manager: Adding rule \n" + drl);
        }

        ByteArrayResource res = new ByteArrayResource( drl.getBytes() );
        res.setResourceType( ResourceType.DRL );
        res.setSourcePath( id + ".drl" );
        addResource( id, res );

        if (logger.isDebugEnabled()) {
            logger.debug(" ### Session Manager: RULE ADDED ____________ \n");
        }
    }


}
