package org.drools.mas.util.helper;

import org.drools.command.CommandFactory;
import org.drools.grid.helper.GridHelper;
import org.drools.grid.remote.command.AsyncBatchExecutionCommandImpl;
import org.drools.mas.Encodings;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseContent implements Serializable{
    private String nodeId;
    private String sessionId;
    private String messageId;
    private Object data;
    private Fault fault;

    private static Logger logger = LoggerFactory.getLogger( ResponseContent.class );


    public static void deliverResponse( String $nodeId, String $sessionId, String $msgId, Object $return, Fault $fault ) {
        try {
            if ( logger.isDebugEnabled() ) {
                logger.debug( "(" + Thread.currentThread().getId() + ")"+Thread.currentThread().getName() +"Content helper is now active" );
            }
            Map results = null;
            if ( $return != null ) {
                results = new HashMap();
                if ( $return instanceof String ) {
                    results.put( "?return", $return );
                } else {
                    String ret = MessageContentEncoder.encode( $return, Encodings.XML );
                    results.put( "?return", ret );
                }
                if ( logger.isDebugEnabled() ) {
                    logger.debug("(" + Thread.currentThread().getId() + ")" + Thread.currentThread().getName() + "Content helper would like to return" + results.get("?return"));
                }
            }

            ResponseContent response = new ResponseContent( $nodeId, $sessionId, $msgId, results );
            response.setFault( $fault );

            if ( logger.isDebugEnabled() ) {
                logger.debug("(" + Thread.currentThread().getId() + ")" + Thread.currentThread().getName() + "Content helper fault is expected to be null " + $fault);
            }

            StatefulKnowledgeSession kSession = GridHelper.getStatefulKnowledgeSession( $nodeId, $sessionId );

            if ( logger.isDebugEnabled() ) {
                logger.debug( "(" + Thread.currentThread().getId() + ")"+Thread.currentThread().getName() +"Content helper ksession found!"  );
            }

            List list = new ArrayList(2);
            list.add( CommandFactory.newInsert(response) );
            list.add( CommandFactory.newFireAllRules() );
            AsyncBatchExecutionCommandImpl batch = new AsyncBatchExecutionCommandImpl( list );

            if ( logger.isDebugEnabled() ) {
                logger.debug( "(" + Thread.currentThread().getId() + ")"+Thread.currentThread().getName() +"Content helper batch is ready"  );
            }

            kSession.execute( batch );

            if ( logger.isDebugEnabled() ) {
                logger.debug("(" + Thread.currentThread().getId() + ")" + Thread.currentThread().getName() + "Content helper batch dispatched");
            }
        } catch (Exception e) { e.printStackTrace(); }
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