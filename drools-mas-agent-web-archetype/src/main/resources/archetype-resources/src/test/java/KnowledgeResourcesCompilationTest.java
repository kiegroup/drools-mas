/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ${package};

import org.drools.mas.core.DroolsAgent;
import org.drools.mas.helpers.DialogueHelper;
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
         
    }

    @After
    public void tearDown() {
    }
    /*
     * Test for check that the resources provided inside this agent 
     * at least compile without errors. To ensure that the agent can be 
     * initialized correctly
     */
    @Test
    public void compilationTest() throws InterruptedException {
        
        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/applicationContext.xml");
        DroolsAgent agent = (DroolsAgent) context.getBean("agent");
        assertNotNull(agent);
        
        DialogueHelper helper = new DialogueHelper("http://${agent.endpoint.ip}:${agent.endpoint.port}/${agent.name}/services/AsyncAgentService?wsdl");
        
        helper.invokeInform("me", "you", "Hello World!", null);
        
        Thread.sleep(3000);
        
        agent.dispose();
        
    }
}
