/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.util.helper;

/**
 *
 * @author salaboy
 */
public class ExecutionFault implements Fault{
    private String msgId;
    private String faultString;

    public ExecutionFault(String msgId, String faultString) {
        this.msgId = msgId;
        this.faultString = faultString;
    }

    public String getFaultString() {
        return faultString;
    }

    public void setFaultString(String faultString) {
        this.faultString = faultString;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "ExecutionFault{" + "msgId=" + msgId + ", faultString=" + faultString + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExecutionFault other = (ExecutionFault) obj;
        if ((this.msgId == null) ? (other.msgId != null) : !this.msgId.equals(other.msgId)) {
            return false;
        }
        if ((this.faultString == null) ? (other.faultString != null) : !this.faultString.equals(other.faultString)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.msgId != null ? this.msgId.hashCode() : 0);
        hash = 97 * hash + (this.faultString != null ? this.faultString.hashCode() : 0);
        return hash;
    }
    
    
    
}
