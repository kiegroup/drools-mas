package org.drools.mas.helpers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import org.drools.mas.ACLMessage;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.body.acts.AbstractMessageBody;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.body.acts.InformIf;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Query;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.kie.api.runtime.rule.Variable;

public class DialogueHelper {

    public static int WSDL_RETRIEVAL_TIMEOUT = 2000;
    public static int EXECUTOR_SERVICE_THREAD_NUMBER = 5;
    
    private int connectionTimeout = 0;
    private int receiveTimeout = 0;
    
    boolean multiReturnValue = false;
    private Encodings encode = Encodings.XML;
    private URL endpointURL;
    private QName qname;
    
    private ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_SERVICE_THREAD_NUMBER);
    
    protected static interface DialogueHelperCommand{
        void execute();
    }
    
    private DialogueHelperCallback defaultDialogueHelperCallback = new DialogueHelperCallbackImpl(){

        @Override
        public void onSuccess(List<ACLMessage> messages) {
        }

        @Override
        public void onError(Throwable t) {
            Logger.getLogger(DialogueHelper.class.getName()).log(Level.SEVERE, "Agent invocation failed", t);
        }
        

        @Override
        public long getTimeoutForResponses() {
            return 0;
        }

        @Override
        public long getMinimumWaitTimeForResponses() {
            return 0;
        }
        
    };
    
    public DialogueHelper(String url) {
        this(url, 0);
    }
    
    public DialogueHelper(String url, Encodings enc) {
        this(url, enc, 0);
    }
    
    public DialogueHelper(String url, int wSDLRetrievalTimeout) {
        this(url, null, wSDLRetrievalTimeout);
    }
    
    public DialogueHelper(String url, Encodings enc, int wSDLRetrievalTimeout) {
        try {
            this.endpointURL = new URL(AsyncAgentService.class.getResource("."), url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DialogueHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.qname = new QName("http://mas.drools.org/", "AsyncAgentService");
        
        if (enc != null){
            this.encode = enc;
        }
        
        checkEndpointAvailability(wSDLRetrievalTimeout);
    }

    private void checkEndpointAvailability(int wSDLRetrievalTimeout) {
        if (wSDLRetrievalTimeout <= 0){
            if (WSDL_RETRIEVAL_TIMEOUT <= 0){
                return;
            }
            wSDLRetrievalTimeout = WSDL_RETRIEVAL_TIMEOUT;
        }
        
        try {
            URLConnection openConnection;
            openConnection = this.endpointURL.openConnection();
            openConnection.setConnectTimeout(wSDLRetrievalTimeout);
            openConnection.connect();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    

    public String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException {
        return this.doRequest(sender, receiver, methodName, args, callback);
    }
    
    public String invokeRequest(String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException {
        return invokeRequest(UUID.randomUUID().toString(), "", methodName, args, callback);
    }
    
    /**
     * 
     * @param sender
     * @param methodName
     * @param args
     * @return
     * @throws UnsupportedOperationException
     * @deprecated Without using a DialogueHelperCallback there is no way to 
     * be notified about exceptions in the service invocation.
     */
    @Deprecated
    public String invokeRequest(String sender, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        return invokeRequest(sender, "", methodName, args);
    }

    /**
     * 
     * @param methodName
     * @param args
     * @return
     * @throws UnsupportedOperationException
     * @deprecated Without using a DialogueHelperCallback there is no way to 
     * be notified about exceptions in the service invocation.
     */
    @Deprecated
    public String invokeRequest(String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        return invokeRequest(UUID.randomUUID().toString(), "", methodName, args);
    }
    
    /**
     * 
     * @param sender
     * @param receiver
     * @param methodName
     * @param args
     * @return
     * @throws UnsupportedOperationException
     * @deprecated Without using a DialogueHelperCallback there is no way to 
     * be notified about exceptions in the service invocation.
     */
    @Deprecated
    public String invokeRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args) throws UnsupportedOperationException {
        return this.doRequest(sender, receiver, methodName, args, null);
    }
    
    protected String doRequest(String sender, String receiver, String methodName, LinkedHashMap<String, Object> args, DialogueHelperCallback callback) throws UnsupportedOperationException {
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

        this.tell(req, callback, callback != null);

        return req.getId();
    }

    public String invokeQueryIf(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return this.doQueryIf(sender, receiver, proposition, callback);
    }
    
    /**
     * 
     * @param sender
     * @param receiver
     * @param proposition
     * @return
     * @deprecated Without using a DialogueHelperCallback there is no way to 
     * be notified about exceptions in the service invocation.
     */
    @Deprecated
    public String invokeQueryIf(String sender, String receiver, Object proposition) {
        return this.doQueryIf(sender, receiver, proposition, null);
    }
    
    protected String doQueryIf(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        ACLMessage qryif = factory.newQueryIfMessage(sender, receiver, proposition);

        this.tell(qryif, callback, callback != null);
        
        return qryif.getId();

    }
    
    
    public String invokeQueryRef(String sender, String receiver, Query query, DialogueHelperCallback callback) {
        return this.doQueryRef(sender, receiver, query, callback);
    }
    
    protected String doQueryRef(String sender, String receiver, Query query, DialogueHelperCallback callback) {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        ACLMessage qryRef = factory.newQueryRefMessage(sender, receiver, query);

        this.tell(qryRef, callback, callback != null);
        
        return qryRef.getId();

    }

    public String invokeInform(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return this.doInform(sender, receiver, proposition, callback);
    }

    /**
     * 
     * @param sender
     * @param receiver
     * @param proposition
     * @return
     * @deprecated Without using a DialogueHelperCallback there is no way to 
     * be notified about exceptions in the service invocation.
     */
    @Deprecated
    public String invokeInform(String sender, String receiver, Object proposition) {
        return this.doInform(sender, receiver, proposition, null);
    }

    protected String doInform(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newInformMessage = factory.newInformMessage(sender, receiver, proposition);

        this.tell(newInformMessage, callback, callback != null);
        
        return newInformMessage.getId();
    }

    public String invokeConfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return this.doConfirm(sender, receiver, proposition, callback);
    }

    /**
     * 
     * @param sender
     * @param receiver
     * @param proposition
     * @return
     * @deprecated Without using a DialogueHelperCallback there is no way to 
     * be notified about exceptions in the service invocation.
     */
    @Deprecated
    public String invokeConfirm(String sender, String receiver, Object proposition) {
        return this.doConfirm(sender, receiver, proposition, null);
    }
    
    protected String doConfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newConfirmMessage = factory.newConfirmMessage(sender, receiver, proposition);

        this.tell(newConfirmMessage, callback, callback != null);
        
        return newConfirmMessage.getId();
    }

    public String invokeDisconfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        return this.doDisconfirm(sender, receiver, proposition, callback);
    }

    /**
     * 
     * @param sender
     * @param receiver
     * @param proposition
     * @return
     * @deprecated Without using a DialogueHelperCallback there is no way to 
     * be notified about exceptions in the service invocation.
     */
    @Deprecated
    public String invokeDisconfirm(String sender, String receiver, Object proposition) {
        return this.doDisconfirm(sender, receiver, proposition, null);
    }
    
    protected String doDisconfirm(String sender, String receiver, Object proposition, DialogueHelperCallback callback) {
        ACLMessageFactory factory = new ACLMessageFactory(encode);
        ACLMessage newDisconfirmMessage = factory.newDisconfirmMessage(sender, receiver, proposition);

        this.tell(newDisconfirmMessage, callback, callback != null);
        
        return newDisconfirmMessage.getId();
    }

    public boolean validateRequestResponses(List<ACLMessage> answers) {
        if (answers.size() != 2) {
            return false;
        }

        if (Act.AGREE.equals(answers.get(1).getPerformative())) {
            return false;
        }

        ACLMessage answer1 = answers.get(0);
        if (!Act.AGREE.equals(answer1.getPerformative())) {
            return false;
        }

        ACLMessage answer2 = answers.get(1);
        Act act2 = answer2.getPerformative();

        if (!(Act.INFORM.equals(act2) || Act.INFORM_REF.equals(act2))) {
            return false;
        }
        return true;
    }

    public Object extractReturn(ACLMessage msg, boolean decode) throws UnsupportedOperationException {

        if (msg == null) {
            return null;
        }

        AbstractMessageBody returnBody = msg.getBody();
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
        AsyncDroolsAgentService asyncServicePort = this.getAsyncDroolsAgentService();
        return asyncServicePort.getResponses(reqId);
    }

    private AsyncDroolsAgentService getAsyncDroolsAgentService(){
        
        if (this.endpointURL == null || this.qname == null) {
            throw new IllegalStateException("A Web Service URL and a QName Must be Provided for the client to work!");
        } 
        
        AsyncDroolsAgentService asyncServicePort = new AsyncAgentService(this.endpointURL, this.qname).getAsyncAgentServicePort();
        
        if (connectionTimeout > 0){
            ((BindingProvider)asyncServicePort).getRequestContext().put("javax.xml.ws.client.connectionTimeout", String.valueOf(connectionTimeout));
        }
        
        if (receiveTimeout > 0){
            ((BindingProvider)asyncServicePort).getRequestContext().put("javax.xml.ws.client.receiveTimeout", String.valueOf(receiveTimeout));
        }
        
        return asyncServicePort;
        
    }
    
    protected void tell(final ACLMessage message, DialogueHelperCallback callback, final boolean waitForAnswers){
        final DialogueHelperCallback finalCallback = callback != null? callback : this.defaultDialogueHelperCallback;
        
        Logger.getLogger(DialogueHelper.class.getName()).log(Level.INFO, "Preparing tell command for {0} ", message.getId());
        Logger.getLogger(DialogueHelper.class.getName()).log(Level.CONFIG, "Message:\n{0} ", message);
        Runnable runnable = new Runnable() {

            AsyncDroolsAgentService asyncDroolsAgentService = getAsyncDroolsAgentService();
            
            public void run() {
                try{
                    Logger.getLogger(DialogueHelper.class.getName()).log(Level.INFO, "Telling the agent about {0} - START", message.getId());
                    asyncDroolsAgentService.tell(message);
                    Logger.getLogger(DialogueHelper.class.getName()).log(Level.INFO, "Telling the agent about {0} - DONE", message.getId());
                    if (waitForAnswers){
                        List<ACLMessage> results = this.waitForAnswers(message.getId(), finalCallback.getExpectedResponsesNumber(), finalCallback.getMinimumWaitTimeForResponses(), finalCallback.getTimeoutForResponses());
                        finalCallback.onSuccess(results);
                    }
                }catch (Throwable t){
                    finalCallback.onError(t);
                }
            }
            
            private List<ACLMessage> waitForAnswers( String id, int expectedMessagesNumber, long minimumWaitTime, long timeout) throws TimeoutException {
                
                //could be the case that the client is not waiting for any answer.
                //In this case there's no need to invoke the agent to get any response.
                if (expectedMessagesNumber == 0){
                    Logger.getLogger(DialogueHelper.class.getName()).log(Level.INFO, "We are not expecting any answer for '{0}'. Returning.", id);
                    return new ArrayList<ACLMessage>();
                }
                
                List<ACLMessage> answers = new ArrayList<ACLMessage>();
                //avoid infinite waiting loop
                long waitTime = minimumWaitTime <= 0 ? 1 : minimumWaitTime;
                do {
                    try {
                        Logger.getLogger(DialogueHelper.class.getName()).log(Level.INFO, "Answer for {0} is not ready, wait... ", id);
                        Thread.sleep( waitTime );
                    } catch ( InterruptedException ex ) {
                        Logger.getLogger(DialogueHelper.class.getName()).log(Level.WARNING, "Thread could not be put to sleep", ex);
                    }
                    List<ACLMessage> incomingAnswers = asyncDroolsAgentService.getResponses(id);
                    if (incomingAnswers != null){
                        answers.addAll( incomingAnswers );
                    }
                    
                    Logger.getLogger(DialogueHelper.class.getName()).log(Level.INFO, "Answers for {0}: {1} (waitTime= {2}, timeout= {3}, # responsed expected= {4})",new Object[]{ id, answers.size(), waitTime, timeout, expectedMessagesNumber});
                    waitTime *= 2;
                } while ( answers.size() != expectedMessagesNumber && waitTime < timeout );
                
                if (answers.size() < expectedMessagesNumber){
                    throw new TimeoutException("Expecting "+expectedMessagesNumber+" messages for message "+id+" but only received "+answers.size()+" in "+timeout+"ms");
                }
                
                return answers;

            }
            
        };
        
        this.executorService.submit(runnable);
    }
    
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }
}
