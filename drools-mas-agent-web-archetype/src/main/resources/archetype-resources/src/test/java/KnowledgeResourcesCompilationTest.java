/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ${package};

import java.util.Properties;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.helpers.DialogueHelper;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author esteban
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/applicationContext.xml"})
public class KnowledgeResourcesCompilationTest {
    private static Logger logger = LoggerFactory.getLogger(KnowledgeResourcesCompilationTest.class);
    private static String agentUrl;
    
    @Autowired
    private DroolsAgent agent;

    public KnowledgeResourcesCompilationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Properties p = new Properties();
        p.load(KnowledgeResourcesCompilationTest.class.getResourceAsStream("/agentsConfig.properties"));
        agentUrl = p.getProperty("agent.endpoint.url");
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
    @DirtiesContext
    @Test
    public void compilationTest() throws InterruptedException {
        
        assertNotNull(agent);
        
        DialogueHelper helper = new DialogueHelper(agentUrl);
        
        helper.invokeInform("me", "you", "Hello World!", null);
        
        Thread.sleep(3000);
        
        agent.dispose();
        
    }
}
