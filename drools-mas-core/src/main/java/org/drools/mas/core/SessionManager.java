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

import org.drools.mas.core.DroolsAgentConfiguration;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public interface SessionManager{
    
    public void init( String sessionId,
                      String kbaseId,
                      KieContainer kieContainer,
                      KieFileSystem kfs,
                      KieBuilder builder,
                      KieBase kbase,
                      DroolsAgentConfiguration conf,
                      DroolsAgentConfiguration.SubSessionDescriptor subDescr,
                      boolean isMind );

    public KieSession getKieSession();

    public void addResource( Resource rd );

    public void addResource( String resourceId, Resource res );

    public void addRuleByTemplate( String sessionId, String templateName, String id, Object context );

    public void removeResource( String resourceId );

    public String getSessionId();

    /**
     * Dispose the session. This method is invoked when the agent is going down.
     */
    public void finalDispose();
}
