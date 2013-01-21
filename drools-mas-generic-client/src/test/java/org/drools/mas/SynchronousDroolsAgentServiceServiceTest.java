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


import org.drools.mas.helpers.DialogueHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author salaboy
 */
public class SynchronousDroolsAgentServiceServiceTest {
    
    // Define the agent endpoint or endpoints, then the helper will use the endpoint to interact with different agents
    private final String endpoint = "http://localhost:8080/new-action-agent/services/?WSDL";
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
    @Ignore
    public void genericTest() {
        // Get the helper 
        DialogueHelper helper = new DialogueHelper(endpoint);
        
        // Interact with the agent :)
       
        // helper.invokeInform("me", "you", );
        
       //  helper.invokeQueryIf("me", "you", );
        
       
        
        
    }

   
   
}
