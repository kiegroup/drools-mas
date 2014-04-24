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
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.impl.ByteArrayResource;
import org.drools.*;
import java.io.IOException;
import org.drools.io.impl.UrlResource;
import org.drools.io.internal.InternalResource;
import org.drools.mas.util.ResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSessionManager extends SessionTemplateManager implements SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSessionManager.class);
    private String sessionId;

    public AbstractSessionManager() {
    }
    
    public void init(String id, KnowledgeBase kbase){
        this.sessionId = id;
    }

    protected KnowledgeAgent createKnowledgeAgent(String id, KnowledgeBase kbase) {
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

    @Override
    public void addResource(ResourceDescriptor rd) {

        UrlResource res = new UrlResource(rd.getResourceURL());
        res.setResourceType(rd.getType());
        addResource(rd.getId(), res);

    }

    @Override
    public void addResource(String resourceId, Resource res) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(" ### AbstractSessionManager: Add Resource ->  sessionId: " + this.getSessionId() + " - id: " + resourceId + " - res: " + ((InternalResource) res).getURL().toString() + " -  type: " + ((InternalResource) res).getResourceType().getName());
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

            KnowledgeAgent kAgent = this.getKnowledgeAgent();
            kAgent.applyChangeSet(changeSetRes);
        } catch (IOException ex) {
            logger.error(" ### AbstractSessionManager: " + ex);
        }
    }

    @Override
    public String getSessionId(){
        return this.sessionId;
    }
}
