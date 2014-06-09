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


import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.io.impl.UrlResource;
import org.drools.mas.body.acts.Failure;
import org.drools.mas.body.content.Query;
import org.drools.mas.body.acts.InformIf;
import mock.MockFact;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.util.*;
import org.drools.mas.body.acts.InformRef;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Ref;
import org.drools.mas.body.content.Rule;
import org.junit.*;

import java.util.*;

import org.drools.mas.ACLMessage;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.core.*;
import org.drools.mas.mappers.MyMapArgsEntryType;

import static org.junit.Assert.*;

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


public class TestAgent {

    private static DroolsAgent mainAgent;
    private static final Logger logger = LoggerFactory.getLogger( TestAgent.class );


    @Test
    @Ignore( "Manual use only, test for memory leaks" )
    public void stressTest() throws InterruptedException {
        for ( int j = 0; j < 100; j++ ) {
            if ( j > 0 ) {
                createAgents();
            }
            testRequest();
            mainAgent.dispose();
        }
    }

    @Before
    public void createAgents() {

        Map<String, Object> mainSessionGlobals = new HashMap<String, Object>();
        mainSessionGlobals.put("globalString", "GlObAl StRiNg");
        
        Map<String, Object> session1Globals = new HashMap<String, Object>();
        session1Globals.put("session1GlobalString", "SeSsIoN1 GlObAl StRiNg");
        
        DroolsAgentConfiguration mainConfig = new DroolsAgentConfiguration();
        mainConfig.setAgentId( "Mock Test Agent" );
        mainConfig.setKieBaseId( "mainTestAgent" );
        mainConfig.setGlobals(mainSessionGlobals);
        DroolsAgentConfiguration.SubSessionDescriptor subDescr1 = new DroolsAgentConfiguration.SubSessionDescriptor( "session1", "agent0-kbase1", session1Globals );
        mainConfig.addSubSession( subDescr1 );
        DroolsAgentConfiguration.SubSessionDescriptor subDescr2 = new DroolsAgentConfiguration.SubSessionDescriptor( "session2", "agent0-kbase2" );
        mainConfig.addSubSession( subDescr2 );

        mainAgent = DroolsAgentFactory.getInstance().spawn( mainConfig );
        assertNotNull( mainAgent );

        assertNotNull( mainAgent.getInnerSession( "session1" ) );
        assertNotNull( mainAgent.getInnerSession( "session2" ) );
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
    public void testGlobalsSetup(){
        Object mainGlobal = mainAgent.getMind().getGlobal("globalString");
        
        Assert.assertEquals("GlObAl StRiNg", mainGlobal);
        
        Object session1Global = mainAgent.getInnerSession("session1").getGlobal("session1GlobalString");
        Assert.assertEquals("SeSsIoN1 GlObAl StRiNg", session1Global);
    }

    @Test
    public void testSimpleInform() throws InterruptedException {
        MockFact fact = new MockFact( "patient1", 18 );
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage info = factory.newInformMessage( "me", "you", fact );
        mainAgent.tell( info );

        //Now this is also async
        waitForAnswers( info.getId(), 0, 250, 50 );

        assertNotNull( mainAgent.extractAgentAnswers( info.getId() ) );
        KieSession target = mainAgent.getInnerSession( "session1" );
        assertTrue( target.getObjects().contains( fact ) );

    }

    @Test
    public void testSimpleConfirmAndDisconfirm() throws InterruptedException {
        MockFact fact = new MockFact( "patient1", 18 );
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        ACLMessage info = factory.newConfirmMessage( "me", "you", fact );
        mainAgent.tell( info );

        //Now this is also async
        waitForAnswers( info.getId(), 0, 250, 50 );

        assertNotNull( mainAgent.extractAgentAnswers( info.getId() ) );
        KieSession target = mainAgent.getInnerSession( "session1" );
        assertTrue( target.getObjects().contains( fact ) );


        MockFact fact2 = new MockFact( "patient1", 18 );

        ACLMessage info2 = factory.newDisconfirmMessage( "me", "you", fact2 );
        mainAgent.tell( info2 );

        //Now this is also async
        waitForAnswers( info2.getId(), 0, 250, 50 );

        assertNotNull( mainAgent.extractAgentAnswers( info2.getId() ) );
        assertFalse( target.getObjects().contains( fact ) );

    }


    @Test
    public void testInformAsTrigger() throws InterruptedException {
        MockFact fact = new MockFact("patient1", 22);
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);


        ACLMessage info = factory.newInformMessage("me", "you", fact);
        mainAgent.tell(info);


        waitForAnswers( info.getId(), 0, 250, 50 );


        assertNotNull( mainAgent.extractAgentAnswers( info.getId() ) );
        KieSession target = mainAgent.getMind();

        for (Object o : target.getObjects()) {
            System.err.println("\t Inform-Trigger test : " + o);
        }

        target = mainAgent.getInnerSession("session2");

        assertTrue( target.getObjects().contains( new Double( 22.0 ) ) );
        assertTrue( target.getObjects().contains( new Integer( 484 ) ) );
    }



