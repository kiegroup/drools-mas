/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import org.drools.mas.body.acts.InformIf;
import org.drools.mas.body.content.Action;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.examples.emergency.Emergency;
import org.drools.mas.mock.MockFact;
import org.drools.mas.mock.MockResponseInformer;
import org.drools.mas.util.ACLMessageFactory;
import org.drools.mas.util.MessageContentFactory;
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
public class KnowledgeResourcesCompilationTest {
    private static Logger logger = LoggerFactory.getLogger(KnowledgeResourcesCompilationTest.class);
    private Server server;
    public KnowledgeResourcesCompilationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
         DeleteDbFiles.execute("~", "mydb", false);

        logger.info("Staring DB for white pages ...");
        try {
            server = Server.createTcpServer(new String[] {"-tcp","-tcpAllowOthers","-tcpDaemon","-trace"}).start(); 
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
        logger.info("DB for white pages started! ");
    }

    @After
    public void tearDown() {
        
        logger.info("Stopping DB ...");
        server.stop();
        logger.info("DB Stopped!");
    }
    /*
     * Test for check that the resources provided inside this agent 
     * at least compile without errors. To ensure that the agent can be 
     * initialized correctly
     */
    @Test
    public void compilationTest() {
        
        ApplicationContext context = new ClassPathXmlApplicationContext("test-applicationContext.xml");
        DroolsAgent agent = (DroolsAgent) context.getBean("agent");
        
        assertNotNull(agent);
        
        agent.dispose();
        
    }
    
    @Test
    public void simpleInvokeTest() {
        
        ApplicationContext context = new ClassPathXmlApplicationContext("test-applicationContext.xml");
        DroolsAgent agent = (DroolsAgent) context.getBean("agent");
        
        assertNotNull(agent);
        
        
        MockFact fact = new MockFact("patient1", 18);

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        ACLMessage newInformMessage = factory.newInformMessage("", "", fact);
        
        agent.tell(newInformMessage);
        
        
        agent.dispose();
        
        
    }
    
    @Test
    public void simpleRequestTest(){
        ApplicationContext context = new ClassPathXmlApplicationContext("test-applicationContext.xml");
        DroolsAgent agent = (DroolsAgent) context.getBean("agent");
        
        Emergency e = new Emergency("SecondEmergency", new Date(), "Fire", 10);

        //Agent meet the Fire Emergency
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
        ACLMessage newInformMessage = factory.newInformMessage("", "", e);
        agent.tell(newInformMessage);
        
       
        assertEquals(0,agent.getAgentAnswers(newInformMessage.getId()).size());

        // Let's see if you know about a Fire Emergency
        
        ACLMessage qryif = factory.newQueryIfMessage("", "", e);
        
        agent.tell(qryif);
        assertEquals(Act.INFORM_IF, agent.getAgentAnswers(qryif.getId()).get(0).getPerformative());
        assertTrue(((InformIf)agent.getAgentAnswers(qryif.getId()).get(0).getBody()).getProposition().getEncodedContent().contains("SecondEmergency"));
        
        
        
        //assertEquals(e, helper.getReturn(true));
        Action action = MessageContentFactory.newActionContent("coordinateEmergency", new LinkedHashMap<String, Object>());
        ACLMessage req = factory.newRequestMessage("", "", action);

        agent.tell(req);

        //helper.getReturn(true);
        System.out.println("Answers request = "+agent.getAgentAnswers(req.getId()));
        
    }
}
