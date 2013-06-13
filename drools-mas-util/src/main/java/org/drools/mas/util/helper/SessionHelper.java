/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.util.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.drools.agent.KnowledgeAgent;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 *
 * @author Salaboy und Esteban 
 */
public class SessionHelper{
    
    private static SessionHelper INSTANCE = new SessionHelper();
    
    private Map<String, StatefulKnowledgeSession> sessions = new HashMap<String, StatefulKnowledgeSession>();
    private Map<String, KnowledgeAgent> kagents = new HashMap<String, KnowledgeAgent>();
    
    public static SessionHelper getInstance(){
        return INSTANCE;
    }

    public StatefulKnowledgeSession getSession(String key) {
        return sessions.get(key);
    }
    
    public Set<String> getSessionKeys() {
        return sessions.keySet();
    }

    public void registerSession(String key, StatefulKnowledgeSession ksession, KnowledgeAgent kagent) {
        sessions.put(key, ksession);
        kagents.put(key, kagent);
    }
    
    public StatefulKnowledgeSession unregisterSession(String key) {
        kagents.remove(key);
        return sessions.remove(key);
    }
    
    public KnowledgeAgent getKagent(String key) {
        return kagents.get(key);
    }
    
    public Set<String> getKeys() {
        return sessions.keySet();
    }

    public void dispose(){
        for (KnowledgeAgent knowledgeAgent : kagents.values()) {
            knowledgeAgent.dispose();
        }
        this.sessions.clear();
        this.kagents.clear();
    }
}
