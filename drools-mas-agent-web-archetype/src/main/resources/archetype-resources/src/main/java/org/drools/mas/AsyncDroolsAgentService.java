/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ${package};

import java.util.List;
import javax.jws.WebService;
import org.drools.mas.ACLMessage;

/**
 *
 * @author salaboy
 */
@WebService
public interface AsyncDroolsAgentService {
    void tell(ACLMessage message);
    List<ACLMessage> getResponses(String msgId);
}
