/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.helpers.handler;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.NodeList;

/**
 *
 * @author esteban
 */
public class LogHandler implements SOAPHandler<SOAPMessageContext> {

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext context) {
        try {
            SOAPMessage message = context.getMessage();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            message.writeTo(os);
            final String encoding = (String) message.getProperty(SOAPMessage.CHARACTER_SET_ENCODING);

//            try {
//                NodeList nl = message.getSOAPBody().getElementsByTagName("encodedContent");
//                if (nl != null) {
//                    for (int i = 0; i < nl.getLength(); i++) {
//
//                        org.w3c.dom.Node next = nl.item(i);
//
//                        if (next != null) {
//                            System.out.println("\n----------------------------------------------");
//                            System.out.println(">>Content: " + next.getTextContent());
//                            System.out.println("----------------------------------------------\n");
//                        }
//                    }
//                }
//            } catch (Exception e) {
//
//            }

            String xml;
            if (encoding == null) {
                xml = new String(os.toByteArray());
            } else {
                xml = new String(os.toByteArray(), encoding);
            }

            Logger.getLogger(LogHandler.class.getName()).log(Level.FINE, "SOAP Message:\n{0}\n", xml);

        } catch (Exception ex) {
            Logger.getLogger(LogHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    public void close(MessageContext context) {
    }

}
