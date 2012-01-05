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

package org.drools.fipa.body.content;


import java.util.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.drools.fipa.mappers.MyMapArgsEntryType;
import org.drools.fipa.mappers.MyMapReferenceEntryType;
import sun.security.x509.RFC822Name;

@XmlType(name = "Ref", namespace="http://content.body.fipa.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ref extends AbstractMessageContent implements Map<String,Object> {
  
   @XmlElement(required = true)
    public List<MyMapArgsEntryType> references = new ArrayList<MyMapArgsEntryType>(); 
   

    public Ref() {
    }

    
//    public Ref(Map<String, Object> references) {
//        this.references = references;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ref ref = (Ref) o;

        if (references != null ? !references.equals(ref.references) : ref.references != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return references != null ? references.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Ref{" +
                "references=" + references +
                ", encoded= " + getEncodedContent() +
                '}';
    }

   

//    public void encode(Encodings encoding) {
//        if (! isEncoded()) {
//            setEncodedContent(encode(references,encoding));
//            references = null;
//            setEncoded(true);
//        }
//    }
//
//    public void decode(Encodings encoding) {
//        if (isEncoded()) {
//            references = (Map<String,Object>) decodeContent(getEncodedContent(),encoding);
////            setEncodedContent(null);
//            setEncoded(false);
//        }
//    }

    public List<MyMapArgsEntryType> getReferences() {
        return references;
    }

    public void setReferences(List<MyMapArgsEntryType> references) {
        this.references = references;
    }

    public int size() {
        return references.size();
    }

    public boolean isEmpty() {
        return size() > 0;
    }

    public boolean containsKey(Object o) {
        for(MyMapArgsEntryType entry : this.references){
            if( entry.getKey().equals(o.toString() ) ){
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(Object o) {
        for(MyMapArgsEntryType entry : this.references){
            if( entry.getValue().equals(o.toString()) ){
                return true;
            }
        }
        return false;
    }

    public Object get(Object o) {
        for(MyMapArgsEntryType entry : this.references){
//            System.out.println("o.toString()"+o);
            if( entry.getKey().equals(o.toString()) ){
//                System.out.println("entry.getKey()="+entry.getKey()+"====="+o);
//                System.out.println("VALUE -> "+entry.getValue());
                return entry.getValue();
            }
        }
        return null;
    }

    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("Read-only : put not allowed");
    }


    public Object remove(Object o) {
        throw new UnsupportedOperationException("Read-only : remove not allowed");
    }

    public void putAll(Map<? extends String, ? extends Object> map) {
        throw new UnsupportedOperationException("Read-only : putAll not allowed");
    }

    public void clear() {
        this.references.clear();
    }

    public Set keySet() {
        HashSet<String> set = new HashSet<String>();
        for ( MyMapArgsEntryType entry : references ) {
            set.add( entry.getKey() );
        }
        return set;
    }

    public Collection values() {
        Collection<Object> list = new ArrayList<Object>();
        for ( MyMapArgsEntryType entry : references) {
            list.add( entry.getValue() );
        }
        return list;
    }

    public Set entrySet() {
        HashSet<Entry<String,Object>> set = new HashSet<Entry<String,Object>>();
        for ( MyMapArgsEntryType entry : references ) {
            final String k = entry.getKey();
            final Object v = entry.getValue();
            set.add( new Entry<String,Object>() {

                {
                    key = k;
                    value = v;
                }

                private String key;
                private Object value;

                public String getKey() {
                    return key;
                }

                public Object getValue() {
                    return value;
                }

                public Object setValue(Object value) {
                    this.value = value;
                    return value;
                }

                public String toString() {
                    return "[" + key + " = " + value +"]";
                }
            } );
        }
        return set;
    }


}
