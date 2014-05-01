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
import org.drools.mas.body.content.Action;
import org.drools.mas.persistence.tests.model.MathResponse;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author esteban
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/applicationContextOnePersistentSubSession.xml"})
public class SimplePersistentSubSessionTest extends BaseTest {
    
    /**
     * Nothing persistence-specific here. Just checking that a persistent session
     * works just like a regular one.
     */
    @DirtiesContext
    @Test
    public void testSimplePersistentSubSession(){
        
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        
        Map<String, Object> args = new LinkedHashMap<>();
        Double x = 32.0;
        Double y = 5.0;
        args.put("x", x);
        args.put("y", y);
        
        Action action = MessageContentFactory.newActionContent("add", args);

        ACLMessage req = factory.newRequestMessage("me", "you", action);

        agent.tell( req );

        waitForAnswers( req.getId(), 2, 1000, 50 );

        List<ACLMessage> ans = agent.extractAgentAnswers( req.getId() );

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
     * Tests 2 separate invocations to a persistent sub-session. After the 
     * first invocation, the session is persisted and disposed. 
     * The second invocation restores the session from the db, executes what
     * it needs to be executed and disposes the session.
     */
    @DirtiesContext
    @Test
    public void testSimplePersistentSubSession2Invokations(){
        
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        
        //INVOCATION 1
        Map<String, Object> args = new LinkedHashMap<>();
        Double x = 32.0;
        Double y = 5.0;
        args.put("x", x);
        args.put("y", y);
        
        Action action = MessageContentFactory.newActionContent("add", args);
        ACLMessage req = factory.newRequestMessage("me", "you", action);
        agent.tell( req );
        waitForAnswers( req.getId(), 2, 1000, 50 );

        List<ACLMessage> ans = agent.extractAgentAnswers( req.getId() );

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
        
        
        //INVOCATION 2
        args = new LinkedHashMap<>();
        x = 1.0;
        y = 9.0;
        args.put("x", x);
        args.put("y", y);
        
        action = MessageContentFactory.newActionContent("add", args);
        req = factory.newRequestMessage("me", "you", action);
        agent.tell( req );
        waitForAnswers( req.getId(), 2, 1000, 50 );

        ans = agent.extractAgentAnswers( req.getId() );

        assertNotNull(ans);
        assertEquals(2, ans.size());

        answer = ans.get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        answer2 = ans.get(1);
        assertEquals(Act.INFORM, answer2.getPerformative());
        
        MessageContentEncoder.decodeBody(answer2.getBody(), answer2.getEncoding());
        assertEquals(Inform.class, answer2.getBody().getClass());
        assertEquals(MathResponse.class, ((Inform)answer2.getBody()).getProposition().getData().getClass());
        response = (MathResponse) ((Inform)answer2.getBody()).getProposition().getData();

        Assert.assertEquals(x, response.getX(), 0.0001);
        Assert.assertEquals(y, response.getY(), 0.0001);
        Assert.assertEquals(x+y, response.getZ(), 0.0001);
        
        agent.dispose();
        
    }
    
}
