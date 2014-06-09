package org.drools.mas.core;

import java.io.IOException;
import org.drools.mas.core.helpers.SessionHelper;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author esteban
 */
public class SessionManagerFactory {

    private static final Logger logger = LoggerFactory.getLogger( SessionManagerFactory.class );
    private static KieContainer cpathKieContainer;

    static {
        cpathKieContainer = KieServices.Factory.get().getKieClasspathContainer();
        if ( cpathKieContainer.verify().hasMessages( Message.Level.ERROR ) ) {
            System.out.println( cpathKieContainer.verify().getMessages().toString() );
            System.exit( -1 );
        }
    }

    public static SessionManager create( DroolsAgentConfiguration conf, DroolsAgentConfiguration.SubSessionDescriptor subDescr ) {
        if ( subDescr == null ) {
            return create( null, conf );
        } else {
            return create( null, conf, subDescr );
        }
    }



    // create mind
    public static SessionManager create( String sessionId,
                                         DroolsAgentConfiguration conf ) {

        String id = conf.getAgentId();
        String kieBaseId = conf.getKieBaseId();
        if ( kieBaseId == null ) {
            kieBaseId = conf.getDefaultKieBaseId();
        }
        String sessionManagerClassName = conf.getSessionManagerClassName();

        if (sessionId != null) {
            id = sessionId;
        }
        return createSessionManager( id, kieBaseId, sessionManagerClassName, conf, null );
    }

    // create subs
    public static SessionManager create( String sessionId,
                                         DroolsAgentConfiguration conf,
                                         DroolsAgentConfiguration.SubSessionDescriptor subDescr ) {
        String id = subDescr.getSessionId();
        String kieBaseId = subDescr.getKieBaseId();
        if ( kieBaseId == null ) {
            kieBaseId = conf.getDefaultSubsessionKieBaseId();
        }
        String sessionManagerClassName = subDescr.getSessionManagerClassName();

        if (sessionId != null) {
            id = sessionId;
        }

        return createSessionManager( id, kieBaseId, sessionManagerClassName, conf, subDescr );
    }

    public static SessionManager createSessionManager( String id,
                                                       String kBaseId,
                                                       String sessionManagerClassName,
                                                       DroolsAgentConfiguration conf,
                                                       DroolsAgentConfiguration.SubSessionDescriptor subDescr ) {
        return createSessionManager( id, cpathKieContainer, null, null, kBaseId, sessionManagerClassName, conf, subDescr );
    }

    public static SessionManager createSessionManager( String id,
                                                       KieContainer container,
                                                       KieFileSystem kfs,
                                                       KieBuilder builder,
                                                       String kBaseId,
                                                       String sessionManagerClassName,
                                                       DroolsAgentConfiguration conf,
                                                       DroolsAgentConfiguration.SubSessionDescriptor subDescr ) {
        try {
            if (sessionManagerClassName == null){
                throw new IllegalArgumentException("No SessionManager concrete implementation class specified for session '" + id + "'");
            }

            SessionManager sessionManager = (SessionManager) Class.forName( sessionManagerClassName ).newInstance();

            boolean isMind = ( subDescr == null );
            sessionManager.init( id, kBaseId, container, kfs, builder, container.getKieBase( kBaseId ), conf, subDescr, isMind );

            SessionHelper.getInstance().registerSessionManager( id, sessionManager );

            return sessionManager;

        } catch ( Exception ise ) {
            logger.error(" FATAL : Could not create a Knowledge Base ");
            ise.printStackTrace();
        }
        return null;
    }


}
