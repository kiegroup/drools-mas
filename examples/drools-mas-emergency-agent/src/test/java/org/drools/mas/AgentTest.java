/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;

import org.drools.mas.body.acts.Inform;
import org.drools.mas.body.acts.InformIf;
import org.drools.mas.body.content.Action;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.examples.emergency.Actions;
import org.drools.mas.examples.emergency.Emergency;
import org.drools.mas.mock.MockFact;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentEncoder;
import org.drools.mas.util.MessageContentFactory;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 *
 * @author salaboy
 */
public class AgentTest {

    private static Logger logger = LoggerFactory.getLogger( AgentTest.class );
    private static Server server;
    private DroolsAgent agent;

    public AgentTest() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        DeleteDbFiles.execute( "~", "mydb", false );

        logger.info("Staring DB for white pages ..." );
        try {
            server = Server.createTcpServer( new String[] { "-tcp", "-tcpAllowOthers", "-tcpDaemon", "-trace" } ).start();
        } catch ( SQLException ex ) {
            logger.error(ex.getMessage());
        }
        logger.info( "DB for white pages started! " );
    }

    @AfterClass
    public static void tearDownClass() throws Exception {

        logger.info( "Stopping DB ..." );
        server.stop();
        logger.info( "DB Stopped!" );

    }



    private void waitForResponse( String id, int numExpected ) {
        do {
            try {
                Thread.sleep( 1000 );
                System.out.println( "Waiting for messages, now : " + agent.peekAgentAnswers( id ).size() );
            } catch (InterruptedException e) {
                fail( e.getMessage() );
                e.printStackTrace();
            }
        } while ( agent.peekAgentAnswers( id ).size() < numExpected );
    }

    /*
     * Test for check that the resources provided inside this agent 
     * at least compile without errors. To ensure that the agent can be 
     * initialized correctly
     */
    @Test
    public void testCompilation() {

        ApplicationContext context = new ClassPathXmlApplicationContext( "test-applicationContext.xml" );
        agent = (DroolsAgent) context.getBean( "agent" );

        assertNotNull(agent);

        agent.dispose();

    }

    @Test
    public void testSimpleInvoke() throws InterruptedException {

        ApplicationContext context = new ClassPathXmlApplicationContext( "test-applicationContext.xml" );
        agent = (DroolsAgent) context.getBean( "agent" );

        assertNotNull( agent );


        MockFact fact = new MockFact( "patient1", 18 );

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        ACLMessage newInformMessage = factory.newInformMessage( "", "", fact );

        agent.tell( newInformMessage );

        waitForResponse( newInformMessage.getId(), 0 );

        agent.dispose();


    }

    @Test
    public void testSimpleRequest() {
        ApplicationContext context = new ClassPathXmlApplicationContext( "test-applicationContext.xml" );
        agent = (DroolsAgent) context.getBean( "agent" );

        Emergency e = new Emergency( "SecondEmergency", new Date(), "Fire", 10 );

        //Agent meet the Fire Emergency
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );
        ACLMessage newInformMessage = factory.newInformMessage( "", "", e );
        agent.tell( newInformMessage );

        waitForResponse( newInformMessage.getId(), 0 );

        assertEquals( 0, agent.getAgentAnswers( newInformMessage.getId() ).size() );


        // Let's see if you know about a Fire Emergency


        ACLMessage qryif = factory.newQueryIfMessage( "", "", e );

        agent.tell( qryif );

        waitForResponse( qryif.getId(), 1 );

        assertEquals( Act.INFORM_IF, agent.peekAgentAnswers(qryif.getId()).get( 0 ).getPerformative() );
        assertTrue( ( (InformIf) agent.getAgentAnswers( qryif.getId() ).get( 0 ).getBody() ).getProposition().getEncodedContent().contains( "SecondEmergency" ) );



        //assertEquals(e, helper.getReturn(true));
        Action action = MessageContentFactory.newActionContent( "coordinateEmergency", new LinkedHashMap<String, Object>() );
        ACLMessage req = factory.newRequestMessage( "", "", action );

        agent.tell(req);

        waitForResponse( req.getId(), 2 );

        ACLMessage ans = agent.getAgentAnswers( req.getId() ).get( 1 );
        MessageContentEncoder.decodeBody( ans.getBody(), ans.getEncoding() );


        Actions o = (Actions) ((Inform) ans.getBody()).getProposition().getData();

        assertTrue( o.getActions().contains( "Sending Ambulances" ) );
        assertTrue( o.getActions().contains( "Sending Firefigthers" ) );

        agent.dispose();

    }
}
