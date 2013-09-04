/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.core.tests;

import mock.MockFact;
import org.drools.ClassObjectFilter;
import org.drools.mas.ACLMessage;
import org.drools.mas.Encodings;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author esteban
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:failure/applicationContextFailure.xml"})
public class AgentFailureTest {
    
    private static final Logger logger = LoggerFactory.getLogger( AgentFailureTest.class );
    
    @Autowired
    private DroolsAgent agent;
    
    @Test
    @DirtiesContext
    public void testRHSFailure(){
        
        Assert.assertNotNull(agent);    
        
        StatefulKnowledgeSession target = agent.getInnerSession("subsession");
        Assert.assertNotNull(target);
        
        //no MockFacts in our session
        Assert.assertEquals(0, countMockFactObjects());
        
        //Ask to insert a new MockFact
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        ACLMessage info = factory.newInformMessage("me", "you", "insert MockFact please 1");
        agent.tell(info);
        waitForAnswers( agent, info.getId(), 0, 2000, 1 );
        Assert.assertEquals(1, countMockFactObjects());
        
        //Ask the session to fail
        info = factory.newInformMessage("me", "you", "fail please");
        agent.tell(info);
        waitForAnswers( agent, info.getId(), 0, 2000, 1 );
 
        //Ask to insert a new MockFact (the session must still be responsible)
        info = factory.newInformMessage("me", "you", "insert MockFact please 2");
        agent.tell(info);
        waitForAnswers( agent, info.getId(), 0, 2000, 1 );
        Assert.assertEquals(2, countMockFactObjects());
        
        
        logger.info("END");
    }
    
    
    @Test
    @DirtiesContext
    public void testLHSFailure(){
        
        Assert.assertNotNull(agent);    
        
        StatefulKnowledgeSession target = agent.getInnerSession("subsession");
        Assert.assertNotNull(target);
        
        //no MockFacts in our session
        Assert.assertEquals(0, countMockFactObjects());
        
        //Ask to insert a new MockFact
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        ACLMessage info = factory.newInformMessage("me", "you", "insert MockFact please 1");
        agent.tell(info);
        waitForAnswers( agent, info.getId(), 0, 2000, 1 );
        Assert.assertEquals(1, countMockFactObjects());
        
        //Insert a fact that makes fail LHS evaluation
        info = factory.newInformMessage("me", "you", new MockFact(null, 14));
        agent.tell(info);
        waitForAnswers( agent, info.getId(), 0, 2000, 1 );
        Assert.assertEquals(2, countMockFactObjects());
        
        //Ask to insert a new MockFact (the session must still be responsible)
        info = factory.newInformMessage("me", "you", "insert MockFact please 2");
        agent.tell(info);
        waitForAnswers( agent, info.getId(), 0, 2000, 1 );
        Assert.assertEquals(3, countMockFactObjects());
        
        
        logger.info("END");
    }

    private void waitForAnswers( DroolsAgent agent, String id, int expectedSize, long sleep, int maxIters ) {
        int counter = 0;
        do {
            System.out.println( "Answer for " + id + " is not ready, wait... " );
            try {
                Thread.sleep( sleep );
                counter++;
            } catch (InterruptedException e) {
            }
        } while ( agent.peekAgentAnswers( id ).size() < expectedSize && counter < maxIters );
    }
    
    private int countMockFactObjects(){
        StatefulKnowledgeSession target = agent.getInnerSession("subsession");
        return target.getObjects(new ClassObjectFilter(MockFact.class)).size();
    }
    
}
