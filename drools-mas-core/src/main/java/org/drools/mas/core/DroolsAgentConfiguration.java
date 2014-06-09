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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DroolsAgentConfiguration implements Serializable {

    /**
     * Given that different configurations could require specific SessionManager
     * implementations that could not be present in compilation time, we need to
     * specify the name of the concrete class we want to use in runtime.
     * Ideally, the relationship between DroolsAgentConfiguration and concrete
     * SessionManager implementations is always 1->1 or N->1.
     */
    private static final String SESSION_MANAGER_CLASS_NAME = InMemorySessionManager.class.getName();

    private String agentId;
    private String kieBaseId;
    private String defaultKieBaseId = "absent_mind";
    private String responseInformer;
    private List<SubSessionDescriptor> subSessions = new ArrayList<SubSessionDescriptor>();
    
    /**
     * Sub-session descriptor used to generate dynamic sub-sessions on demand.
     * The sessionId and nodeId of this object are going to be overwritten
     * when the session needs to be created.
     * By default, the value of this property is a {@link SubSessionDescriptor}
     * with no specific (null) change-set.
     */
    private SubSessionDescriptor defaultSubsessionDescriptor = new SubSessionDescriptor(null, null);
    
    private List<String> subNodes = new ArrayList<String>();

    private String springContextFilePath;
    
    /**
     * If a sub-session descriptor doesn't specify any change-set, then
     * use this change-set as default.
     */
    private String defaultSubsessionKieBaseId = "simple_mind";

    private Map<String, Object> globals = new HashMap<String, Object>();

    public DroolsAgentConfiguration() {
    }

    public String getSpringContextFilePath() {
        return springContextFilePath;
    }

    public void setSpringContextFilePath(String springContextFilePath) {
        this.springContextFilePath = springContextFilePath;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getKieBaseId() {
        return kieBaseId;
    }

    public void setKieBaseId( String kieBaseId ) {
        this.kieBaseId = kieBaseId;
    }

    public String getDefaultKieBaseId() {
        return defaultKieBaseId;
    }

    public void setDefaultKieBaseId( String defaultKieBaseId ) {
        this.defaultKieBaseId = defaultKieBaseId;
    }

    public String getResponseInformer() {
        return responseInformer;
    }

    public void setResponseInformer(String responseInformer) {
        this.responseInformer = responseInformer;
    }

    public List<SubSessionDescriptor> getSubSessions() {
        return subSessions;
    }

    public void setSubSessions(List<SubSessionDescriptor> subSessions) {
        for (SubSessionDescriptor subSessionDescriptor : subSessions) {
            this.addSubSession(subSessionDescriptor);
        }
    }

    public void addSubSession(SubSessionDescriptor sub) {
        this.subSessions.add(sub);
    }

    public SubSessionDescriptor getDefaultSubsessionDescriptor() {
        return defaultSubsessionDescriptor;
    }

    public void setDefaultSubsessionDescriptor(SubSessionDescriptor defaultSubsessionDescriptor) {
        this.defaultSubsessionDescriptor = defaultSubsessionDescriptor;
    }

    public String getDefaultSubsessionKieBaseId() {
        return defaultSubsessionKieBaseId;
    }

    public void setDefaultSubsessionKieBaseId( String defaultSubsessionKieBaseId ) {
        this.defaultSubsessionKieBaseId = defaultSubsessionKieBaseId;
    }

    public List<String> getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(List<String> subNodes) {
        this.subNodes = subNodes;
    }

    public void addSubNode(String node) {
        this.subNodes.add(node);
    }

    public Map<String, Object> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<String, Object> globals) {
        this.globals.putAll(globals);
    }

    public String getSessionManagerClassName() {
        return SESSION_MANAGER_CLASS_NAME;
    }

    public static class SubSessionDescriptor implements Serializable {

        private String sessionId;
        private String kieBaseId;
        private boolean mutable = true;
        private Map<String, Object> globals = new HashMap<String, Object>();

        public SubSessionDescriptor( String sessionId, String kieBaseId ) {
            this( sessionId, kieBaseId, Collections.emptyMap(), true );
        }

        public SubSessionDescriptor( String sessionId, String kieBaseId, Map globals ) {
            this( sessionId, kieBaseId, globals, true );
        }

        public SubSessionDescriptor( String sessionId, String kieBaseId, Map globals, boolean mutable ) {
            this.sessionId = sessionId;
            this.kieBaseId = kieBaseId;
            this.globals.putAll( globals );
            this.mutable = mutable;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getKieBaseId() {
            return kieBaseId;
        }

        public void setKieBaseId( String kieBaseId ) {
            this.kieBaseId = kieBaseId;
        }

        public Map<String, Object> getGlobals() {
            return globals;
        }

        public String getSessionManagerClassName() {
            return SESSION_MANAGER_CLASS_NAME;
        }

        public boolean isMutable() {
            return mutable;
        }

        public void setMutable( boolean mutable ) {
            this.mutable = mutable;
        }

        public SubSessionDescriptor makeClone(){
            return new SubSessionDescriptor(this.getSessionId(), this.getKieBaseId(), this.getGlobals(), this.mutable );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SubSessionDescriptor that = (SubSessionDescriptor) o;

            if ( kieBaseId != null ? !kieBaseId.equals(that.kieBaseId ) : that.kieBaseId != null) {
                return false;
            }
            if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = sessionId != null ? sessionId.hashCode() : 0;
            result = 31 * result + ( kieBaseId != null ? kieBaseId.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "SubSessionDescriptor{" + "sessionId=" + sessionId + ", kieBaseId=" + kieBaseId + '}';
        }

    }

    @Override
    public String toString() {
        return "DroolsAgentConfiguration{" + "agentId=" + agentId + ", kieBaseId=" + kieBaseId + ", responseInformer=" + responseInformer + ", subSessions=" + subSessions + ", springContextFilePath=" + springContextFilePath + ", defaultSubsessionKieBaseId=" + defaultSubsessionKieBaseId + '}';
    }

}
