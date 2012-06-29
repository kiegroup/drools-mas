/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.core.tests;

import java.sql.SQLException;
import java.util.HashMap;
import javax.persistence.Persistence;
import org.drools.SystemEventListenerFactory;
import org.drools.grid.Grid;
import org.drools.grid.GridServiceDescription;
import org.drools.grid.conf.GridPeerServiceConfiguration;
import org.drools.grid.conf.impl.GridPeerConfiguration;
import org.drools.grid.helper.GridHelper;
import org.drools.grid.impl.MultiplexSocketServerImpl;
import org.drools.grid.io.impl.MultiplexSocketServiceConfiguration;
import org.drools.grid.remote.mina.MinaAcceptorFactoryService;
import org.drools.grid.service.directory.WhitePages;
import org.drools.grid.service.directory.impl.CoreServicesLookupConfiguration;
import org.drools.grid.service.directory.impl.JpaWhitePages;
import org.drools.grid.service.directory.impl.WhitePagesLocalConfiguration;
import org.drools.grid.timer.impl.CoreServicesSchedulerConfiguration;
import org.drools.mas.ACLMessage;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.util.ACLMessageFactory;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.*;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salaboy
 */
public class SpringAgentTest {

    private static int port1 = 8000;
    private static int port2 = 8010;
    private static Logger logger = LoggerFactory.getLogger( SpringAgentTest.class );
    private static Server server;

    public SpringAgentTest() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        DeleteDbFiles.execute("~", "mydb", false);
        
        logger.info("Staring DB for white pages ...");
        try {
            server = Server.createTcpServer(new String[] {"-tcp","-tcpAllowOthers","-tcpDaemon","-trace"}).start();
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
        logger.info("DB for white pages started! ");

        GridHelper.reset();
        
        logger.info( "----------------------------------------------------------------------------------------------" );
        logger.info( "PRE-Setup Complete \n\n\n\n\n" );
    }

    @AfterClass
    public static void tearDownClass() {
                
        logger.info("Stopping DB ...");
        try {
            Server.shutdownTcpServer( server.getURL(), "", false, false );
        } catch (SQLException e) {
            e.printStackTrace();
            fail ( e.getMessage() );
        }
        logger.info("DB Stopped!");
        
        logger.info( "----------------------------------------------------------------------------------------------" );
        logger.info( "\n\n\n\n\n Context TORN DOWN" );
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void helloAgentSmithOneNode() {
        spawnAgentSmithAndLayHimToRest( "applicationContextOneNode.xml" );
    }

    @Test
    public void helloAgentSmithManyNodes() {
        spawnAgentSmithAndLayHimToRest( "applicationContextGrid.xml" );
    }


    
    @Test
    public void helloAgentSmithManyNodesRespawn() {

        System.out.println( "Create agent" );
        spawnAgentSmithAndLayHimToRest( "applicationContextGrid.xml" );

        System.out.println( "Recreate agent in same context" );
        spawnAgentSmithAndLayHimToRest( "applicationContextGrid.xml" );

        System.out.println( "Recreate same agent for the third time" );
        spawnAgentSmithAndLayHimToRest( "applicationContextGrid.xml" );

    }

    @Test
    public void helloAgentsSmiths() {
        ApplicationContext context;
        DroolsAgent a1;
        DroolsAgent a2;

        context = new ClassPathXmlApplicationContext( "applicationContextGrid.xml" );
        a1 = (DroolsAgent) context.getBean( "agent" );
        a1.dispose();

        context = new ClassPathXmlApplicationContext( "applicationContextOneNode.xml" );
        a2 = (DroolsAgent) context.getBean( "agent" );
        a2.dispose();

    }


    @Test
    public void helloAgentsSmithsTogether() {
        ApplicationContext context;
        DroolsAgent a1;
        DroolsAgent a2;

        context = new ClassPathXmlApplicationContext( "applicationContextGrid.xml" );
        a1 = (DroolsAgent) context.getBean( "agent" );

        context = new ClassPathXmlApplicationContext( "applicationContextOneNode.xml" );
        a2 = (DroolsAgent) context.getBean( "agent" );

        a1.dispose();
        a2.dispose();
    }



    @Test
    public void helloAgentsSmithsUpAndDown() {
        ApplicationContext context;
        DroolsAgent a1;
        DroolsAgent a2;

        context = new ClassPathXmlApplicationContext( "applicationContextGrid.xml" );
        a1 = (DroolsAgent) context.getBean( "agent" );

        context = new ClassPathXmlApplicationContext( "applicationContextOneNode.xml" );
        a2 = (DroolsAgent) context.getBean( "agent" );

        a1.dispose();

        context = new ClassPathXmlApplicationContext( "applicationContextGrid.xml" );
        a1 = (DroolsAgent) context.getBean( "agent" );

        a1.dispose();
        a2.dispose();

        context = new ClassPathXmlApplicationContext( "applicationContextOneNode.xml" );
        a2 = (DroolsAgent) context.getBean( "agent" );

        context = new ClassPathXmlApplicationContext( "applicationContextGrid.xml" );
        a1 = (DroolsAgent) context.getBean( "agent" );

        a2.dispose();
        a1.dispose();

    }


    @Test
    public void testNodeSessionSharedHosting() {
        ApplicationContext context;
        DroolsAgent a1;
        DroolsAgent a2;

        context = new ClassPathXmlApplicationContext( "applicationContextGrid.xml" );
        a1 = (DroolsAgent) context.getBean( "agent" );

        context = new ClassPathXmlApplicationContext( "applicationContextSharing.xml" );
        a2 = (DroolsAgent) context.getBean( "agent" );


        a2.dispose();

        a1.dispose();
    }




    protected void spawnAgentSmithAndLayHimToRest( String contextFile ) {

        ApplicationContext context = new ClassPathXmlApplicationContext( contextFile );

        logger.info( "\n\n\n\n\n\n\n\n\n**********************************************************************************************" );
        DroolsAgent agent = (DroolsAgent) context.getBean( "agent" );

        assertNotNull( agent );

        ACLMessage imsg = ACLMessageFactory.getInstance().newInformMessage( "", "", new mock.MockFact( "asdasd", 12 ) );
        agent.tell( imsg );

        waitForAnswers( agent, imsg.getId(), 0, 100, 1 );

        agent.dispose();

        logger.info( "**********************************************************************************************\n\n\n\n\n\n\n\n\n" );
    }
    
    
    
    private void waitForAnswers( DroolsAgent agent, String id, int expectedSize, long sleep, int maxIters ) {
        int counter = 0;
        do {
            System.out.println( "Answer for " + id + " is not ready, wait... " );
            try {
                Thread.sleep( sleep );
                counter++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while ( agent.peekAgentAnswers( id ).size() < expectedSize && counter < maxIters );
    }

}
