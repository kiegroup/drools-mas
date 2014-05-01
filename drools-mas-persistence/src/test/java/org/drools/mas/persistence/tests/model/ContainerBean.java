/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.drools.mas.persistence.tests.model;

import java.util.HashMap;
import java.util.Map;
import org.drools.mas.persistence.tests.MultipleInstancePersistentSubSessionTest;

/**
 * Helper class that can be used as a generic singleton bean to pass
 * values between different tests execution within a single test class.
 * For an example, you can look at {@link MultipleInstancePersistentSubSessionTest}
 * @author esteban
 */
public class ContainerBean {

    private final Map<String, Object> internalData = new HashMap<>();

    public ContainerBean() {
    }

    public Object getData(String key) {
        return internalData.get(key);
    }
    
    public void putData(String key, Object value) {
        this.internalData.put(key, value);
    }
    
    
    
}