    @Test
    public void testQueryIf() throws InterruptedException {
        MockFact fact = new MockFact( "patient1", 18 );
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        ACLMessage info = factory.newInformMessage( "me", "you", fact );
        mainAgent.tell( info );

        waitForAnswers( info.getId(), 0, 250, 50 );


        ACLMessage qryif = factory.newQueryIfMessage("me", "you", fact);
        assertEquals( 0, mainAgent.extractAgentAnswers( qryif.getId() ).size() );


        mainAgent.tell( qryif );

        waitForAnswers( qryif.getId(), 1, 250, 50 );

        List<ACLMessage> ans = mainAgent.extractAgentAnswers( qryif.getId() );

        assertNotNull( ans );
        assertEquals( 1, ans.size() );

        ACLMessage answer = ans.get(0);
        MessageContentEncoder.decodeBody( answer.getBody(), answer.getEncoding() );
        assertEquals( Act.INFORM_IF, answer.getPerformative() );
        assertEquals( ((InformIf) answer.getBody() ).getProposition().getData(), fact );
    }

    @Test
    public void testQueryRef() throws InterruptedException {
        MockFact fact = new MockFact( "patient1", 18 );
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        ACLMessage info = factory.newInformMessage( "me", "you", fact );
        mainAgent.tell( info );
        waitForAnswers( info.getId(), 0, 250, 50 );

        Query query = MessageContentFactory.newQueryContent( "ageOfPatient",
                new Object[] { MessageContentHelper.variable( "?mock" ), "patient1", MessageContentHelper.variable( "?age" ) } );
        ACLMessage qryref = factory.newQueryRefMessage( "me", "you", query );
        mainAgent.tell( qryref );

        waitForAnswers( qryref.getId(), 2, 250, 50 );

        List<ACLMessage> ans = mainAgent.extractAgentAnswers( qryref.getId() );

        assertNotNull( ans );
        assertEquals( 2, ans.size() );

        ACLMessage answer = ans.get(0);
        assertEquals( Act.AGREE, answer.getPerformative() );
        ACLMessage answer2 = ans.get(1);
        assertEquals( Act.INFORM_REF, answer2.getPerformative() );

        MessageContentEncoder.decodeBody(answer2.getBody(), answer2.getEncoding());
        assertEquals(InformRef.class, answer2.getBody().getClass());
        Ref ref = ((InformRef) answer2.getBody()).getReferences();
        assertNotNull(ref.getReferences());
        
        boolean containsPatient = false;
        boolean containsAge = false;
        for (MyMapArgsEntryType entry : ref.getReferences()) {
            if (entry.getKey().equals("?mock")) {
                containsPatient = true;
                assertEquals(MockFact.class, entry.getValue().getClass());
                assertEquals(fact.toString(), entry.getValue().toString());
            }
            if (entry.getKey().equals("?age")) {
                containsAge = true;
                assertEquals(Integer.class, entry.getValue().getClass());
                assertEquals(18, entry.getValue());
            }
        }
        assertTrue(containsPatient);
        assertTrue(containsAge);

        System.out.println("ok");
    }

