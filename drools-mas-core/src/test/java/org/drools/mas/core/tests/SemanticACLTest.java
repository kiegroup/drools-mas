///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.drools.mas.core.tests;
//
//import mock.MockFact;
//import org.drools.mas.ACLMessage;
//import org.drools.mas.Act;
//import org.drools.mas.Encodings;
//import org.drools.mas.body.acts.InformIf;
//import org.drools.mas.core.DroolsAgent;
//import org.drools.mas.core.DroolsAgentConfiguration;
//import org.drools.mas.core.DroolsAgentFactory;
//import org.drools.mas.mock.MockResponseInformer;
//import org.drools.mas.util.ACLMessageFactory;
//import org.drools.mas.util.MessageContentEncoder;
//import org.drools.runtime.StatefulKnowledgeSession;
//import org.junit.*;
//import static org.junit.Assert.*;
//
///**
// *
// * @author salaboy
// */
//public class SemanticACLTest {
//
//    private static DroolsAgent mainAgent;
//    private static DroolsAgent clientAgent;
//    private static MockResponseInformer mainResponseInformer;
//    private static MockResponseInformer clientResponseInformer;
//
//    public SemanticACLTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//
//    @Before
//    public void setUp() {
//        mainResponseInformer = new MockResponseInformer();
//        clientResponseInformer = new MockResponseInformer();
//
//        DroolsAgentConfiguration mainConfig = new DroolsAgentConfiguration();
//        mainConfig.setAgentId("Mock Test Agent");
//        mainConfig.setChangeset("mainTestAgent_changeset.xml");
//        mainConfig.setResponseInformer(mainResponseInformer);
//        DroolsAgentConfiguration.SubSessionDescriptor subDescr1 = new DroolsAgentConfiguration.SubSessionDescriptor("session1", "sub1.xml", "NOT_USED_YET");
//        mainConfig.addSubSession(subDescr1);
//        DroolsAgentConfiguration.SubSessionDescriptor subDescr2 = new DroolsAgentConfiguration.SubSessionDescriptor("session2", "sub2.xml", "NOT_USED_YET");
//        mainConfig.addSubSession(subDescr2);
//        mainAgent = DroolsAgentFactory.getInstance().spawn(mainConfig);
//        assertNotNull(mainAgent);
//        assertNotNull(mainAgent.getInnerSession("session1"));
//        assertNotNull(mainAgent.getInnerSession("session2"));
//
//        DroolsAgentConfiguration clientConfig = new DroolsAgentConfiguration();
//        clientConfig.setAgentId("Humble Test Client");
//        clientConfig.setChangeset("clientTestAgent_changeset.xml");
//        clientConfig.setResponseInformer(clientResponseInformer);
//        clientAgent = DroolsAgentFactory.getInstance().spawn(clientConfig);
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    /*
//     * The sender informs the receiver that a given proposition is true.
//     */
//    @Test
//    public void informACLMessageTest() {
//        // In this case the MockFact represent the proposition
//        MockFact fact = new MockFact("patient1", 18);
//
//        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
//
//        ACLMessage info = factory.newInformMessage("me", "you", fact);
//
//        mainAgent.tell(info);
//
//        assertNull(mainResponseInformer.getResponses(info));
//
//        StatefulKnowledgeSession target = mainAgent.getInnerSession("session1");
//
//        assertTrue(target.getObjects().contains(fact));
//
//    }
//    
//    /*
//     * The action of asking another agent whether or not a given proposition is true.
//     */
//    @Test
//    public void testQueryIf() {
//        MockFact fact = new MockFact("patient1", 18);
//        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);
//        
//        ACLMessage info = factory.newInformMessage("me", "you", fact);
//        // First we inform a new proposition to the main agent
//        mainAgent.tell(info);
//
//        ACLMessage qryif = factory.newQueryIfMessage("me", "you", fact);
//        assertNull(mainResponseInformer.getResponses(qryif));
//        // Now we query for that proposition
//        mainAgent.tell(qryif);
//
//
//        assertNotNull(mainResponseInformer.getResponses(qryif));
//        assertEquals(1, mainResponseInformer.getResponses(qryif).size());
//
//        ACLMessage answer = mainResponseInformer.getResponses(qryif).get(0);
//        MessageContentEncoder.decodeBody(answer.getBody(), answer.getEncoding());
//        assertEquals(Act.INFORM_IF, answer.getPerformative());
//        assertEquals(((InformIf) answer.getBody()).getProposition().getData(), fact);
//    }
//}
