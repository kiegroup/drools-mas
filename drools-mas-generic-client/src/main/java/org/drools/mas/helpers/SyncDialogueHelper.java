package org.drools.mas.helpers;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.xml.namespace.QName;
import org.drools.mas.*;
import org.drools.mas.body.acts.AbstractMessageBody;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.body.acts.InformRef;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.acts.InformIf;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.drools.runtime.rule.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncDialogueHelper {

    boolean multiReturnValue = false;
    private AbstractMessageBody returnBody;
    private Encodings encode = Encodings.XML;
    private URL endpointURL;
    private QName qname;
    private long maximumWaitTime = 60000;
    private long minWaitTime = 500;
    private int maxRetries = 1;

    private static Logger logger = LoggerFactory.getLogger( SyncDialogueHelper.class.getName() );

    public SyncDialogueHelper(String url, Encodings enc) {
        try {
            this.endpointURL = new URL(AsyncAgentService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            logger.error( ex.getMessage() );
        }
        this.qname = new QName("http://mas.drools.org/", "AsyncAgentService");
        this.encode = enc;
    }

    public SyncDialogueHelper(String url) {
        try {
            this.endpointURL = new URL(AsyncAgentService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            logger.error(ex.getMessage());
        }
        this.qname = new QName("http://mas.drools.org/", "AsyncAgentService");

    }

    public long getMaximumWaitTime() {
        return maximumWaitTime;
    }

    public void setMaximumWaitTime( long maximumWaitTime ) {
        this.maximumWaitTime = maximumWaitTime;
    }

    public long getMinWaitTime() {
        return minWaitTime;
    }

    public void setMinWaitTime( long minWaitTime ) {
        this.minWaitTime = minWaitTime;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries( int maxRetries ) {
        this.maxRetries = maxRetries;
    }

    public String invokeRequest( String sender, String methodName, LinkedHashMap<String, Object> args ) throws UnsupportedOperationException {
        return invokeRequest( sender, "", methodName, args);
    }


    public String invokeRequest( String methodName, LinkedHashMap<String, Object> args ) throws UnsupportedOperationException {
        return invokeRequest( UUID.randomUUID().toString(), "", methodName, args );
    }


    public String invokeRequest( String sender, String receiver, String methodName, LinkedHashMap<String, Object> args ) 
            throws UnsupportedOperationException, IllegalStateException {
        
        multiReturnValue = false;

        int numVars = 0;
        for ( Object o : args.values() ) {
            if ( o == Variable.v ) {
                numVars++;
                if ( numVars >= 2 ) {
                    multiReturnValue = true;
                    break;
                }
            }
        }

        AsyncDroolsAgentService asyncServicePort = null;
        
        if ( this.endpointURL == null || this.qname == null ) {
            throw new IllegalStateException( "A Web Service URL and a QName Must be Provided for the client to work!" );
        } else {
            asyncServicePort = new AsyncAgentService( this.endpointURL, this.qname ).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory( encode );

        Action action = MessageContentFactory.newActionContent( methodName, args );


        int numTries = 0;
        do {
            ACLMessage req = factory.newRequestMessage( sender, receiver, action );
            asyncServicePort.tell( req );

            List<ACLMessage> answers = waitForAnswers( asyncServicePort, req.getId(), 2, maximumWaitTime, methodName );

            if ( validateRequestResponses( answers, methodName, args ) ) {
                if ( ! multiReturnValue ) {
                    returnBody = answers.size() == 2 ? ( (Inform) answers.get( 1 ).getBody() ) : null;
                } else {
                    returnBody = answers.size() == 2 ? ( (InformRef) answers.get( 1 ).getBody() ) : null;
                }

                return req.getId();
            }
        } while ( ++numTries < maxRetries );

        throw new IllegalStateException(" Request " + methodName + " with args "
                            + args + " did not return in time" );
    }
    
    
    private boolean validateRequestResponses( List<ACLMessage> answers, String methodName, Map<String,Object> args ) {
        if ( answers.size() != 2 ) {
            return false;
        }

        if ( Act.AGREE.equals( answers.get( 1 ).getPerformative() ) ) {
            throw new IllegalStateException( " TODO : Agree was collected after its response, check rules" );
        }
        
        ACLMessage answer1 = answers.get( 0 );
        if ( ! Act.AGREE.equals( answer1.getPerformative() ) ) {
            throw new UnsupportedOperationException( " Request " + methodName + " was not agreed with args " + args );
        }

        ACLMessage answer2 = answers.get( 1 );
        Act act2 = answer2.getPerformative();

        if ( ! ( Act.INFORM.equals( act2 ) || Act.INFORM_REF.equals( act2 ) ) ) {
            throw new IllegalStateException(" Request " + methodName + " with args "
                    + args + "failed and returned this msg: " + answer2 );
        }        
        return true;
    }

    public String invokeQueryIf( String sender, String receiver, Object proposition ) {
        AsyncDroolsAgentService asyncServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage qryif = factory.newQueryIfMessage(sender, receiver, proposition);
        asyncServicePort.tell(qryif);

        List<ACLMessage> answers = waitForAnswers( asyncServicePort, qryif.getId(), 1, maximumWaitTime, proposition.toString() );

        returnBody = answers.get(0).getBody();
        return qryif.getId();

    }

    public String invokeInform( String sender, String receiver, Object proposition ) {
        AsyncDroolsAgentService asyncServicePort;
        if ( this.endpointURL == null || this.qname == null ) {
            throw new IllegalStateException( "A Web Service URL and a QName Must be Provided for the client to work!" );
        } else {
            asyncServicePort = new AsyncAgentService( this.endpointURL, this.qname ).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory( encode );
        ACLMessage newInformMessage = factory.newInformMessage( sender, receiver, proposition );

        asyncServicePort.tell( newInformMessage );

        return newInformMessage.getId();


    }

    public String invokeConfirm( String sender, String receiver, Object proposition ) {
        AsyncDroolsAgentService asyncServicePort;
        if ( this.endpointURL == null || this.qname == null ) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory( encode );
        ACLMessage newConfirmMessage = factory.newConfirmMessage( sender, receiver, proposition );

        asyncServicePort.tell( newConfirmMessage );

        return newConfirmMessage.getId();


    }

    public String invokeDisconfirm( String sender, String receiver, Object proposition ) {
        AsyncDroolsAgentService asyncServicePort;
        if ( this.endpointURL == null || this.qname == null ) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService( this.endpointURL, this.qname ).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory( encode );
        ACLMessage newDisconfirmMessage = factory.newDisconfirmMessage( sender, receiver, proposition );

        asyncServicePort.tell( newDisconfirmMessage );

        return newDisconfirmMessage.getId();

    }

    private List<ACLMessage> waitForAnswers( AsyncDroolsAgentService asyncServicePort, String msgid, int numExpectedMessages, long maxTimeout, String msgRef ) {
            List<ACLMessage> answers = new ArrayList<ACLMessage>();
            long waitTime = minWaitTime;
            do {
                try {
                    logger.debug( " >>> Waiting for answers (" + waitTime + ") for: Message ->  " + msgRef );
                    Thread.sleep( waitTime );
                } catch ( InterruptedException ex ) {
                    logger.error( ex.getMessage() );
                }
                List<ACLMessage> incomingAnswers = asyncServicePort.getResponses(msgid);
                answers.addAll( incomingAnswers );

                waitTime *= 2;
            } while ( answers.size() != numExpectedMessages && waitTime < maxTimeout );
            return answers;
        }

    public Object getReturn( boolean decode ) throws UnsupportedOperationException {
        if ( returnBody == null ) {
            return null;
        }
        if ( decode ) {
            MessageContentEncoder.decodeBody( returnBody, encode );
            if ( returnBody instanceof Inform ) {
                return ( (Inform) returnBody ).getProposition().getData();
            }
            if ( returnBody instanceof InformIf ) {
                return ( (InformIf) returnBody ).getProposition().getData();
            }
        } else {
            if ( returnBody instanceof Inform ) {
                return ( (Inform) returnBody ).getProposition().getEncodedContent();
            }
            if ( returnBody instanceof InformIf ) {
                return ( (InformIf) returnBody ).getProposition().getEncodedContent();
            }
        }
        return returnBody;
    }

//    public List<ACLMessage> getAgentAnswers(String reqId) {
//        AsyncDroolsAgentService asyncServicePort = null;
//        if (this.endpointURL == null || this.qname == null) {
//            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
//        } else {
//            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
//        }
//        return asyncServicePort.getResponses(reqId);
//    }
}