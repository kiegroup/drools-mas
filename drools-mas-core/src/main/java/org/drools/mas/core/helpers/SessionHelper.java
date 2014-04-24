/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.core.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.drools.agent.KnowledgeAgent;
import org.drools.mas.core.SessionManager;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handy class to get {@link SessionManager} references by knowing its id. 
 * Typically, the id of a {@link SessionManager} is the same as the KSession's
 * id it controls.
 * 
 * @author Esteban 
 */
public class SessionHelper{
    
    private static final Logger logger = LoggerFactory.getLogger(SessionHelper.class);
    private static final SessionHelper INSTANCE = new SessionHelper();
    
    private final Map<String, SessionManager> sessionManagers = new HashMap<String, SessionManager>();
    
    private SessionHelper(){
        
    }
    
    public static SessionHelper getInstance(){
        return INSTANCE;
    }

    public void registerSessionManager(String sessionId, SessionManager sessionManager) {
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
    
    public StatefulKnowledgeSession getSession(String sessionId) {
        SessionManager sessionManager = this.getSessionManager(sessionId);
        if (sessionManager == null){
            return null;
        }
        
        return sessionManager.getStatefulKnowledgeSession();
    }
    
    public KnowledgeAgent getKnowledgeAgent(String sessionId) {
        SessionManager sessionManager = this.getSessionManager(sessionId);
        if (sessionManager == null){
            return null;
        }
        
        return sessionManager.getKnowledgeAgent();
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

}
