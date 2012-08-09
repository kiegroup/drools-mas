/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.helpers;

import java.util.List;
import org.drools.mas.ACLMessage;


public class DialogueHelperCallbackImpl implements DialogueHelperCallback {

    int expectedResponsesNumber = 1;
    
    long timeoutForResponses = 500;
    
    long minimumWaitTimeForResponses = 50;
    
    public void onSuccess(List<ACLMessage> messages) {
    }

    public void onError(Throwable t) {
    }

    public int getExpectedResponsesNumber() {
        return expectedResponsesNumber;
    }

    public void setExpectedResponsesNumber(int expectedResponsesNumber) {
        this.expectedResponsesNumber = expectedResponsesNumber;
    }

    public long getTimeoutForResponses() {
        return timeoutForResponses;
    }

    public void setTimeoutForResponses(long timeoutForResponses) {
        this.timeoutForResponses = timeoutForResponses;
    }

    public long getMinimumWaitTimeForResponses() {
        return minimumWaitTimeForResponses;
    }

    public void setMinimumWaitTimeForResponses(long minimumWaitTimeForResponses) {
        this.minimumWaitTimeForResponses = minimumWaitTimeForResponses;
    }

    
}
