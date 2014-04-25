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

import org.drools.io.Resource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.*;
import org.drools.agent.KnowledgeAgent;
import org.drools.mas.util.ResourceDescriptor;

public interface SessionManager{
    
    public void init(String id, KnowledgeBase kbase, DroolsAgentConfiguration conf, DroolsAgentConfiguration.SubSessionDescriptor subDescr);

    public StatefulKnowledgeSession getStatefulKnowledgeSession();
    
    public KnowledgeAgent getKnowledgeAgent();

    public void addResource(ResourceDescriptor rd);

    public void addResource(String resourceId, Resource res);

    public String getSessionId();
    
    /**
     * Gives the chance to dispose a session after a command was executed.
     */
    public void disposeSession();
    
    /**
     * Dispose the session. This method is invoked when the agent is going down.
     */
    public void finalDispose();
}