    @Test
    public void testRequest() throws InterruptedException {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("x", new Double(36));

        Action action = MessageContentFactory.newActionContent( "squareRoot", args );
        ACLMessage req = factory.newRequestMessage( "me", "you", action );



        mainAgent.tell( req );

        waitForAnswers( req.getId(), 2, 250, 50 );

        List<ACLMessage> ans = mainAgent.extractAgentAnswers( req.getId() );

        assertNotNull( ans );

        assertEquals( 2, ans.size() );

        ACLMessage answer = ans.get(0);
        assertEquals( Act.AGREE, answer.getPerformative() );
        ACLMessage answer2 = ans.get(1);
        assertEquals( Act.INFORM, answer2.getPerformative() );

        assertTrue( ( (Inform) answer2.getBody() ).getProposition().getEncodedContent().contains( "6.0" ) );

    }

    @Test
    public void testSimpleInformInNewSession() throws InterruptedException {
        MockFact fact = new MockFact( "patient3", 18 );
        MockFact fact2 = new MockFact( "patient3", 44 );

        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        ACLMessage info = factory.newInformMessage( "me", "you", fact );

        mainAgent.tell(info);

        waitForAnswers( info.getId(), 0, 2000, 10 );

        System.out.println(" answers: " + mainAgent.peekAgentAnswers( info.getId() ) );
        assertEquals( 0, mainAgent.extractAgentAnswers( info.getId() ).size() );

        KieSession target = mainAgent.getInnerSession( "patient3" );
        assertNotNull( target );
        assertTrue( target.getObjects().contains( fact ) );

        ACLMessage info2 = factory.newInformMessage( "me", "you", fact2 );

        mainAgent.tell( info2 );
        //Now this is also async
        waitForAnswers( info.getId(), 0, 250, 50 );
        assertTrue( target.getObjects().contains( fact2 ) );

        KieSession target2 = mainAgent.getInnerSession( "session2" );
        assertTrue( target2.getObjects().contains( fact2 ) );
    }



    @Test
    public void testNotUnderstood() throws InterruptedException {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Action action = MessageContentFactory.newActionContent("nonExistingRequest", new LinkedHashMap());
        ACLMessage notUnd = factory.newRequestMessage("me", "you", action);

        mainAgent.tell(notUnd);

        waitForAnswers( notUnd.getId(), 0, 250, 50 );

        assertEquals(Act.NOT_UNDERSTOOD, mainAgent.extractAgentAnswers(notUnd.getId()).get(0).getPerformative());

    }

    @Test
    public void testImplicitRequestFailure() throws InterruptedException {

        Double in = new Double( -9 );

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("x", in);

        Action action = MessageContentFactory.newActionContent("squareRoot", args);
        ACLMessage req = factory.newRequestMessage("me", "you", action);
        mainAgent.tell( req );

        waitForAnswers( req.getId(), 2, 250, 50 );

        List<ACLMessage> ans = mainAgent.extractAgentAnswers(req.getId());

        assertEquals( 2, ans.size() );
        assertEquals( Act.AGREE, ans.get(0).getPerformative() );
        assertEquals( Act.FAILURE, ans.get( 1 ).getPerformative() );

        Failure fail = (Failure) ans.get(1).getBody();
        String msg = fail.getCause().getData().toString();


        assertTrue(  msg.contains( "can't extract the square root of -9" ) );
    }


    @Test
    public void testExplicitRequestFailure() throws InterruptedException {

        Double in = new Double(-9);

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put( "x", in );

        Action action = MessageContentFactory.newActionContent( "squareRoot", args );
        ACLMessage req = factory.newRequestMessage( "me", "you", action );
        mainAgent.tell( req );

        waitForAnswers( req.getId(), 2, 250, 50 );

        List<ACLMessage> ans = mainAgent.extractAgentAnswers(req.getId());

        assertEquals( Act.AGREE, ans.get( 0 ).getPerformative() );
        assertEquals( Act.FAILURE, ans.get( 1 ).getPerformative() );

        Failure fail = (Failure) ans.get( 1 ).getBody();
        String msg = fail.getCause().getData().toString();

        assertTrue( msg.contains( "can't extract the square root of -9" ) );

    }

