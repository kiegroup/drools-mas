/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.core.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.mas.core.DroolsAgentConfiguration;
import org.drools.mas.core.SessionManager;
import org.drools.mas.core.SessionManagerFactory;
import org.drools.mas.util.LoggerHelper;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handy class to get {@link SessionManager} references by knowing its id. 
 * Typically, the id of a {@link SessionManager} is the same as the KSession's
 * id it controls.
 * 
 * @author Esteban 
 */
public class SessionHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionHelper.class);
    private static final SessionHelper INSTANCE = new SessionHelper();
    
    private final Map<String, SessionManager> sessionManagers = new HashMap<String, SessionManager>();
    
    private SessionHelper() {
        
    }
    
    public static SessionHelper getInstance(){
        return INSTANCE;
    }

    public void registerSessionManager( String sessionId, SessionManager sessionManager ) {
        sessionManagers.put(sessionId, sessionManager);
    }
    
    
    public SessionManager unregisterSessionManager(String sessionId) {
        return sessionManagers.remove(sessionId);
    }
    
    public SessionManager getSessionManager(String sessionId){
        return sessionManagers.get(sessionId);
    }
    
    public Set<String> getSessionKeys() {
        return sessionManagers.keySet();
    }
    
    public KieSession getSession(String sessionId) {
        SessionManager sessionManager = this.getSessionManager(sessionId);
        if (sessionManager == null){
            return null;
        }
        
        return sessionManager.getKieSession();
    }

    public void dispose(){

        for (SessionManager sessionManager : sessionManagers.values()) {
            try{
                sessionManager.finalDispose();
            } catch (Exception e){
                logger.error("Error disposing session '"+sessionManager.getSessionId()+"'", e);
            }
                    
        }
        
        this.sessionManagers.clear();
    }



    public static KieSession createSessionOnTheFly( String sid, DroolsAgentConfiguration conf ) {
        /**
         * Get the deault sub-session descriptor, clone it and override its sessionId
         * and nodeId properties.
         **/
        DroolsAgentConfiguration.SubSessionDescriptor dsd = conf.getDefaultSubsessionDescriptor();
        if ( dsd == null ){
            if( LoggerHelper.isErrorEnabled() ){
                LoggerHelper.error("  #### Generating Session: DroolsAgentConfiguration (" + conf + ") doesn't specify any default sub-session descriptor to use!");
            }
            throw new IllegalStateException("Unable to dynamically create a sub-session: DroolsAgentConfiguration (" + conf + ") doesn't specify any default sub-session descriptor to use!");
        }
        dsd = dsd.makeClone();
        dsd.setSessionId( sid );
        if( LoggerHelper.isDebugEnabled() ){
            LoggerHelper.debug("  !#!@#!@#!@#!@#### Generating Session: '" + sid + "' with conf: "+ dsd );
        }

        String kBaseId;
        if ( dsd.isMutable() ) {
            String kid = dsd.getKieBaseId() != null ? dsd.getKieBaseId() : conf.getDefaultSubsessionKieBaseId();
            return createDedicatedKieSession( sid, kid, conf, dsd );
        } else {
            kBaseId = dsd.getKieBaseId();

            return SessionManagerFactory.createSessionManager( sid,
                                                               kBaseId,
                                                               conf.getSessionManagerClassName(),
                                                               conf,
                                                               dsd ).getKieSession();

        }
    }

    private static KieSession createDedicatedKieSession( String sid, String kieBaseId, DroolsAgentConfiguration conf, DroolsAgentConfiguration.SubSessionDescriptor dsd ) {
        KieServices ks = KieServices.Factory.get();

        KieRepository kieRepository = ks.getRepository();
        KieModule core = kieRepository.getKieModule( ks.newReleaseId( "org.drools.mas", "drools-mas-core", "6.2.0-SNAPSHOT" ) );

        KieContainer cpContainer = ks.getKieClasspathContainer();
        KieModule base = (( KieContainerImpl) cpContainer).getKieProject().getKieModuleForKBase( kieBaseId );

        String id = kieBaseId + "-" + sid;
        ReleaseId rid = ks.newReleaseId( "org.drools.mas", id, "1.0" );

        KieModuleModel model = ks.newKieModuleModel();
            KieBaseModel kbm = model.newKieBaseModel( id );
            kbm.addInclude( kieBaseId );
            kbm.setEqualsBehavior( EqualityBehaviorOption.EQUALITY );
            kbm.setDefault( false );
            kbm.setEventProcessingMode( EventProcessingOption.STREAM );
            kbm.setDeclarativeAgenda( DeclarativeAgendaOption.ENABLED );

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( model.toXML() );
        kfs.generateAndWritePomXML( rid );

        KieBuilder builder = ks.newKieBuilder( kfs );
        if ( base != null ) {
            builder.setDependencies( core, base );
        } else {
            builder.setDependencies( core );
        }
        builder.buildAll();
        KieContainer kieContainer = ks.newKieContainer( builder.getKieModule().getReleaseId() );

        return SessionManagerFactory.createSessionManager( sid,
                                                           kieContainer,
                                                           kfs,
                                                           builder,
                                                           id,
                                                           conf.getSessionManagerClassName(),
                                                           conf,
                                                           dsd ).getKieSession();
    }

}
