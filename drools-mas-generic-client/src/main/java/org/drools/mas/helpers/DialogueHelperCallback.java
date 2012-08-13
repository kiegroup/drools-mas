/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.helpers;

import java.util.List;
import org.drools.mas.ACLMessage;

/**
 *
 * @author esteban
 */
public interface DialogueHelperCallback {
    
    void onSuccess(List<ACLMessage> messages);

    void onError(Throwable t);
    
    int getExpectedResponsesNumber();
    
    long getTimeoutForResponses();
    
    long getMinimumWaitTimeForResponses();
}
