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

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.drools.mas.examples.emergency.Emergency;
import org.drools.mas.helpers.SynchronousRequestHelper;
import org.drools.mas.mock.MockFact;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author salaboy
 */
public class SynchronousDroolsAgentServiceServiceTest {

    private String endpoint = "http://localhost:8080/emergency-agent/services/SyncAgentService?WSDL";

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
    @Ignore("Needs upgrade to use latest version of components")
    public void testSimpleInformWithHelper() {
        SynchronousRequestHelper agentHelper = new SynchronousRequestHelper(endpoint);

        MockFact fact = new MockFact("patient1", 18);


        agentHelper.invokeInform("me", "you", fact);

        Object result = agentHelper.getReturn(true);
        assertNull(result);



    }

    @Test
    @Ignore("Needs upgrade to use latest version of components")
    public void informAgentAboutEmergency() {

        SynchronousRequestHelper helper = new SynchronousRequestHelper(endpoint);


        Emergency e = new Emergency("FirstEmergency", new Date(), "Fire", 10);

        //Agent meet the Fire Emergency
        helper.invokeInform("me", "you", e);

        assertNull(helper.getReturn(false));

        // Let's see if you know about a Fire Emergency
        helper.invokeQueryIf("me", "you", e);

        assertEquals(e, helper.getReturn(true));


    }

    @Test
    @Ignore("Needs upgrade to use latest version of components")
    public void helpMeWithMyEmergency() {
        SynchronousRequestHelper helper = new SynchronousRequestHelper(endpoint);


        Emergency e = new Emergency("SecondEmergency", new Date(), "Fire", 10);

        //Agent meet the Fire Emergency
        helper.invokeInform("me", "you", e);

        assertNull(helper.getReturn(false));

        // Let's see if you know about a Fire Emergency
        helper.invokeQueryIf("me", "you", e);

        assertEquals(e, helper.getReturn(true));

        helper.invokeRequest("coordinateEmergency", new LinkedHashMap<String, Object>());

        helper.getReturn(true);

    }

    @Test
    @Ignore("Needs upgrade to use latest version of components")
    public void multiThreadTest() throws InterruptedException {
        final SynchronousRequestHelper helper = new SynchronousRequestHelper(endpoint);
        final int EMERGENCY_COUNT = 45;
        final int THREAD_COUNT = 10;

        // Create test data and callable tasks
        //
        

        Collection <Callable<Void>> tasks = new ArrayList <Callable<Void>>();
        for (int i = 0; i < EMERGENCY_COUNT; i++){

            // Test data
            final Emergency emergency = new Emergency("Emergency"+i, new Date(), "Fire"+i, 10);

            // Tasks - each task makes exactly one service invocation.
            tasks.add(new Callable<Void>() {
                public Void call() throws Exception {
                     helper.invokeInform("me", "you", emergency);
                     helper.invokeRequest("coordinateEmergency", new LinkedHashMap<String, Object>());
                     return null;
                }
            });
        }
        

        // Execute tasks
        //
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        // invokeAll() blocks until all tasks have run...
        List<Future<Void>> futures = executorService.invokeAll(tasks);
        assertEquals(futures.size(), EMERGENCY_COUNT);




    }
}
