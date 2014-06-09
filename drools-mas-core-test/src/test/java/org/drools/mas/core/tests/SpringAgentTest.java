/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.core.tests;

import org.drools.mas.ACLMessage;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.util.ACLMessageFactory;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertNotNull;

/**
 *
 * @author salaboy
 */
public class SpringAgentTest {

    private static final Logger logger = LoggerFactory.getLogger( SpringAgentTest.class );

    public SpringAgentTest() {

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testGlobalsSetup(){
        ApplicationContext context = new ClassPathXmlApplicationContext( "applicationContextOneNodeGlobals.xml" );

        DroolsAgent agent = (DroolsAgent) context.getBean( "agent" );
        
        Object mainGlobal = agent.getMind().getGlobal("globalString");
        
        Assert.assertEquals("GlObAl StRiNg", mainGlobal);
        
        Object session1Global = agent.getInnerSession("session47").getGlobal("session1GlobalString");
        Assert.assertEquals("TeSt StRiNg", session1Global);
        
        agent.dispose();
    }

    @Test
    public void helloAgentSmithOneNode() {
        spawnAgentSmithAndLayHimToRest( "applicationContextOneNode.xml" );
    }

    @Test
    public void helloAgentSmithManyNodes() {
        spawnAgentSmithAndLayHimToRest( "applicationContext.xml" );
    }


    
    @Test
    public void helloAgentSmithManyNodesRespawn() {

        System.out.println( "Create agent" );
        spawnAgentSmithAndLayHimToRest( "applicationContext.xml" );

        System.out.println( "Recreate agent in same context" );
        spawnAgentSmithAndLayHimToRest( "applicationContext.xml" );

        System.out.println( "Recreate same agent for the third time" );
        spawnAgentSmithAndLayHimToRest( "applicationContext.xml" );

    }

    @Test
    public void helloAgentsSmiths() {
        ApplicationContext context;
        DroolsAgent a1;
        DroolsAgent a2;

        context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
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

        context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
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

        context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
        a1 = (DroolsAgent) context.getBean( "agent" );

        context = new ClassPathXmlApplicationContext( "applicationContextOneNode.xml" );
        a2 = (DroolsAgent) context.getBean( "agent" );

        a1.dispose();

        context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
        a1 = (DroolsAgent) context.getBean( "agent" );

        a1.dispose();
        a2.dispose();

        context = new ClassPathXmlApplicationContext( "applicationContextOneNode.xml" );
        a2 = (DroolsAgent) context.getBean( "agent" );

        context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
        a1 = (DroolsAgent) context.getBean( "agent" );

        a2.dispose();
        a1.dispose();

    }


    @Test
    @Ignore
    public void testNodeSessionSharedHosting() {
        ApplicationContext context;
        DroolsAgent a1;
        DroolsAgent a2;

        context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
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
            }
        } while ( agent.peekAgentAnswers( id ).size() < expectedSize && counter < maxIters );
    }

}
