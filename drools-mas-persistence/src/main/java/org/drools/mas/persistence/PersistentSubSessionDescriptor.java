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
package org.drools.mas.persistence;

import java.io.Serializable;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import org.drools.mas.core.DroolsAgentConfiguration;

/**
 * Extension to {@link DroolsAgentConfiguration.SubSessionDescriptor} that
 * includes configuration parameters for the creation of persistent
 * sub-sessions.
 *
 * @author esteban
 */
public class PersistentSubSessionDescriptor extends DroolsAgentConfiguration.SubSessionDescriptor implements Serializable {

    /**
     * Given that different configurations could require specific SessionManager
     * implementations that could not be present in compilation time, we need to
     * specify the name of the concrete class we want to use in runtime.
     * Ideally, the relationship between DroolsAgentConfiguration and concrete
     * SessionManager implementations is always 1->1 or N->1.
     */
    private final static String SESSION_MANAGER_CLASS_NAME = "org.drools.mas.persistence.PersistentSessionManager";

    private EntityManagerFactory entityManagerFactory;
    private Object transactionManager;
    
    public PersistentSubSessionDescriptor(String sessionId, String changeset, String nodeId) {
        super(sessionId, changeset, nodeId);
    }

    public PersistentSubSessionDescriptor(String sessionId, String changeset, String nodeId, Map globals) {
        super(sessionId, changeset, nodeId, globals);
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory =  entityManagerFactory;
    }

    public Object getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(Object transactionManager) throws Exception {
        this.transactionManager = transactionManager;
    }

    @Override
    public String getSessionManagerClassName() {
        return SESSION_MANAGER_CLASS_NAME;
    }

    @Override
    public String toString() {
        return "SubSessionDescriptor{" + "sessionId=" + getSessionId() + ", changeset=" + getChangeset() + ", nodeId=" + getNodeId() + '}';
    }

}
