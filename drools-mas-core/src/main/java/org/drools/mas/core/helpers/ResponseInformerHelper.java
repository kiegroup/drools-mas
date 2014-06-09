/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.core.helpers;

import org.drools.mas.ACLMessage;
import org.drools.mas.core.DroolsAgentResponseInformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author salaboy
 */
public class ResponseInformerHelper {

    private static Logger logger = LoggerFactory.getLogger(ResponseInformerHelper.class);

    public static void informResponse(String implementation, ACLMessage originalMessage, ACLMessage response) {
            DroolsAgentResponseInformer responseInformer = getInstance(implementation);
            if(logger.isDebugEnabled()){
                logger.debug(" ### Informing Response : ("+implementation+") ");
                logger.debug(" ### \t message: "+originalMessage);
                logger.debug(" ### \t response: "+response);
            }
            responseInformer.informResponse(originalMessage, response);

    }

    public static DroolsAgentResponseInformer getInstance(String implementation) {
        DroolsAgentResponseInformer responseInformer = null;
        try {
            Class impl = null;
            impl = Class.forName(implementation);
            responseInformer = (DroolsAgentResponseInformer) impl.newInstance();
        } catch (ClassNotFoundException ex) {
            logger.error("Class Not Found (" + implementation + ") " + ex);
        } catch (InstantiationException ex) {
            logger.error("Instantation Exception (" + implementation + ") " + ex);
        } catch (IllegalAccessException ex) {
            logger.error("Illegal Access Exception (" + implementation + ") " + ex);
        }
        return responseInformer;
    }
}
