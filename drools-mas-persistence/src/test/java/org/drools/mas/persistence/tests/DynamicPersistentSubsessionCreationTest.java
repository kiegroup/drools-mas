/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.drools.mas.persistence.tests;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.drools.mas.ACLMessage;
import org.drools.mas.Encodings;
import org.drools.mas.body.content.Action;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author esteban
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/applicationContextDefaultSubSessionDescriptor.xml"})
public class DynamicPersistentSubsessionCreationTest extends BaseTest {

    /**
     * FIX THIS TEST!
     * For some reason, after the Action is notified, the rule
     * "Generate Session" in acl_content_based_routing.drl gets activated
     * and executed (this is OK), but the execution of that rule is not 
     * activating rule "Route Message" in agent_cbr.drl even if it should.
     * 
     * If we don't wait for any answer and notify a second Action, the 
     * session that was previously created by "Generate Session" rule is 
     * correctly used by "Route Message".
     * 
     * @throws InterruptedException 
     */
    @Test
    public void testSubsessionCreation() throws InterruptedException{
        
        //We start with 2 sessions in the agent: 'mind' and 'subsession1'.
        List<String> agentSessionsIds = this.getAgentSessionsIds();
        Assert.assertEquals(2, agentSessionsIds.size());
        Assert.assertTrue(agentSessionsIds.contains("mind"));
        Assert.assertTrue(agentSessionsIds.contains("subsession1"));
        
        
        //Notify a message to an un-mapped sender. A new sub-session is going
        //to be generated using the default sub-session descriptor configured
        //in the agent's configuration descriptor.
        Map<String, Object> args = new LinkedHashMap<>();
        Double x = 32.0;
        Double y = 5.0;
        args.put("x", x);
        args.put("y", y);
        
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        Action action = MessageContentFactory.newActionContent("add", args);
        ACLMessage req = factory.newRequestMessage("unknown-mapping", "you", action);

        agent.tell( req );
        
        waitForAnswers( req.getId(), 2, 1000, 50 );
        List<ACLMessage> ans = agent.extractAgentAnswers( req.getId() );
        /*Thread.sleep(1000);
        
        //We have 3 sessions now: 'mind', 'subsession1' and 'unknown-mapping'
        agentSessionsIds = this.getAgentSessionsIds();
        Assert.assertEquals(3, agentSessionsIds.size());
        Assert.assertTrue(agentSessionsIds.contains("mind"));
        Assert.assertTrue(agentSessionsIds.contains("subsession1"));
        Assert.assertTrue(agentSessionsIds.contains("unknown-mapping"));
        
        
        
        
        
        factory = new ACLMessageFactory(Encodings.XML);
        action = MessageContentFactory.newActionContent("add", args);
        req = factory.newRequestMessage("unknown-mapping", "you", action);

        agent.tell( req );
        
        waitForAnswers( req.getId(), 2, 1000, 50 );
        List<ACLMessage> ans = agent.extractAgentAnswers( req.getId() );
        
        System.out.println("\n\n\nResults:\n");
        for (ACLMessage aCLMessage : ans) {
            System.out.println(ans);
        }
        */
    }
}
