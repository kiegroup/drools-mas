package org.drools.mas.helpers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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

public class DialogueHelper {

    boolean multiReturnValue = false;
    private AbstractMessageBody returnBody;
    private Encodings encode = Encodings.XML;
    private URL endpointURL;
    private QName qname;

    public DialogueHelper(String url, Encodings enc) {
        try {
            this.endpointURL = new URL(AsyncAgentService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DialogueHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.qname = new QName("http://mas.drools.org/", "AsyncAgentService");
        this.encode = enc;
    }

    public DialogueHelper(String url) {  
        try {
            this.endpointURL = new URL(AsyncAgentService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DialogueHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.qname = new QName("http://mas.drools.org/", "AsyncAgentService");

    }

    public String invokeRequest( String sender, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
            return invokeRequest( sender, "", methodName, args );
        }
    
    public String invokeRequest(String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        return invokeRequest( UUID.randomUUID().toString(), "", methodName, args );
    }

    public String invokeRequest( String sender, String receiver, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException{
        multiReturnValue = false;
        for (Object o : args.values()) {
            if (o == Variable.v) {
                multiReturnValue = true;
                break;
            }
        }
        AsyncDroolsAgentService asyncServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService( this.endpointURL, this.qname ).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory( encode );

        Action action = MessageContentFactory.newActionContent( methodName, args );
        ACLMessage req = factory.newRequestMessage( sender, receiver, action );

        asyncServicePort.tell( req );
//        List<ACLMessage> answers = asyncServicePort.getResponses(req.getId());
        
//        if ( answers.size() == 0 ) { return req.getId(); }
        
//        ACLMessage answer = answers.get(0);
//        if ( ! Act.AGREE.equals( answer.getPerformative() ) ) {
//            throw new UnsupportedOperationException(" Request " + methodName + " was not agreed with args " + args );
//        }
//
//        if ( ! multiReturnValue ) {
//            returnBody = answers.size() == 2 ? ( (Inform) answers.get( 1 ).getBody() ) : null;
//        } else {
//            returnBody = answers.size() == 2 ? ( (InformRef) answers.get( 1 ).getBody() ) : null;
//        }
        return req.getId();
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
        asyncServicePort.tell( qryif );
        List<ACLMessage> answers = asyncServicePort.getResponses( qryif.getId() );

        if ( answers.size() == 0 ) { return qryif.getId(); }

        returnBody = ((InformIf) answers.get(0).getBody());
        return qryif.getId();
        
    }

    public String invokeInform( String sender, String receiver, Object proposition ) {
        AsyncDroolsAgentService asyncServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newInformMessage = factory.newInformMessage(sender, receiver, proposition);
        asyncServicePort.tell( newInformMessage );
        
        return newInformMessage.getId();
    }

    public String invokeConfirm( String sender, String receiver, Object proposition ) {
        AsyncDroolsAgentService asyncServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newConfirmMessage = factory.newConfirmMessage(sender, receiver, proposition);
        asyncServicePort.tell(newConfirmMessage);
        
        return newConfirmMessage.getId();
    }

    public String invokeDisconfirm( String sender, String receiver, Object proposition ) {
        AsyncDroolsAgentService asyncServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newDisconfirmMessage = factory.newDisconfirmMessage(sender, receiver, proposition);
        asyncServicePort.tell( newDisconfirmMessage );
        
        return newDisconfirmMessage.getId();
    }


    public boolean validateRequestResponses( List<ACLMessage> answers ) {
        if ( answers.size() != 2 ) {
            return false;
        }

        if ( Act.AGREE.equals( answers.get( 1 ).getPerformative() ) ) {
            return false;
        }

        ACLMessage answer1 = answers.get( 0 );
        if ( ! Act.AGREE.equals( answer1.getPerformative() ) ) {
            return false;
        }

        ACLMessage answer2 = answers.get( 1 );
        Act act2 = answer2.getPerformative();

        if ( ! ( Act.INFORM.equals( act2 ) || Act.INFORM_REF.equals( act2 ) ) ) {
            return false;
        }
        return true;
    }
    
    
    public Object extractReturn( ACLMessage msg, boolean decode ) throws UnsupportedOperationException {

        if ( msg == null  ) {
            return null;
        }

        AbstractMessageBody returnBody = msg.getBody();
        if ( returnBody == null  ) {
            return null;
        }

        if (decode) {
            MessageContentEncoder.decodeBody( returnBody, encode );
            if (returnBody instanceof Inform) {
                return ((Inform) returnBody).getProposition().getData();
            }
            if (returnBody instanceof InformIf) {
                return ((InformIf) returnBody).getProposition().getData();
            }
        } else {
            if (returnBody instanceof Inform) {
                return ((Inform) returnBody).getProposition().getEncodedContent();
            }
            if (returnBody instanceof InformIf) {
                return ((InformIf) returnBody).getProposition().getData();
            }
        }
        return returnBody;
    }

    public List<ACLMessage> getAgentAnswers( String reqId ) {
        AsyncDroolsAgentService asyncServicePort = null;
        if ( this.endpointURL == null || this.qname == null ) {
            throw new IllegalStateException( "A Web Service URL and a QName Must be Provided for the client to work!" );
        } else {
            asyncServicePort = new AsyncAgentService( this.endpointURL, this.qname ).getAsyncAgentServicePort();
        }
        return asyncServicePort.getResponses( reqId );
    }
}