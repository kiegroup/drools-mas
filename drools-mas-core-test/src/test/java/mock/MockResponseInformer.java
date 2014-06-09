/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.mas.ACLMessage;
import org.drools.mas.core.DroolsAgentResponseInformer;

/**
 *
 * @author salaboy
 */
public class MockResponseInformer implements DroolsAgentResponseInformer {

    private static Map<ACLMessage, List<ACLMessage>> responses = new HashMap<ACLMessage, List<ACLMessage>>();

    public synchronized void informResponse(ACLMessage originalMessage, ACLMessage response) {
        if (!responses.containsKey(originalMessage)) {
            responses.put(originalMessage, new ArrayList<ACLMessage>());
        }

        responses.get(originalMessage).add(response);
    }

    public static List<ACLMessage> getResponses(ACLMessage originalMessage) {
        return responses.get(originalMessage);
    }
    
    public static void clearResponses(){
        responses.clear();
    }
}
