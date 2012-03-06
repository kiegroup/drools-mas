/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas;

import java.util.List;
import javax.jws.WebService;

/**
 *
 * @author salaboy
 */
@WebService
public interface AsyncDroolsAgentService {
    void tell(ACLMessage message);
    List<ACLMessage> getResponses(String msgId);
}
