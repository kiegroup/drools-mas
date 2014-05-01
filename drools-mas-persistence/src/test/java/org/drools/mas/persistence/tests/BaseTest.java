/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.drools.mas.persistence.tests;

import org.drools.mas.core.DroolsAgent;
import org.drools.mas.persistence.tests.model.ContainerBean;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import sun.management.resources.agent;

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
