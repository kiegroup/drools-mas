package org.drools.mas.helpers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.drools.mas.ACLMessage;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.body.acts.*;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Info;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.drools.runtime.rule.Variable;

public class SynchronousRequestHelper {

    boolean multiReturnValue = false;
    private AbstractMessageBody returnBody;
    private Encodings encode = Encodings.XML;
    private URL endpointURL;
    private QName qname;

    private boolean initialized = false;

    public SynchronousRequestHelper(String url, Encodings enc) {
        try {
            this.endpointURL = new URL(SynchronousDroolsAgentServiceImplService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SynchronousRequestHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.qname = new QName("http://mas.drools.org/", "SynchronousDroolsAgentServiceImplService");
        this.encode = enc;
    }

    public SynchronousRequestHelper(String url) {
        try {
            this.endpointURL = new URL(SynchronousDroolsAgentServiceImplService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SynchronousRequestHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.qname = new QName("http://mas.drools.org/", "SynchronousDroolsAgentServiceImplService");

    }

    public void invokeRequest(String methodName, LinkedHashMap<String, Object> args) throws AgentInteractionException {
        invokeRequest("", "", methodName, args);
    }

    public void invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args) throws AgentInteractionException {


        SynchronousDroolsAgentService synchronousDroolsAgentServicePort = init();

        multiReturnValue = false;
                for (Object o : args.values()) {
                    if (o == Variable.v) {
                        multiReturnValue = true;
                        break;
                    }
                }

        ACLMessageFactory factory = new ACLMessageFactory(encode);

        Action action = MessageContentFactory.newActionContent(methodName, args);
        ACLMessage req = factory.newRequestMessage(sender, receiver, action);

        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(req);

        if ( answers.size() == 0 ) {
            throw new AgentInteractionException( null, null, 
                    "CRITICAL Error - no response received for Request : " + methodName +
                    "(" + args + ") from " + req.getReceiver() );
        } else {
            ACLMessage answer = answers.get( 0 );
            
                                    
            if (!Act.AGREE.equals(answer.getPerformative())) {
                Info failCause = null;
                if ( Act.FAILURE.equals( answer.getPerformative() ) ) {
                    failCause = ((Failure)answer.getBody()).getCause();
                } else if ( Act.NOT_UNDERSTOOD.equals( answer.getPerformative() ) ) {
                    failCause = ((NotUnderstood)answer.getBody()).getCause();
                } else {
                    failCause = new Info( );
                        failCause.setData( "Cause Unknown" );
                }
                throw new AgentInteractionException( answer.getPerformative(), 
                                                    failCause,
                                                    " Request " + methodName + "(" + args + ") from + " +
                                                        req.getReceiver() + " was not agreed back " );    
            }
            
            answer = answers.get( 1 );
            if ( answers.size() < 2 || !Act.INFORM.equals(answers.get(1).getPerformative())) {
                Info failCause = new Info( );                    
                if ( Act.FAILURE.equals( answer.getPerformative() ) ) {
                    failCause = ((Failure)answer.getBody()).getCause();
                } else if ( Act.NOT_UNDERSTOOD.equals( answer.getPerformative() ) ) {
                    failCause = ((NotUnderstood)answer.getBody()).getCause();
                } else {
                    failCause = new Info( );
                        failCause.setData( "Cause Unknown" );
                }
                throw new AgentInteractionException( answer.getPerformative(),
                                                     failCause,
                                                     " Request " + methodName + "(" + args + ") from + " +
                                                        req.getReceiver() + " did not receive an INFORM response : " + failCause );
            }

//            if ( ! multiReturnValue ) {
            returnBody = answer.getBody();
//            } else {
//                returnBody = answers.size() == 2 ? ((InformRef) answers.get(1).getBody()) : null;
//            }
        }

    }

    public void invokeQueryIf(String sender, String receiver, Object proposition) throws AgentInteractionException {
        SynchronousDroolsAgentService synchronousDroolsAgentServicePort = init();

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage qryif = factory.newQueryIfMessage(sender, receiver, proposition);
        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(qryif);

        if ( answers.size() == 0 ) {
            Info failCause = new Info( );
                 failCause.setData( "Cause Unknown" );
            throw new AgentInteractionException( null, failCause, "CRITICAL Error - no response received for QueryIf" );
        } else {
            ACLMessage answer = answers.get( 0 );
            if ( !Act.INFORM_IF.equals(answers.get(1).getPerformative())) {
                Info failCause = new Info( );
                    failCause.setData( "Cause Unknown" );
                throw new AgentInteractionException( answer.getPerformative(), failCause, " Query IF" + proposition + " was not answered back " );
            }

            returnBody = answer.getBody();
        }

    }

    public void invokeInform(String sender, String receiver, Object proposition) {
        SynchronousDroolsAgentService synchronousDroolsAgentServicePort = init();

        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newInformMessage = factory.newInformMessage(sender, receiver, proposition);
        System.out.println("ENDPOINT URL = " + this.endpointURL);
        System.out.println("QNAME = " + this.qname);
        System.out.println("BEFORE CALLING TELL = " + newInformMessage);
        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(newInformMessage);
        System.out.println("AFTER CALLING TELL = " + answers);
        // No Answer needed


    }

    private SynchronousDroolsAgentService init() {
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            try {
                initialized = true;
                return new SynchronousDroolsAgentServiceImplService(this.endpointURL, this.qname).getSynchronousDroolsAgentServiceImplPort();
            } catch ( Exception e ) {
                initialized = false;
            }
        }
        return null;
    }

    public Object getReturn(boolean decode) throws UnsupportedOperationException {
        if (returnBody == null) {
            return null;
        }
        if (decode) {
            MessageContentEncoder.decodeBody(returnBody, encode);
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
        return null;
    }

    public boolean isInitialized() {
        return initialized;
    }
}