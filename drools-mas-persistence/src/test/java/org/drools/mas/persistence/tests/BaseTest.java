/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.drools.mas.persistence.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.drools.runtime.rule.QueryResults;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.util.helper.SessionLocator;
import org.drools.runtime.rule.QueryResultsRow;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esteban
 */
public abstract class BaseTest {
    
    @Autowired
    protected DroolsAgent agent;
    
    @Before
    public void doBeforeTest(){
        Assert.assertNotNull("Agent was not correctly injected. Check for errors message in the logs.", agent);
    }
    
    protected List<SessionLocator> getAgentSessions(){
        
        List<SessionLocator> subsessions = new ArrayList<>();
        
        QueryResults results = agent.getMind().getQueryResults("getSessions");
        Iterator<QueryResultsRow> iterator = results.iterator();
        while (iterator.hasNext()) {
            QueryResultsRow queryResultsRow = iterator.next();
            subsessions.add((SessionLocator) queryResultsRow.get("$sessionLocator"));
        }
        
        return subsessions;
    }
    
    protected List<String> getAgentSessionsIds(){
        
        List<String> ids = new ArrayList<>();
        List<SessionLocator> agentSessions = getAgentSessions();
        
        if (agentSessions != null){
            for (SessionLocator sessionLocator : agentSessions) {
                ids.add(sessionLocator.getSessionId());
            }
        }
        
        return ids;
    }
    
    protected void waitForAnswers(String id, int expectedSize, long sleep, int maxIters ) {
        int counter = 0;
        do {
            System.out.println( "Answer for " + id + " is not ready, wait... " );
            try {
                Thread.sleep( sleep );
                counter++;
            } catch (InterruptedException e) {
            }
        } while ( agent.peekAgentAnswers( id ).size() < expectedSize && counter < maxIters );
        if ( counter == maxIters ) {
            fail( "Timeout waiting for an answer to msg " + id );
        }

    }
    
}
