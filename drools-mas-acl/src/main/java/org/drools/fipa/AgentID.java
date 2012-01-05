
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

package org.drools.fipa;



import java.io.Serializable;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.xml.bind.annotation.XmlType;



/**
 * Simple agent identifier, according to FIPA standard
 */
@XmlType(name = "AgentID", namespace="http://fipa.drools.org/")
public class AgentID implements Comparable, Serializable, Cloneable {



    protected static String platformID = "org.DROOLS";
    public static final String getPlatformID() { return platformID; }
    public static final void setPlatformID(String id) { platformID = id; }



    protected String name;
    protected List<URI> addresses;
    protected List<AgentID> resolvers;
    protected Properties userDefinedProperties;

    public AgentID() {
        this(UUID.randomUUID().toString(), false);
    }

   
    
    public AgentID(String name) {
        this(name,false);
    }

    public AgentID(String name, boolean isGUID) {
        if (isGUID) {
            setName(name);
        } else {
            setLocalName(name);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String globalName) {
        name = globalName.trim();
    }


    public void setLocalName(String localName) {
        name = localName.trim().concat("@"+getPlatformID());
    }


    public String getLocalName() {
        int atPos = name.lastIndexOf('@');
        return (atPos == -1) ? name : name.substring(0,atPos);
    }

    public String getHap() {
        int atPos = name.lastIndexOf('@');
        return (atPos == -1) ? null : name.substring(atPos + 1);
    }

    public List<URI> getAddresses() {
        if (addresses == null) {
            addresses = new LinkedList<URI>();
        }
        return addresses;
    }

    public List<AgentID> getResolvers() {
        if (resolvers == null) {
            resolvers = new LinkedList<AgentID>();
        }
        return resolvers;
    }

    public Properties getUserDefinedProperties() {
        if (userDefinedProperties == null) {
            userDefinedProperties = new Properties();
        }
        return userDefinedProperties;
    }


    public Object clone() {
        AgentID result;
        try {
            result = (AgentID) super.clone();
        } catch (CloneNotSupportedException ce) {
            throw new UnsupportedOperationException(ce);
        }

        if (addresses != null) {
            result.addresses = (List<URI>) ((LinkedList<URI>)addresses).clone();
        }

        if (resolvers != null) {
            result.resolvers = new LinkedList();
            if (resolvers.size() > 0) {
                for (AgentID resolver : resolvers) {
                    result.resolvers.add((AgentID) resolver.clone());
                }
            }
        }
        if (userDefinedProperties != null) {
            result.userDefinedProperties = (Properties) userDefinedProperties.clone();
        }
        return result;
    }


    public int compareTo(Object o) {
        return name.compareToIgnoreCase(((AgentID) o).name);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AgentID agentID = (AgentID) o;

        if (name != null ? !name.equals(agentID.name) : agentID.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }


}
