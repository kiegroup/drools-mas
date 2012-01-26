/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 *
 * @author salaboy
 */
@WebService
public interface SynchronousDroolsAgentService {
    @WebMethod
    List<ACLMessage> tell(ACLMessage message);
    
}
