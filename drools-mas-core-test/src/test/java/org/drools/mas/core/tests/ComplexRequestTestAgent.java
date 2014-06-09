package org.drools.mas.core.tests;
/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


import mock.MockFact;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.mas.ACLMessage;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.body.acts.Failure;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.body.acts.InformIf;
import org.drools.mas.body.acts.InformRef;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Query;
import org.drools.mas.body.content.Ref;
import org.drools.mas.body.content.Rule;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.core.DroolsAgentConfiguration;
import org.drools.mas.core.DroolsAgentFactory;
import org.drools.mas.core.ResourceActions;
import org.drools.mas.mappers.MyMapArgsEntryType;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.drools.mas.util.MessageContentHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class ComplexRequestTestAgent {

    private static DroolsAgent mainAgent;
    private static final Logger logger = LoggerFactory.getLogger( ComplexRequestTestAgent.class );


    @Before
    public void createAgents() {

        Map<String, Object> mainSessionGlobals = new HashMap<String, Object>();
        mainSessionGlobals.put("globalString", "GlObAl StRiNg");
        
        Map<String, Object> session1Globals = new HashMap<String, Object>();
        session1Globals.put("session1GlobalString", "SeSsIoN1 GlObAl StRiNg");
        
        DroolsAgentConfiguration mainConfig = new DroolsAgentConfiguration();
        mainConfig.setAgentId( "Mock Test Agent" );
        mainConfig.setKieBaseId( "mainTestAgent" );
        mainConfig.setDefaultSubsessionKieBaseId( "agent0-kbase2" );
        mainConfig.setGlobals( mainSessionGlobals );

        mainAgent = DroolsAgentFactory.getInstance().spawn( mainConfig );
        assertNotNull( mainAgent );

    }

    @After
    public void cleanUp() {

        if ( mainAgent != null ) {
            mainAgent.dispose();
        }

    }

    private void waitForAnswers( String id, int expectedSize, long sleep, int maxIters ) {
        int counter = 0;
        do {
            System.out.println( "Answer for " + id + " is not ready, wait... " );
            try {
                Thread.sleep( sleep );
                counter++;
            } catch (InterruptedException e) {
            }
        } while ( mainAgent.peekAgentAnswers( id ).size() < expectedSize && counter < maxIters );
        if ( counter == maxIters ) {
            fail( "Timeout waiting for an answer to msg " + id );
        }

    }

    @Test
    public void testRequestWhen() {

        Double in = new Double(36);

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put( "x", in );

        Rule condition = new Rule();
        condition.setDrl("String( this == \"actionTrigger\" || this == \"actionTrigger2\")");

        Action action = MessageContentFactory.newActionContent("squareRoot", args);
        ACLMessage req = factory.newRequestWhenMessage("me", "you", action, condition);
        mainAgent.tell(req);

        ACLMessage info = factory.newInformMessage("me", "you", new String("actionTrigger"));
        mainAgent.tell(info);


        ACLMessage info2 = factory.newInformMessage("me", "you", new String("actionTrigger2"));
        mainAgent.tell(info2);

        waitForAnswers( req.getId(), 2, 1000, 50 );


        KieSession s2 = mainAgent.getInnerSession("session6");
        QueryResults ans = s2.getQueryResults("squareRoot", in, Variable.v);
        assertEquals(1, ans.size());
        assertEquals(6.0, (Double) ans.iterator().next().get("$return"), 1e-6);

    }


    @Test
    public void testRequestWhenever() {

        Double in = new Double(36);

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put( "x", in );

        Rule condition = new Rule();
        condition.setDrl( "String( this == \"actionTrigger\" || this == \"actionTrigger2\" )" );

        Action action = MessageContentFactory.newActionContent( "squareRoot", args );
        ACLMessage req = factory.newRequestWheneverMessage( "me", "you", action, condition );
        mainAgent.tell( req );

        ACLMessage info = factory.newInformMessage("me", "you", new String( "actionTrigger" ) );
        mainAgent.tell( info );

        ACLMessage info2 = factory.newInformMessage("me", "you", new String( "actionTrigger2" ) );
        mainAgent.tell( info2 );

        KieSession s2 = mainAgent.getInnerSession( "session6" );
        assertNotNull( s2 );

        QueryResults ans = s2.getQueryResults( "squareRoot", in, Variable.v );
        assertEquals( 2, ans.size() );
        Iterator<QueryResultsRow> iter = ans.iterator();
        assertEquals(6.0, (Double) iter.next().get("$return"), 1e-6);
        assertEquals(6.0, (Double) iter.next().get("$return"), 1e-6);

        waitForAnswers( req.getId(), 0, 1000, 50 );

        List<ACLMessage> answers = mainAgent.extractAgentAnswers( req.getId() );
        assertEquals( 3, answers.size() );

        assertEquals( Act.AGREE, answers.get(0).getPerformative() );
        assertEquals( Act.INFORM, answers.get(1).getPerformative() );
        assertEquals( Act.INFORM, answers.get(2).getPerformative() );

        ACLMessage info3 = factory.newInformMessage("me", "you", new String( "actionTrigger2" ) );
        mainAgent.tell( info3 );

        waitForAnswers( req.getId(), 0, 1000, 50 );

        answers.clear();
        answers = mainAgent.extractAgentAnswers( req.getId() );
        assertEquals( 1, answers.size() );
        assertEquals( Act.INFORM, answers.get(0).getPerformative() );

    }  
    
    @Test
    @Ignore // We need to add full unification to the query mechanism in order to work
    public void testRequestWithMultipleOutputs() throws InterruptedException {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        Double x = 32.0;

        args.put("x", x);
        args.put("?y", Variable.v);
        args.put("?inc", Variable.v);


        Action action = MessageContentFactory.newActionContent("randomSum", args);

        ACLMessage req = factory.newRequestMessage("me", "you", action);



        mainAgent.tell( req );

        waitForAnswers( req.getId(), 2, 1000, 50 );

        List<ACLMessage> ans = mainAgent.extractAgentAnswers( req.getId() );

        assertNotNull(ans);
        assertEquals(2, ans.size());

        ACLMessage answer = ans.get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        ACLMessage answer2 = ans.get(1);
        assertEquals(Act.INFORM_REF, answer2.getPerformative());

//        answer2.getBody().decode(answer2.getEncoding());
        MessageContentEncoder.decodeBody(answer2.getBody(), answer2.getEncoding());
        assertEquals(InformRef.class, answer2.getBody().getClass());

        Ref ref = ((InformRef) answer2.getBody()).getReferences();
        assertNotNull(ref.getReferences());
        boolean containsInc = false;
        boolean containsY = false;
        for (MyMapArgsEntryType entry : ref.getReferences()) {
            if (entry.getKey().equals("?inc")) {
                containsInc = true;
            }
            if (entry.getKey().equals("?y")) {
                containsY = true;
            }
        }
        assertTrue(containsInc);
        assertTrue(containsY);
        assertEquals(Double.class, ref.getReferences().get(0).getValue().getClass());
        assertEquals(Double.class, ref.getReferences().get(1).getValue().getClass());

        Double z = (Double) ref.getReferences().get(0).getValue();
        Double y = (Double) ref.getReferences().get(1).getValue();

        assertEquals(y, x + z, 1e-6);

    }





}
