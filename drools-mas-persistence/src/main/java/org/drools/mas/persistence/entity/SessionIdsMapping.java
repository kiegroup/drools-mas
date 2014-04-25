/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.drools.mas.persistence.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Mapping between a session configuration id (i.e. the one defined in spring
 * configuration file) and the id of the corresponding session in the database.
 * @author esteban
 */
@Entity
public class SessionIdsMapping implements Serializable{
    @Id
    private String configurationId;
    private Integer databaseId;

    public String getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }
    
}
