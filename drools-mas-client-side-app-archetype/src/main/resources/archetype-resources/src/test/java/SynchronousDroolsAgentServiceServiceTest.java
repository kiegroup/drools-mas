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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas;

import org.drools.mas.mock.MockFact;
import org.drools.mas.helpers.DialogueHelper;
import org.drools.mas.util.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author salaboy
 */
public class SynchronousDroolsAgentServiceServiceTest {

    public SynchronousDroolsAgentServiceServiceTest() {
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

   
   @Test
    public void testSimpleInformWithHelper() {
        DialogueHelper agentHelper = new DialogueHelper("http://localhost:8080/${agent-name}/services/?WSDL");
        
        MockFact fact = new MockFact("patient1", 18);
        

        agentHelper.invokeInform("me", "you", fact);
        
        Object result = agentHelper.getReturn(true);
        assertNull(result);
       


    }
}
