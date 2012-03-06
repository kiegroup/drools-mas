/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.util.helper;

import org.drools.command.CommandFactory;
import org.drools.grid.helper.GridHelper;
import org.drools.grid.remote.command.AsyncBatchExecutionCommandImpl;
import org.drools.runtime.StatefulKnowledgeSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author salaboy
 */
public class ResponseContent implements Serializable{
    private String nodeId;
    private String sessionId; 
    private String messageId;
    private Object data;
    private Fault fault;



    public static void deliverResponse( String $nodeId, String $sessionId, String $msgId, Object $return, Fault $fault ) {

        Map results = null;

        if ( $return != null ) {
            results = new HashMap();
            results.put( "?return", $return );
            System.out.println(" ### SUB-SESSION x: RESPONSE MAP = " + results );
        }

        ResponseContent response = new ResponseContent( $nodeId, $sessionId, $msgId, results );
        response.setFault( $fault );

        StatefulKnowledgeSession kSession = GridHelper.getStatefulKnowledgeSession($nodeId, $sessionId);
        List list = new ArrayList(2);
            list.add( CommandFactory.newInsert(response) );
            list.add( CommandFactory.newFireAllRules() );
        AsyncBatchExecutionCommandImpl batch = new AsyncBatchExecutionCommandImpl( list );
        kSession.execute( batch );
    }



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

    public Fault getFault() {
        return fault;
    }

    public void setFault(Fault fault) {
        this.fault = fault;
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
        if (this.fault != other.fault && (this.fault == null || !this.fault.equals(other.fault))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.nodeId != null ? this.nodeId.hashCode() : 0);
        hash = 89 * hash + (this.sessionId != null ? this.sessionId.hashCode() : 0);
        hash = 89 * hash + (this.messageId != null ? this.messageId.hashCode() : 0);
        hash = 89 * hash + (this.data != null ? this.data.hashCode() : 0);
        hash = 89 * hash + (this.fault != null ? this.fault.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ResponseContent{" + "nodeId=" + nodeId + ", sessionId=" + sessionId + ", messageId=" + messageId + ", data=" + data + ", fault=" + fault + '}';
    }

    
    
}
