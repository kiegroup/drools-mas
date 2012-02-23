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
import java.util.List;

public class DroolsAgentConfiguration implements Serializable {

    private String agentId;
    private String changeset;
    private String responseInformer;
    private List<SubSessionDescriptor> subSessions = new ArrayList<SubSessionDescriptor>();
    
    private String springContextFilePath;
    // Local by default, you can override it by configuration
    private String defaultSubsessionChangeSet = "local";
    private String mindNodeLocation;
    private int port = 7000;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    

    public static class SubSessionDescriptor implements Serializable {

        private String sessionId;
        private String changeset;
        private String nodeId;

        public SubSessionDescriptor(String sessionId, String changeset, String nodeId) {
            this.sessionId = sessionId;
            this.changeset = changeset;
            this.nodeId = nodeId;
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
        return "DroolsAgentConfiguration{" + "agentId=" + agentId + ", changeset=" + changeset + ", responseInformer=" + responseInformer + ", subSessions=" + subSessions + ", springContextFilePath=" + springContextFilePath + ", defaultSubsessionChangeSet=" + defaultSubsessionChangeSet + ", mindNodeLocation=" + mindNodeLocation + ", port=" + port + '}';
    }
    
}
