package org.drools.mas.util.helper;

import org.drools.command.CommandFactory;
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
import org.drools.command.runtime.BatchExecutionCommandImpl;

public class ResponseContent implements Serializable{

    private String nodeId;
    private String sessionId;
    private String messageId;
    private Object data;
    private Fault fault;

    private static Logger logger = LoggerFactory.getLogger( ResponseContent.class );

    public static void deliverResponse(String sessionId, String msgId, Object ret, Fault fault ) {
        deliverResponse( sessionId, msgId, ret, fault, false );
    }

    public static void deliverResponse(String sessionId, String msgId, Object ret, Fault fault, boolean needEncoding ) {
        try {
            if ( logger.isDebugEnabled() ) {
                logger.debug( "(" + Thread.currentThread().getId() + ")"+Thread.currentThread().getName() + "Content helper is now active" );
                logger.debug("Invocation StackTrace:");
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                for (int i = 0; i < stackTrace.length; i++) {
                    StackTraceElement stackTraceElement = stackTrace[i];
                    logger.debug(stackTraceElement.toString());
                }
                logger.debug("\n");
            }
            Map results = null;
            if ( ret != null ) {
                results = new HashMap();
                if ( ret instanceof String ) {
                    results.put( "?return", ret );
                } else {
                    if ( needEncoding ) {
                        ret = MessageContentEncoder.encode( ret, Encodings.XML );
                    }
                    results.put( "?return", ret );
                }
                if ( logger.isDebugEnabled() ) {
                    logger.debug("(" + Thread.currentThread().getId() + ")" + Thread.currentThread().getName() + "Content helper would like to return" + results.get("?return"));
                }
            }

            ResponseContent response = new ResponseContent( sessionId, msgId, results );
            response.setFault( fault );

            if ( logger.isDebugEnabled() ) {
                logger.debug( "(" + Thread.currentThread().getId() + ")" + Thread.currentThread().getName() + "Content helper fault is expected to be null " + fault );
            }

            StatefulKnowledgeSession kSession = SessionHelper.getInstance().getSession(sessionId);

            if ( logger.isDebugEnabled() ) {
                logger.debug( "(" + Thread.currentThread().getId() + ")"+Thread.currentThread().getName() +"Content helper ksession found!"  );
            }

            List list = new ArrayList(2);
            list.add( CommandFactory.newInsert(response) );
            list.add( CommandFactory.newFireAllRules() );
            BatchExecutionCommandImpl batch = new BatchExecutionCommandImpl( list );

            if ( logger.isDebugEnabled() ) {
                logger.debug( "(" + Thread.currentThread().getId() + ")"+Thread.currentThread().getName() +"Content helper batch is ready"  );
            }

            kSession.execute( batch );

            if ( logger.isDebugEnabled() ) {
                logger.debug("(" + Thread.currentThread().getId() + ")" + Thread.currentThread().getName() + "Content helper batch dispatched" );
            }
        } catch (Exception e) { e.printStackTrace(); }
    }



    public ResponseContent(String sessionId, String messageId, Object data) {
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
        hash = 89 * hash + (this.sessionId != null ? this.sessionId.hashCode() : 0);
        hash = 89 * hash + (this.messageId != null ? this.messageId.hashCode() : 0);
        hash = 89 * hash + (this.data != null ? this.data.hashCode() : 0);
        hash = 89 * hash + (this.fault != null ? this.fault.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ResponseContent{ sessionId=" + sessionId + ", messageId=" + messageId + ", data=" + data + ", fault=" + fault + '}';
    }



}