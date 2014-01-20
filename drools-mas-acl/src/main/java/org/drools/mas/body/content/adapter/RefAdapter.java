/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.drools.mas.body.content.adapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.drools.mas.body.content.Ref;

/**
 *
 * @author esteban
 */
public class RefAdapter extends XmlAdapter<String, Ref>{

    @Override
    public Ref unmarshal(String v) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Ref.class);
        Object unmarshal = jc.createUnmarshaller().unmarshal(new ByteArrayInputStream(v.getBytes()));
        return (Ref)unmarshal;
    }

    @Override
    public String marshal(Ref v) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        JAXBContext jc = JAXBContext.newInstance(Ref.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(v, baos);
        
        return baos.toString();
    }

    
}