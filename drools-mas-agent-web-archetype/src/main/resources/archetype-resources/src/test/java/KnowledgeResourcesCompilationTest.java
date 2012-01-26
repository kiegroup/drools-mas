/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
