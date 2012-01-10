/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.mas.ACLMessage;
import org.drools.mas.core.DroolsAgentResponseInformer;

/**
 *
 * @author esteban
 */
public class SynchronousDroolsAgentResponseInformer implements DroolsAgentResponseInformer {

    private Map<ACLMessage,List<ACLMessage>> responses = new HashMap<ACLMessage, List<ACLMessage>>();
    
    public synchronized void informResponse(ACLMessage originalMessage, ACLMessage reponse) {
        if (!responses.containsKey(originalMessage)){
            responses.put(originalMessage, new ArrayList<ACLMessage>());
        }
        
        responses.get(originalMessage).add(reponse);
    }
     
    public synchronized List<ACLMessage> retrieveResponses(ACLMessage originalMessage){
        return this.responses.remove(originalMessage);
    }
    
}
