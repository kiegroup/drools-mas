/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.examples.emergency;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author salaboy
 */
public class Emergency implements Serializable{
    public String id;
    public Date time;
    public String type;
    public int magnitude;

    public Emergency() {
    }

    public Emergency(String id, Date time, String type, int magnitude) {
        this.id = id;
        this.time = time;
        this.type = type;
        this.magnitude = magnitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(int magnitude) {
        this.magnitude = magnitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Emergency{" + "id=" + id + ", time=" + time + ", type=" + type + ", magnitude=" + magnitude + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Emergency other = (Emergency) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if (this.magnitude != other.magnitude) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 29 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 29 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 29 * hash + this.magnitude;
        return hash;
    }
    
    
    
}
