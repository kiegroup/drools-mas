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

import org.drools.mas.examples.emergency.Emergency;
import org.drools.mas.helpers.DialogueHelper;
import org.drools.mas.helpers.SyncDialogueHelper;
import org.drools.mas.mock.MockFact;
import org.junit.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author salaboy
 */
public class EmergencyAgentServiceRemoteTest {

    private String endpoint = "http://localhost:8084/emergency-agent/services/AsyncAgentService?WSDL";

    public EmergencyAgentServiceRemoteTest() {
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
        SyncDialogueHelper agentHelper = new SyncDialogueHelper( endpoint );

        MockFact fact = new MockFact( "patient1", 18 );

        String iid = agentHelper.invokeInform( "me", "you", fact );

    }

    @Test
    public void informAgentAboutEmergency() {
        SyncDialogueHelper helper = new SyncDialogueHelper( endpoint );

        Emergency e = new Emergency( "FirstEmergency", new Date(), "Fire", 10 );

        //Agent meet the Fire Emergency
        helper.invokeInform( "me", "you", e );

        // Let's see if you know about a Fire Emergency
        String qid = helper.invokeQueryIf( "me", "you", e );
        Object ret = helper.getReturn( true );
        assertEquals( e, ret );

    }

    @Test
    public void helpMeWithMyEmergency() {
        SyncDialogueHelper helper = new SyncDialogueHelper( endpoint );

        Emergency e = new Emergency( "SecondEmergency", new Date(), "Fire", 10 );

        //Agent meet the Fire Emergency
        helper.invokeInform("me", "you", e);

        // Let's see if you know about a Fire Emergency
        String qid = helper.invokeQueryIf( "me", "you", e );
        Object ret = helper.getReturn( true );
        assertEquals( e, ret );
        
        String rid = helper.invokeRequest( "coordinateEmergency", new LinkedHashMap<String, Object>() );
        Object rAns = helper.getReturn( true );

        System.out.println( rAns );

    }

    @Test
    public void multiThreadTest() throws InterruptedException {
        final SyncDialogueHelper helper = new SyncDialogueHelper( endpoint );
        final int EMERGENCY_COUNT = 45;
        final int THREAD_COUNT = 10;

        // Create test data and callable tasks
        //
        Collection <Callable<Void>> tasks = new ArrayList <Callable<Void>>();
        for ( int i = 0; i < EMERGENCY_COUNT; i++ ){

            // Test data
            final Emergency emergency = new Emergency( "Emergency" + i, new Date(), "Fire" + i, 10 );

            // Tasks - each task makes exactly one service invocation.
            tasks.add( new Callable<Void>() {
                public Void call() throws Exception {
                     helper.invokeInform( "me", "you", emergency );
                     helper.invokeRequest( "coordinateEmergency", new LinkedHashMap<String, Object>() );
                     helper.getReturn( false );
                     return null;
                }
            });
        }


        // Execute tasks
        //
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        // invokeAll() blocks until all tasks have run...
        List<Future<Void>> futures = executorService.invokeAll( tasks );
        assertEquals( futures.size(), EMERGENCY_COUNT );


    }
}
