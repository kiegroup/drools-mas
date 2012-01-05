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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa.util;

import com.jayway.jsonpath.JsonPath;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import org.drools.fipa.ACLMessage;
import org.drools.fipa.Act;
import org.drools.fipa.body.acts.Agree;
import org.drools.fipa.body.acts.Inform;
import org.drools.fipa.body.acts.InformRef;
import org.drools.fipa.body.acts.QueryIf;
import org.drools.fipa.body.acts.QueryRef;
import org.drools.fipa.body.acts.Request;
import org.drools.fipa.body.acts.RequestWhen;
import org.drools.fipa.body.acts.RequestWhenever;
import org.drools.fipa.body.content.AbstractMessageContent;

/**
 *
 * @author salaboy
 */
public class InspectMessageHelper {

    public static String inspect(ACLMessage message, String path) throws ParseException, XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        AbstractMessageContent content = inspectContent(message);
      
        if (content.getEncodedContent() != null || !content.getEncodedContent().equals("")) {
            switch (message.getEncoding()) {
                case JSON:
                    Object res = JsonPath.read(content.getEncodedContent(), path);
                    return (res != null) ? res.toString() : null;
                case XML:
                    XPath accessor = XPathFactory.newInstance().newXPath();
                    InputStream inStream = new ByteArrayInputStream(content.getEncodedContent().getBytes());
                    Document dox = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inStream);
                    return (String) accessor.evaluate(path, dox, XPathConstants.STRING);
                default:
                    throw new ParseException("Unable to access byte-encoded message body", 0);
            }
        }
        return null;
    }

    public static AbstractMessageContent inspectContent(ACLMessage message) {
        
        Act act = message.getBody().getPerformative();
       
        switch (act) {
            case INFORM:
                return ((Inform) message.getBody()).getProposition();

            case QUERY_IF:
                return ((QueryIf) message.getBody()).getProposition();

            case QUERY_REF:
                return ((QueryRef) message.getBody()).getQuery();

            case INFORM_REF:
                return ((InformRef) message.getBody()).getReferences();

            case REQUEST:
                return ((Request) message.getBody()).getAction();
            case REQUEST_WHEN:
                return ((RequestWhen) message.getBody()).getAction();
            case REQUEST_WHENEVER:
                return ((RequestWhenever) message.getBody()).getAction();    
            
            case AGREE:
                return ((Agree) message.getBody()).getCondition();


        }
        return null;

    }
}
