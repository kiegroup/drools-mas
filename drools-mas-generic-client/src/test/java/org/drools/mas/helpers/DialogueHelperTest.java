/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.helpers;

import java.io.IOException;
import org.junit.Test;

/**
 *
 */
public class DialogueHelperTest {
    
    @Test(timeout=3000)
    public void testNonExistingEndpoint(){
        
        try{
            DialogueHelper helper = new DialogueHelper("http://8.8.8.8:8080/action-agent/services/AsyncAgentService?WSDL", 2000);
        } catch(RuntimeException ex){
            if (ex.getCause() != null && !(ex.getCause() instanceof IOException)){
                throw ex;
            }
        }
    }
}
