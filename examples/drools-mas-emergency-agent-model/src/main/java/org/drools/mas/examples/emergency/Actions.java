/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.examples.emergency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class Actions implements Serializable{
    List<String> actions = new ArrayList<String>();

    public Actions(String... args) {
        for(String arg : args){
            actions.add(arg);
        }
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }
    
    
}
