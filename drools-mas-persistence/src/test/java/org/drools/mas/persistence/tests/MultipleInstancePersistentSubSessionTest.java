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
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.body.acts.InformRef;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Query;
import org.drools.mas.persistence.tests.model.ContainerBean;
import org.drools.mas.persistence.tests.model.MathResponse;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.drools.mas.util.MessageContentHelper;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Combination of 2 tests to prove that persistent sub-sessions survive after
 * the agent is disposed. 
 * The first test generates an object of type MathResponse in the sub-session.
 * Second test then queries this object. The original msgId is kept in a 
 * helper spring bean of type {@link ContainerBean}.
 * 
 * @author esteban
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/applicationContextOnePersistentSubSession.xml"})
public class MultipleInstancePersistentSubSessionTest extends BaseTest {
    
    private Double x = 32.0;
    private Double y = 5.0;
    
    @Autowired
    protected ContainerBean containerBean;
    
    /**
     * The trick here is that this particular test method doesn't use @DirtiesContext.
     * This will allow the second test method to run with the same dataSource
     * (keeping all the information previously created by this method).
     * In applicationContextOnePersistentSubSession.xml, the scope of the
     * Agent is 'prototype'. This ensures that 2 different agent instances
     * are used for the 2 methods. 
     * This must be like this because we are sharing the same context between 
     * the 2 methods (to reuse the data in the database and in conainerBean) but 
     * we want to use different agent instances (to prove the point that 
     * persistent sessions survive after the agent is disposed).
     */
    @Test
    public void testMultipleInstancePersistentSubSessionPart1(){
        
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        
        Map<String, Object> args = new LinkedHashMap<>();
        
        args.put("x", x);
        args.put("y", y);
        
        Action action = MessageContentFactory.newActionContent("add", args);

        ACLMessage req = factory.newRequestMessage("me", "you", action);

        agent.tell( req );

        waitForAnswers( req.getId(), 2, 1000, 50 );

        List<ACLMessage> ans = agent.extractAgentAnswers( req.getId() );

        containerBean.putData("lastMessageId", req.getId());
        System.out.println("Setting lastMessageId to "+req.getId());
        
        assertNotNull(ans);
        assertEquals(2, ans.size());

        ACLMessage answer = ans.get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        ACLMessage answer2 = ans.get(1);
        assertEquals(Act.INFORM, answer2.getPerformative());
        
        MessageContentEncoder.decodeBody(answer2.getBody(), answer2.getEncoding());
        assertEquals(Inform.class, answer2.getBody().getClass());
        assertEquals(MathResponse.class, ((Inform)answer2.getBody()).getProposition().getData().getClass());
        MathResponse response = (MathResponse) ((Inform)answer2.getBody()).getProposition().getData();

        Assert.assertEquals(x, response.getX(), 0.0001);
        Assert.assertEquals(y, response.getY(), 0.0001);
        Assert.assertEquals(x+y, response.getZ(), 0.0001);
        
        agent.dispose();
        
    }
    
    
    /**
     * Make sure you ran the previous test before!
     */
    @DirtiesContext
    @Test
    public void testMultipleInstancePersistentSubSessionPart2(){
        
        String lastMessageId = (String) containerBean.getData("lastMessageId");
        System.out.println("lastMessageId from previous test is "+lastMessageId);
        
        Assert.assertNotNull("'lastMessageId' in containerBean is null. Are you sure you ran the previous test?", lastMessageId);
        
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        
        Query query = MessageContentFactory.newQueryContent("getMathResponses", lastMessageId, MessageContentHelper.variable("$result"));
        ACLMessage req = factory.newQueryRefMessage("me", "you", query);

        agent.tell( req );

        waitForAnswers( req.getId(), 2, 1000, 50 );

        List<ACLMessage> ans = agent.extractAgentAnswers( req.getId() );

        assertNotNull(ans);
        assertEquals(2, ans.size());

        ACLMessage answer = ans.get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        ACLMessage answer2 = ans.get(1);
        assertEquals(Act.INFORM_REF, answer2.getPerformative());
        
        MessageContentEncoder.decodeBody(answer2.getBody(), answer2.getEncoding());
        assertEquals(InformRef.class, answer2.getBody().getClass());
        assertEquals(MathResponse.class, ((InformRef)answer2.getBody()).getReferences().get("$result").getClass());
        MathResponse response = (MathResponse) ((InformRef)answer2.getBody()).getReferences().get("$result");

        Assert.assertEquals(x, response.getX(), 0.0001);
        Assert.assertEquals(y, response.getY(), 0.0001);
        Assert.assertEquals(x+y, response.getZ(), 0.0001);
        
        agent.dispose();
        
    }
    
}
