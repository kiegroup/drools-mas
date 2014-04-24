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
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;
import org.drools.mas.core.DroolsAgentConfiguration;

/**
 * Extension to DroolsAgentConfiguration that includes configuration parameters
 * for the creation of persistent sessions.
 * @author esteban
 */
public class PersistentDroolsAgentConfiguration extends DroolsAgentConfiguration implements Serializable{

    private EntityManagerFactory entityManagerFactory;
    private TransactionManager txManager;
    
    public PersistentDroolsAgentConfiguration() {
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public TransactionManager getTxManager() {
        return txManager;
    }

    public void setTxManager(TransactionManager txManager) {
        this.txManager = txManager;
    }
    
}
