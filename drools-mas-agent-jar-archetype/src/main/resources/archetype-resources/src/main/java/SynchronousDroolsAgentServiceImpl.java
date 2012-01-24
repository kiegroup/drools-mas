/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.mas;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;




import org.drools.mas.body.acts.*;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Info;
import org.drools.mas.body.content.Query;
import org.drools.mas.body.content.Ref;
import org.drools.mas.body.content.Rule;
import org.drools.mas.core.DroolsAgent;
import org.drools.mas.mappers.MyMapArgsEntryType;
import org.drools.mas.mappers.MyMapReferenceEntryType;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author salaboy
 * @author esteban
 */

@WebService(targetNamespace="http://mas.drools.org/")
@XmlSeeAlso(value={ACLMessage.class,AbstractMessageBody.class, Inform.class, Info.class, QueryIf.class, InformIf.class, 
                    Agree.class, Failure.class, Action.class, Rule.class, InformRef.class, Act.class,
                    QueryRef.class, Query.class, Ref.class, Encodings.class,
                    Ref.class, InformRef.class, Request.class, RequestWhen.class,
                    MyMapReferenceEntryType.class, MyMapArgsEntryType.class})
@SOAPBinding(
        style = SOAPBinding.Style.DOCUMENT,
        use = SOAPBinding.Use.LITERAL,
        parameterStyle = SOAPBinding.ParameterStyle.WRAPPED
)

public class SynchronousDroolsAgentServiceImpl implements SynchronousDroolsAgentService {

    private static Logger logger = LoggerFactory.getLogger(SynchronousDroolsAgentServiceImpl.class);
    
    private DroolsAgent agent;
    
    private SynchronousDroolsAgentResponseInformer responseInformer;
    
    
    public SynchronousDroolsAgentServiceImpl() { 
       
    }
    @WebMethod(exclude=true)
    public void setAgent(DroolsAgent agent) {
        this.agent = agent;
    }
    @WebMethod(exclude=true)
    public SynchronousDroolsAgentResponseInformer getResponseInformer() {
        return responseInformer;
    }
    @WebMethod(exclude=true)
    public void setResponseInformer(SynchronousDroolsAgentResponseInformer responseInformer) {
        this.responseInformer = responseInformer;
    }

    
    @WebMethod(operationName="tell")
    public  List<ACLMessage> tell( ACLMessage message) {
        logger.info(" >>> IN Message -> " + message.getPerformative().name());
        System.out.println(" >>> IN Message -> " + message.getPerformative());
        System.out.println("Agent = "+agent);
        System.out.println("responseInformer = "+responseInformer);
        try {
            agent.tell(message);
        } catch (Throwable t) {
            logger.error(">>>>>>>>>>>>> exception => " + t.getMessage());
            System.out.println(">>>>>>>>>>>>> exception => " + t.getMessage());

            t.printStackTrace();
        }
        List<ACLMessage> retrieveResponses = responseInformer.retrieveResponses(message);
        if (retrieveResponses != null) {
            
            logger.info(" >>> Number of OUT Messages -> " + retrieveResponses.size());
            System.out.println(" >>> Number of OUT Messages -> " + retrieveResponses.size());
            for (ACLMessage outMessage : retrieveResponses) {
                logger.info(" >>> OUT Message -> " + outMessage.getPerformative().name());
                System.out.println(" >>> OUT Message -> " + outMessage.getPerformative().name());
            }
        } else {
            logger.info(">>> 0 OUT Messages");
            System.out.println(">>> 0 OUT Messages");
        }
        return retrieveResponses;
    }
}
