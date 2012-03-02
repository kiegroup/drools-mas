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
public class ResponseContent implements Serializable{
    private String nodeId;
    private String sessionId; 
    private String messageId;
    private Object data;

    public ResponseContent(String nodeId, String sessionId, String messageId, Object data) {
        this.nodeId = nodeId;
        this.sessionId = sessionId;
        this.messageId = messageId;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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
        final ResponseContent other = (ResponseContent) obj;
        if ((this.nodeId == null) ? (other.nodeId != null) : !this.nodeId.equals(other.nodeId)) {
            return false;
        }
        if ((this.sessionId == null) ? (other.sessionId != null) : !this.sessionId.equals(other.sessionId)) {
            return false;
        }
        if ((this.messageId == null) ? (other.messageId != null) : !this.messageId.equals(other.messageId)) {
            return false;
        }
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.nodeId != null ? this.nodeId.hashCode() : 0);
        hash = 29 * hash + (this.sessionId != null ? this.sessionId.hashCode() : 0);
        hash = 29 * hash + (this.messageId != null ? this.messageId.hashCode() : 0);
        hash = 29 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ResponseContent{" + "nodeId=" + nodeId + ", sessionId=" + sessionId + ", messageId=" + messageId + ", data=" + data + '}';
    }
    
    
}
