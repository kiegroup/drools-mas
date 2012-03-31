/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.core.tests;

import java.sql.SQLException;
import java.util.HashMap;
import javax.persistence.Persistence;
import org.drools.SystemEventListenerFactory;
import org.drools.grid.Grid;
import org.drools.grid.GridNode;
import org.drools.grid.GridServiceDescription;
import org.drools.grid.SocketService;
import org.drools.grid.conf.GridPeerServiceConfiguration;
import org.drools.grid.conf.impl.GridPeerConfiguration;
import org.drools.grid.helper.GridHelper;
import org.drools.grid.impl.GridImpl;
import org.drools.grid.impl.MultiplexSocketServerImpl;
import org.drools.grid.io.impl.MultiplexSocketServiceCongifuration;
import org.drools.grid.remote.mina.MinaAcceptorFactoryService;
import org.drools.grid.service.directory.WhitePages;
import org.drools.grid.service.directory.impl.CoreServicesLookupConfiguration;
import org.drools.grid.service.directory.impl.JpaWhitePages;
import org.drools.grid.service.directory.impl.WhitePagesLocalConfiguration;
import org.drools.grid.timer.impl.CoreServicesSchedulerConfiguration;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.util.ACLMessageFactory;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
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
public class SpringAgentTest {

    private static int port1 = 8000;
    private static int port2 = 8010;
    private static Logger logger = LoggerFactory.getLogger(SpringAgentTest.class);
    private static Server server;
    private DroolsAgent agent;

    public SpringAgentTest() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        DeleteDbFiles.execute("~", "mydb", false);
        
        logger.info("Staring DB for white pages ...");
        try {
            server = Server.createTcpServer(new String[] {"-tcp","-tcpAllowOthers","-tcpDaemon","-trace"}).start();
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
        logger.info("DB for white pages started! ");

        GridHelper.reset();
    }

    @AfterClass
    public static void tearDownClass() {
        logger.info("Stopping DB ...");
        try {
            Server.shutdownTcpServer( server.getURL(), "", false, false);
        } catch (SQLException e) {
            e.printStackTrace();
            fail ( e.getMessage() );
        }
        logger.info("DB Stopped!");

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        agent.dispose();
    }

    @Test
    public void helloAgentSmith() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        agent = (DroolsAgent) context.getBean("agent");




    }

    @Test
    public void helloAgentSmithGrid() {


        Grid grid1 = configureGrid(new GridImpl("peer1",new HashMap<String, Object>()), port1);
        final GridNode n1 = grid1.createGridNode("node1");
        grid1.get(SocketService.class).addService("node1", port1, n1);


        Grid grid2 = configureGrid(new GridImpl("peer2",new HashMap<String, Object>()), port2);
        final GridNode n2 = grid2.createGridNode("node2");
        grid2.get(SocketService.class).addService("node2", port2, n2);

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContextGrid.xml");

        agent = (DroolsAgent) context.getBean("agent");

        assertNotNull(agent);

        agent.tell(ACLMessageFactory.getInstance().newInformMessage("", "", new mock.MockFact("asdasd", 12)));


        n1.dispose();
        grid1.get(SocketService.class).close();

        n2.dispose();
        grid2.get(SocketService.class).close();

        

    }

    private Grid configureGrid(Grid grid, int port) {

        //Local Grid Configuration, for our client
        GridPeerConfiguration conf = new GridPeerConfiguration();

        //Configuring the Core Services White Pages
        GridPeerServiceConfiguration coreSeviceWPConf = new CoreServicesLookupConfiguration(new HashMap<String, GridServiceDescription>());
        conf.addConfiguration(coreSeviceWPConf);

        //Configuring the Core Services Scheduler
        GridPeerServiceConfiguration coreSeviceSchedulerConf = new CoreServicesSchedulerConfiguration();
        conf.addConfiguration(coreSeviceSchedulerConf);

        //Configuring the a local WhitePages service
        WhitePagesLocalConfiguration wplConf = new WhitePagesLocalConfiguration();
        wplConf.setWhitePages(new JpaWhitePages(Persistence.createEntityManagerFactory("org.drools.grid")));
        conf.addConfiguration(wplConf);

        if (port >= 0) {
            //Configuring the SocketService
            MultiplexSocketServiceCongifuration socketConf = new MultiplexSocketServiceCongifuration(new MultiplexSocketServerImpl("127.0.0.1",
                    new MinaAcceptorFactoryService(),
                    SystemEventListenerFactory.getSystemEventListener(),
                    grid));
            socketConf.addService(WhitePages.class.getName(), wplConf.getWhitePages(), port);

            conf.addConfiguration(socketConf);
        }
        conf.configure(grid);

        return grid;
    }
}
