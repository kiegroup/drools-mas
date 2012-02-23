package org.drools.mas.helpers;


import org.drools.mas.Act;
import org.drools.mas.body.content.Info;

public class AgentInteractionException extends Exception {
    
    private Act performative;
    
    private Info info;
    
    public AgentInteractionException( Act incomingPerformative, Info cause, String message ) {
        super( message );
        performative = incomingPerformative;
    }

    public Act getPerformative() {
        return performative;
    }

    public Info geInfo() {
        return info;
    }
}
