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
import org.drools.mas.body.acts.AbstractMessageBody;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.body.acts.InformRef;
import org.drools.mas.body.content.Action;
import org.drools.mas.reduced.client.SynchronousDroolsAgentServiceImpl;
import org.drools.mas.reduced.client.SynchronousDroolsAgentServiceImplService;
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

    public SynchronousRequestHelper(String url, Encodings enc) {
        try {
            this.endpointURL = new URL(SynchronousDroolsAgentServiceImplService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SynchronousRequestHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.qname = new QName("http://drools-fipa-agent.drools.org/", "SynchronousDroolsAgentServiceImplService");
        this.encode = enc;
    }

    public SynchronousRequestHelper(String url) {
        try {
            this.endpointURL = new URL(SynchronousDroolsAgentServiceImplService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SynchronousRequestHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.qname = new QName("http://drools-fipa-agent.drools.org/", "SynchronousDroolsAgentServiceImplService");

    }

    public void invokeRequest(String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        invokeRequest("", "", methodName, args);
    }

    public void invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        multiReturnValue = false;
        for (Object o : args.values()) {
            if (o == Variable.v) {
                multiReturnValue = true;
                break;
            }
        }
        SynchronousDroolsAgentServiceImpl synchronousDroolsAgentServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            //synchronousDroolsAgentServicePort = new SynchronousDroolsAgentServiceImplService().getSynchronousDroolsAgentServiceImplPort();
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            synchronousDroolsAgentServicePort = new SynchronousDroolsAgentServiceImplService(this.endpointURL, this.qname).getSynchronousDroolsAgentServiceImplPort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(encode);

        Action action = MessageContentFactory.newActionContent(methodName, args);
        ACLMessage req = factory.newRequestMessage(sender, receiver, action);

        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(req);

        ACLMessage answer = answers.get(0);
        if (!Act.AGREE.equals(answer.getPerformative())) {
            throw new UnsupportedOperationException(" Request " + methodName + " was not agreed with args " + args);
        }

        if (!multiReturnValue) {
            returnBody = answers.size() == 2 ? ((Inform) answers.get(1).getBody()) : null;
        } else {
            returnBody = answers.size() == 2 ? ((InformRef) answers.get(1).getBody()) : null;
        }

    }

    public void invokeInform(String sender, String receiver, Object proposition) {
        SynchronousDroolsAgentServiceImpl synchronousDroolsAgentServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            //synchronousDroolsAgentServicePort = new SynchronousDroolsAgentServiceImplService().getSynchronousDroolsAgentServiceImplPort();
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            synchronousDroolsAgentServicePort = new SynchronousDroolsAgentServiceImplService(this.endpointURL, this.qname).getSynchronousDroolsAgentServiceImplPort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newInformMessage = factory.newInformMessage(sender, receiver, proposition);
        System.out.println("ENDPOINT URL = "+this.endpointURL);
        System.out.println("QNAME = "+this.qname);
        System.out.println("BEFORE CALLING TELL = "+newInformMessage);
        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(newInformMessage);
        System.out.println("AFTER CALLING TELL = "+answers);
      //  ACLMessage answer = answers.get(0);
       // No Answer needed
       

    }

    public Object getReturn(boolean decode) throws UnsupportedOperationException {
        if (returnBody == null) {
            return null;
        }
        if (decode) {
            MessageContentEncoder.decodeBody(returnBody, encode);
            return ((Inform) returnBody).getProposition().getData();
        } else {
            return ((Inform) returnBody).getProposition().getEncodedContent();
        }
    }
}