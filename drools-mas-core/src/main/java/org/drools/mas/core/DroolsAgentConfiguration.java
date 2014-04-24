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
    private static String SESSION_MANAGER_CLASS_NAME = "org.drools.mas.core.inmemory.InMemorySessionManager";

    private String agentId;
    private String changeset;
    private String responseInformer;
    private List<SubSessionDescriptor> subSessions = new ArrayList<SubSessionDescriptor>();
    private List<String> subNodes = new ArrayList<String>();

    private String springContextFilePath;
    private String defaultSubsessionChangeSet = "org/drools/mas/acl_subsession_def_changeset.xml";
    private String mindNodeLocation;

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

    public String getChangeset() {
        return changeset;
    }

    public void setChangeset(String changeset) {
        this.changeset = changeset;
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

    public String getDefaultSubsessionChangeSet() {
        return defaultSubsessionChangeSet;
    }

    public void setDefaultSubsessionChangeSet(String defaultSubsessionChangeSet) {
        this.defaultSubsessionChangeSet = defaultSubsessionChangeSet;
    }

    public String getMindNodeLocation() {
        return mindNodeLocation;
    }

    public void setMindNodeLocation(String mindNodeLocation) {
        this.mindNodeLocation = mindNodeLocation;
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
        private String changeset;
        private String nodeId;
        private Map<String, Object> globals = new HashMap<String, Object>();

        public SubSessionDescriptor(String sessionId, String changeset, String nodeId) {
            this.sessionId = sessionId;
            this.changeset = changeset;
            this.nodeId = nodeId;
        }

        public SubSessionDescriptor(String sessionId, String changeset, String nodeId, Map globals) {
            this.sessionId = sessionId;
            this.changeset = changeset;
            this.nodeId = nodeId;

            this.globals.putAll(globals);
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getChangeset() {
            return changeset;
        }

        public void setChangeset(String changeset) {
            this.changeset = changeset;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public Map<String, Object> getGlobals() {
            return globals;
        }

        public String getSessionManagerClassName() {
            return SESSION_MANAGER_CLASS_NAME;
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

            if (changeset != null ? !changeset.equals(that.changeset) : that.changeset != null) {
                return false;
            }
            if (nodeId != null ? !nodeId.equals(that.nodeId) : that.nodeId != null) {
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
            result = 31 * result + (changeset != null ? changeset.hashCode() : 0);
            result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "SubSessionDescriptor{" + "sessionId=" + sessionId + ", changeset=" + changeset + ", nodeId=" + nodeId + '}';
        }

    }

    @Override
    public String toString() {
        return "DroolsAgentConfiguration{" + "agentId=" + agentId + ", changeset=" + changeset + ", responseInformer=" + responseInformer + ", subSessions=" + subSessions + ", springContextFilePath=" + springContextFilePath + ", defaultSubsessionChangeSet=" + defaultSubsessionChangeSet + ", mindNodeLocation=" + mindNodeLocation + '}';
    }

}
