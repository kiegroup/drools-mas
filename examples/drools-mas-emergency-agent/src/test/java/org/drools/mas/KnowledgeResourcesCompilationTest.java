/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas;

import org.drools.mas.core.DroolsAgent;
import org.junit.*;
import static org.junit.Assert.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salaboy
 */
public class KnowledgeResourcesCompilationTest {

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
    public void compilationTest() {
        
        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/applicationContext.xml");
        DroolsAgent agent = (DroolsAgent) context.getBean("agent");
        
        assertNotNull(agent);
        
        agent.dispose();
        
    }
}
