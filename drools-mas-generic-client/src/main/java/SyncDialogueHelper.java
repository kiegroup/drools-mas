
import org.drools.mas.helpers.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
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

public class SyncDialogueHelper {

    boolean multiReturnValue = false;
    private AbstractMessageBody returnBody;
    private Encodings encode = Encodings.XML;
    private URL endpointURL;
    private QName qname;

    public SyncDialogueHelper(String url, Encodings enc) {
        try {
            this.endpointURL = new URL(AsyncAgentService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DialogueHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.qname = new QName("http://mas.drools.org/", "AsyncAgentService");
        this.encode = enc;
    }

    public SyncDialogueHelper(String url) {
        try {
            this.endpointURL = new URL(AsyncAgentService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DialogueHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.qname = new QName("http://mas.drools.org/", "AsyncAgentService");

    }

    public String invokeRequest(String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        return invokeRequest("", "", methodName, args);
    }

    public String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
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
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(encode);

        Action action = MessageContentFactory.newActionContent(methodName, args);
        ACLMessage req = factory.newRequestMessage(sender, receiver, action);

        asyncServicePort.tell(req);

        List<ACLMessage> answers = asyncServicePort.getResponses(req.getId());
        int waitTime = 100;
        do {
            try {
                System.out.println(" >>> Waiting for answers (" + waitTime + ") for: Request ->  " + methodName);
                Thread.sleep(waitTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(SyncDialogueHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            answers = asyncServicePort.getResponses(req.getId());

            waitTime *= 2;
        } while (answers.size() != 2 && waitTime < 3000);

        ACLMessage answer = answers.get(0);
        if (!Act.AGREE.equals(answer.getPerformative())) {
            throw new UnsupportedOperationException(" Request " + methodName + " was not agreed with args " + args);
        }

        ACLMessage answer2 = answers.get(1);
        if (!Act.INFORM_IF.equals(answer.getPerformative()) && !Act.INFORM_REF.equals(answer.getPerformative())) {
            throw new IllegalStateException(" Request " + methodName + " with args " 
                                        + args + "failed and returned this msg: " +answer2);
        }
        if (!multiReturnValue) {
            returnBody = answers.size() == 2 ? ((Inform) answers.get(1).getBody()) : null;
        } else {
            returnBody = answers.size() == 2 ? ((InformRef) answers.get(1).getBody()) : null;
        }
        return req.getId();
    }

    public String invokeQueryIf(String sender, String receiver, Object proposition) {
        AsyncDroolsAgentService asyncServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage qryif = factory.newQueryIfMessage(sender, receiver, proposition);
        asyncServicePort.tell(qryif);
        List<ACLMessage> answers = asyncServicePort.getResponses(qryif.getId());
        System.out.println("AFTER CALLING TELL = " + answers);

        returnBody = ((InformIf) answers.get(0).getBody());
        return qryif.getId();

    }

    public String invokeInform(String sender, String receiver, Object proposition) {
        AsyncDroolsAgentService asyncServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newInformMessage = factory.newInformMessage(sender, receiver, proposition);
        System.out.println("ENDPOINT URL = " + this.endpointURL);
        System.out.println("QNAME = " + this.qname);
        System.out.println("BEFORE CALLING TELL = " + newInformMessage);
        asyncServicePort.tell(newInformMessage);

        return newInformMessage.getId();


    }

    public String invokeConfirm(String sender, String receiver, Object proposition) {
        AsyncDroolsAgentService asyncServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newConfirmMessage = factory.newConfirmMessage(sender, receiver, proposition);
        System.out.println("ENDPOINT URL = " + this.endpointURL);
        System.out.println("QNAME = " + this.qname);
        System.out.println("BEFORE CALLING TELL = " + newConfirmMessage);
        asyncServicePort.tell(newConfirmMessage);

        return newConfirmMessage.getId();


    }

    public String invokeDisconfirm(String sender, String receiver, Object proposition) {
        AsyncDroolsAgentService asyncServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newDisconfirmMessage = factory.newDisconfirmMessage(sender, receiver, proposition);
        System.out.println("ENDPOINT URL = " + this.endpointURL);
        System.out.println("QNAME = " + this.qname);
        System.out.println("BEFORE CALLING TELL = " + newDisconfirmMessage);
        asyncServicePort.tell(newDisconfirmMessage);

        return newDisconfirmMessage.getId();


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
        return returnBody;
    }

    public List<ACLMessage> getAgentAnswers(String reqId) {
        AsyncDroolsAgentService asyncServicePort = null;
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } else {
            asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        }
        return asyncServicePort.getResponses(reqId);
    }
}