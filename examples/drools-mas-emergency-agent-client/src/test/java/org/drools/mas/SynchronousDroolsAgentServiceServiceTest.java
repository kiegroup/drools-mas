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

import java.util.Date;
import java.util.LinkedHashMap;
import org.drools.mas.examples.emergency.Emergency;
import org.drools.mas.helpers.SynchronousRequestHelper;
import org.drools.mas.mock.MockFact;
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
    private String endpoint = "http://localhost:8080/emergency-agent/services/?WSDL";
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
        SynchronousRequestHelper agentHelper = new SynchronousRequestHelper(endpoint);
        
        MockFact fact = new MockFact("patient1", 18);
        

        agentHelper.invokeInform("me", "you", fact);
        
        Object result = agentHelper.getReturn(true);
        assertNull(result);
       


    }
   
    @Test
    public void informAgentAboutEmergency() {
        
        SynchronousRequestHelper helper = new SynchronousRequestHelper(endpoint);
        
        
        Emergency e = new Emergency("FirstEmergency",new Date(),"Fire",10);
       
        //Agent meet the Fire Emergency
        helper.invokeInform("me", "you", e);
        
        assertNull(helper.getReturn(false));
        
        // Let's see if you know about a Fire Emergency
        helper.invokeQueryIf("me", "you", e);
        
        assertEquals(e, helper.getReturn(true));
        
        
    }

    @Test
    public void helpMeWithMyEmergency(){
        SynchronousRequestHelper helper = new SynchronousRequestHelper(endpoint);
        
        
        Emergency e = new Emergency("SecondEmergency",new Date(),"Fire",10);
       
        //Agent meet the Fire Emergency
        helper.invokeInform("me", "you", e);
        
        assertNull(helper.getReturn(false));
        
        // Let's see if you know about a Fire Emergency
        helper.invokeQueryIf("me", "you", e);
        
        assertEquals(e, helper.getReturn(true));
    
        helper.invokeRequest("coordinateEmergency", new LinkedHashMap<String, Object>());
        
        helper.getReturn(true);
        
    }
}
