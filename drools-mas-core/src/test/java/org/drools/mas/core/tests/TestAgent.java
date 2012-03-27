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

import java.sql.SQLException;
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
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.Variable;
import org.junit.*;

import java.util.*;
import org.drools.mas.ACLMessage;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.core.*;
import org.drools.mas.mappers.MyMapArgsEntryType;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;

import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestAgent {

    private static DroolsAgent mainAgent;
    private static Logger logger = LoggerFactory.getLogger(TestAgent.class);
    private Server server;

    @Before
    public void createAgents() {

        DeleteDbFiles.execute("~", "mydb", false);

        logger.info("Staring DB for white pages ...");
        try {
            
            server = Server.createTcpServer(new String[] {"-tcp","-tcpAllowOthers","-tcpDaemon","-trace"}).start(); 
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
        logger.info("DB for white pages started! ");


        DroolsAgentConfiguration mainConfig = new DroolsAgentConfiguration();
        mainConfig.setAgentId("Mock Test Agent");
        mainConfig.setChangeset("mainTestAgent_changeset.xml");
        DroolsAgentConfiguration.SubSessionDescriptor subDescr1 = new DroolsAgentConfiguration.SubSessionDescriptor("session1", "sub1.xml", "local-mock-test-agent");
        mainConfig.addSubSession(subDescr1);
        DroolsAgentConfiguration.SubSessionDescriptor subDescr2 = new DroolsAgentConfiguration.SubSessionDescriptor("session2", "sub2.xml", "local-mock-test-agent");
        mainConfig.addSubSession(subDescr2);
        mainConfig.setMindNodeLocation("local-mock-test-agent");
        mainConfig.setPort(7000);
        mainAgent = DroolsAgentFactory.getInstance().spawn(mainConfig);
        assertNotNull(mainAgent);

        assertNotNull(mainAgent.getInnerSession("session1"));
        assertNotNull(mainAgent.getInnerSession("session2"));


    }

    @After
    public void cleanUp() {
        
        if (mainAgent != null) {
            mainAgent.dispose();
        }

        logger.info("Stopping DB ...");
        server.stop();
        logger.info("DB Stopped!");
    }

    
    private void waitForAnswers( String id, int expectedSize, long sleep, int maxIters ) {
        int counter = 0;
        do {
            System.out.println( "Answer for " + id + " is not ready, wait... " );
            try {
                Thread.sleep( sleep );
                counter++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while ( mainAgent.getAgentAnswers( id ).size() < expectedSize && counter < maxIters );
        if ( counter == maxIters ) {
            fail( "Timeout waiting for an answer to msg " + id );
        }

    }
    
    
    
    @Test
    public void testSimpleInform() throws InterruptedException {
        MockFact fact = new MockFact( "patient1", 18 );
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage info = factory.newInformMessage( "me", "you", fact );
        mainAgent.tell( info );
        
        //Now this is also async
        waitForAnswers( info.getId(), 0, 250, 50 );
        
        assertNotNull( mainAgent.getAgentAnswers( info.getId() ) );
        StatefulKnowledgeSession target = mainAgent.getInnerSession( "session1" );
        assertTrue( target.getObjects().contains( fact ) );


    }

    @Test
    public void testSimpleConfirmAndDisconfirm() throws InterruptedException {
        MockFact fact = new MockFact( "patient1", 18 );
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage info = factory.newConfirmMessage( "me", "you", fact );
        mainAgent.tell( info );

        //Now this is also async
        waitForAnswers( info.getId(), 0, 250, 50 );

        assertNotNull( mainAgent.getAgentAnswers( info.getId() ) );
        StatefulKnowledgeSession target = mainAgent.getInnerSession( "session1" );
        assertTrue( target.getObjects().contains( fact ) );


        MockFact fact2 = new MockFact( "patient1", 18 );

        ACLMessage info2 = factory.newDisconfirmMessage( "me", "you", fact2 );
        mainAgent.tell( info2 );

        //Now this is also async
        waitForAnswers( info2.getId(), 0, 250, 50 );

        assertNotNull( mainAgent.getAgentAnswers( info2.getId() ) );
        assertFalse( target.getObjects().contains( fact ) );


    }


    @Test
    public void testInformAsTrigger() throws InterruptedException {
        MockFact fact = new MockFact("patient1", 22);
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);


        ACLMessage info = factory.newInformMessage("me", "you", fact);
        mainAgent.tell(info);

        
        waitForAnswers( info.getId(), 0, 250, 50 );
          
        
        assertNotNull( mainAgent.getAgentAnswers(info.getId() ) );
        StatefulKnowledgeSession target = mainAgent.getInnerSession("session2");
        
        for (Object o : target.getObjects()) {
            System.err.println("\t Inform-Trigger test : " + o);
        }
        
        assertTrue(target.getObjects().contains(new Double(22.0)));
        assertTrue(target.getObjects().contains(new Integer(484)));
    }

    

    @Test
    public void testQueryIf() throws InterruptedException {
        MockFact fact = new MockFact("patient1", 18);
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage info = factory.newInformMessage("me", "you", fact);
        mainAgent.tell(info);

        waitForAnswers( info.getId(), 0, 250, 50 );


        ACLMessage qryif = factory.newQueryIfMessage("me", "you", fact);
        assertEquals(0, mainAgent.getAgentAnswers(qryif.getId()).size());


        mainAgent.tell( qryif );

        waitForAnswers( qryif.getId(), 1, 250, 50 );

        assertNotNull( mainAgent.getAgentAnswers( qryif.getId() ) );
        assertEquals( 1, mainAgent.getAgentAnswers( qryif.getId() ).size() );

        ACLMessage answer = mainAgent.getAgentAnswers( qryif.getId() ).get(0);
        MessageContentEncoder.decodeBody( answer.getBody(), answer.getEncoding() );
        assertEquals( Act.INFORM_IF, answer.getPerformative() );
        assertEquals( ((InformIf) answer.getBody() ).getProposition().getData(), fact );
    }

    @Test
    public void testQueryRef() throws InterruptedException {
        MockFact fact = new MockFact("patient1", 18);
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        ACLMessage info = factory.newInformMessage("me", "you", fact);
        mainAgent.tell( info );
        waitForAnswers( info.getId(), 0, 250, 50 );

        Query query = MessageContentFactory.newQueryContent("ageOfPatient", new Object[]{MessageContentHelper.variable("?mock"), "patient1", MessageContentHelper.variable("?age")});
        ACLMessage qryref = factory.newQueryRefMessage("me", "you", query);
        mainAgent.tell( qryref );

        waitForAnswers( qryref.getId(), 2, 250, 50 );

        assertNotNull( mainAgent.getAgentAnswers(qryref.getId() ) );
        assertEquals( 2, mainAgent.getAgentAnswers(qryref.getId() ).size() );

        ACLMessage answer = mainAgent.getAgentAnswers( qryref.getId() ).get(0);
        assertEquals( Act.AGREE, answer.getPerformative() );
        ACLMessage answer2 = mainAgent.getAgentAnswers( qryref.getId() ).get(1);
        assertEquals( Act.INFORM_REF, answer2.getPerformative() );
    }

    @Test
    public void testRequest() throws InterruptedException {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("x", new Double(36));

        Action action = MessageContentFactory.newActionContent("squareRoot", args);
        ACLMessage req = factory.newRequestMessage("me", "you", action);



        mainAgent.tell(req);

        waitForAnswers( req.getId(), 2, 250, 50 );
        
        assertNotNull( mainAgent.getAgentAnswers(req.getId() ) );

        assertEquals( 2, mainAgent.getAgentAnswers( req.getId() ).size() );

        ACLMessage answer = mainAgent.getAgentAnswers( req.getId() ).get(0);
        assertEquals( Act.AGREE, answer.getPerformative() );
        ACLMessage answer2 = mainAgent.getAgentAnswers( req.getId() ).get(1);
        assertEquals( Act.INFORM, answer2.getPerformative() );

        assertTrue( ( (Inform) answer2.getBody() ).getProposition().getEncodedContent().contains( "6.0" ) );

    }




    //leave ignored until we fix the remoting kagent
    @Test
    @Ignore
    public void testRequestWhen() {

        Double in = new Double(36);

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("x", in);


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


        StatefulKnowledgeSession s2 = mainAgent.getInnerSession("session2");
        QueryResults ans = s2.getQueryResults("squareRoot", in, Variable.v);
        assertEquals(1, ans.size());
        assertEquals(6.0, (Double) ans.iterator().next().get("$return"), 1e-6);

    }
    // Leave ignored


    @Test
    @Ignore
    public void testRequestWhenever() {

        Double in = new Double(36);

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("x", in);


        Rule condition = new Rule();
        condition.setDrl("String( this == \"actionTrigger\" || this == \"actionTrigger2\")");

        Action action = MessageContentFactory.newActionContent("squareRoot", args);
        ACLMessage req = factory.newRequestWheneverMessage("me", "you", action, condition);
        mainAgent.tell(req);

        ACLMessage info = factory.newInformMessage("me", "you", new String("actionTrigger"));
        mainAgent.tell(info);


        ACLMessage info2 = factory.newInformMessage("me", "you", new String("actionTrigger2"));
        mainAgent.tell(info2);


        StatefulKnowledgeSession s2 = mainAgent.getInnerSession("session2");
        QueryResults ans = s2.getQueryResults("squareRoot", in, Variable.v);
        assertEquals(2, ans.size());
        Iterator<QueryResultsRow> iter = ans.iterator();
        assertEquals(6.0, (Double) iter.next().get("$return"), 1e-6);
        assertEquals(6.0, (Double) iter.next().get("$return"), 1e-6);


        fail("INCOMPLETE TEST : Needs open queries to send answer back with a message, but keep trigger rule!");

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



        mainAgent.tell(req);
        
        Thread.sleep(5000);
        
        assertNotNull(mainAgent.getAgentAnswers(req.getId()));
        assertEquals(2, mainAgent.getAgentAnswers(req.getId()).size());

        ACLMessage answer = mainAgent.getAgentAnswers(req.getId()).get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        ACLMessage answer2 = mainAgent.getAgentAnswers(req.getId()).get(1);
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




    @Test
    public void testSimpleInformInNewSession() throws InterruptedException {
        MockFact fact = new MockFact( "patient3", 18 );
        MockFact fact2 = new MockFact( "patient3", 44 );

        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        ACLMessage info = factory.newInformMessage( "me", "you", fact );
        
        mainAgent.tell(info);

        waitForAnswers( info.getId(), 0, 2000, 10 );

        System.out.println(" answers: " + mainAgent.getAgentAnswers( info.getId() ) );
        assertEquals( 0, mainAgent.getAgentAnswers( info.getId() ).size() );

        StatefulKnowledgeSession target = mainAgent.getInnerSession( "patient3" );
        assertNotNull( target );
        assertTrue( target.getObjects().contains( fact ) );

        ACLMessage info2 = factory.newInformMessage( "me", "you", fact2 );
        
        mainAgent.tell( info2 );
        //Now this is also async
        waitForAnswers( info.getId(), 0, 250, 50 );
        assertTrue( target.getObjects().contains( fact2 ) );

        StatefulKnowledgeSession target2 = mainAgent.getInnerSession( "session2" );
        assertTrue( target2.getObjects().contains( fact2 ) );
    }



    @Test
    public void testNotUnderstood() throws InterruptedException {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Action action = MessageContentFactory.newActionContent("nonExistingRequest", new LinkedHashMap());
        ACLMessage notUnd = factory.newRequestMessage("me", "you", action);

        mainAgent.tell(notUnd);

        waitForAnswers( notUnd.getId(), 0, 250, 50 );
        
        assertEquals(Act.NOT_UNDERSTOOD, mainAgent.getAgentAnswers(notUnd.getId()).get(0).getPerformative());

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

        waitForAnswers( req.getId(), 0, 250, 50 );
        
        assertEquals( 2, mainAgent.getAgentAnswers(req.getId()).size() );
        assertEquals( Act.AGREE, mainAgent.getAgentAnswers(req.getId()).get(0).getPerformative() );
        assertEquals( Act.FAILURE, mainAgent.getAgentAnswers(req.getId()).get(1).getPerformative() );

        Failure fail = (Failure) mainAgent.getAgentAnswers(req.getId()).get(1).getBody();
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
        
        assertEquals( Act.AGREE, mainAgent.getAgentAnswers(req.getId()).get(0).getPerformative() );
        assertEquals( Act.FAILURE, mainAgent.getAgentAnswers(req.getId()).get(1).getPerformative() );

        Failure fail = (Failure) mainAgent.getAgentAnswers( req.getId() ).get(1).getBody();
        try {
            throw (RuntimeException) fail.getCause().getData();
        } catch (RuntimeException re) {
            System.err.println(re.getMessage());
        }

    }

    @Test
    public void testQueryNotUnderstoodFailure() throws InterruptedException {

        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        Query query = MessageContentFactory.newQueryContent( "queryNonExists", new Object[0] );
        ACLMessage qryref = factory.newQueryRefMessage( "me", "you", query );
        mainAgent.tell( qryref );
        //Now this is also async
        waitForAnswers( qryref.getId(), 1, 250, 50 );

        assertEquals( 1, mainAgent.getAgentAnswers( qryref.getId()).size() );
        assertEquals( Act.NOT_UNDERSTOOD, mainAgent.getAgentAnswers( qryref.getId()).get(0).getPerformative() );

    }

    @Test
    public void testQueryRefFailure() throws InterruptedException {

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        Query query = MessageContentFactory.newQueryContent( "queryExceptional", new Object[]{ "?x" } );
        ACLMessage qryref = factory.newQueryRefMessage( "me", "you", query );
        mainAgent.tell( qryref );
        //Now this is also async
        waitForAnswers( qryref.getId(), 0, 250, 50 );
        
        assertEquals( 2, mainAgent.getAgentAnswers(qryref.getId()).size() );
        assertEquals( Act.AGREE, mainAgent.getAgentAnswers(qryref.getId()).get(0).getPerformative() );
        assertEquals( Act.FAILURE, mainAgent.getAgentAnswers(qryref.getId()).get(1).getPerformative() );

    }
}
