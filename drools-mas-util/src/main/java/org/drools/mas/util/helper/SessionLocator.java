/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.util.helper;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class SessionLocator implements Serializable{
    private String nodeId;
    private String sessionId;

    public SessionLocator(String nodeId, String sessionId) {
        this.nodeId = nodeId;
        this.sessionId = sessionId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String key) {
        this.nodeId = key;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SessionLocator other = (SessionLocator) obj;
        if ((this.nodeId == null) ? (other.nodeId != null) : !this.nodeId.equals(other.nodeId)) {
            return false;
        }
        if ((this.sessionId == null) ? (other.sessionId != null) : !this.sessionId.equals(other.sessionId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.nodeId != null ? this.nodeId.hashCode() : 0);
        hash = 59 * hash + (this.sessionId != null ? this.sessionId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "SessionEntry{" + "nodeId=" + nodeId + ", sessionId=" + sessionId + '}';
    }
    
    
}