    @Test
    public void testQueryNotUnderstoodFailure() throws InterruptedException {

        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        Query query = MessageContentFactory.newQueryContent( "queryNonExists", new Object[0] );
        ACLMessage qryref = factory.newQueryRefMessage( "me", "you", query );
        mainAgent.tell( qryref );
        //Now this is also async
        waitForAnswers( qryref.getId(), 1, 250, 50 );

        List<ACLMessage> ans = mainAgent.extractAgentAnswers(qryref.getId());

        assertEquals( 1, ans.size() );
        assertEquals( Act.NOT_UNDERSTOOD, ans.get( 0 ).getPerformative() );

    }

    @Test
    public void testQueryRefFailure() throws InterruptedException {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Query query = MessageContentFactory.newQueryContent( "queryExceptional", new Object[]{ "?x" } );
        ACLMessage qryref = factory.newQueryRefMessage( "me", "you", query );
        mainAgent.tell( qryref );
        //Now this is also async
        waitForAnswers( qryref.getId(), 0, 250, 50 );

        List<ACLMessage> ans = mainAgent.extractAgentAnswers(qryref.getId());

        assertEquals( 2, ans.size() );
        assertEquals( Act.AGREE, ans.get(0).getPerformative() );
        assertEquals( Act.FAILURE, ans.get(1).getPerformative() );

    }


    @Test
    public void testDynamicResourceAdditionToImmutableSubsession() throws Exception{

        KieSession target = mainAgent.getInnerSession( "session1" );

        for (Object o : target.getObjects()) {
            System.err.println("\t Assets BEFORE the inform : " + o);
        }

        Assert.assertFalse(target.getObjects().contains("--------@@   It's alive!!   @@--------------"));

        Assert.assertNull( target.getKieBase().getRule( "org.drools.mas.test", "Test this" ) );

        // resource does not exist, but the test will fail before getting there since the session is immutable
        // the name is used for routing purposes
        Resource cPathRes = KieServices.Factory.get().getResources()
                .newClassPathResource( "testResource.drl" ).setResourceType( ResourceType.DRL );

        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );
        ACLMessage req = factory.newRequestMessage( "me", "you", ResourceActions.newAddResourceAction( cPathRes ) );
        mainAgent.tell(req);

        waitForAnswers( req.getId(), 0, 1000, 50 );

        for (Object o : target.getObjects()) {
            System.err.println("\t Assets AFTER the inform : " + o);
        }

        List<ACLMessage> ans = mainAgent.extractAgentAnswers( req.getId() );
        assertEquals( 2, ans.size() );

        assertEquals( Act.AGREE, ans.get(0).getPerformative() );
        assertEquals( Act.FAILURE, ans.get(1).getPerformative() );

    }

    @Test
    public void testDynamicResourceAddition() throws Exception{

        KieSession target = mainAgent.getInnerSession( "session3" );
        assertNull( target );

        KieResources krs = KieServices.Factory.get().getResources();
        ClassPathResource cPathRes = (ClassPathResource) krs.newClassPathResource( "newResource.drl" ).setResourceType( ResourceType.DRL );
        Resource fsRes = krs.newFileSystemResource( cPathRes.getURL().getPath() );
        assertTrue( cPathRes.getInputStream().available() > 0 );

        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );
        ACLMessage req = factory.newRequestMessage( "me", "you", ResourceActions.newAddResourceAction( fsRes ) );
        mainAgent.tell(req);

        waitForAnswers( req.getId(), 0, 1000, 50 );

        target = mainAgent.getInnerSession( "session3" );

        List<ACLMessage> ans = mainAgent.extractAgentAnswers(req.getId());
        assertEquals( 2, ans.size() );

        assertEquals( Act.AGREE, ans.get(0).getPerformative() );
        assertEquals( Act.INFORM, ans.get(1).getPerformative() );

        //This string comes from newResource.drl
        Assert.assertTrue( target.getObjects().contains("--------@@   It's alive!!   @@--------------") );
        Assert.assertNotNull( target.getKieBase().getRule( "org.drools.mas.test", "Test this") );

    }


}
