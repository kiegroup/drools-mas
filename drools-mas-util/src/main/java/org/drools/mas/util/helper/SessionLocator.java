/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.util.helper;

import org.kie.api.definition.type.Position;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class SessionLocator implements Serializable {

    @Position(0)
    private String sessionId;
    @Position(1)
    private boolean mind = true;
    @Position(2)
    private boolean child = false;
    @Position(3)
    private boolean mutable = false;

    public SessionLocator( String sessionId ) {
        this.sessionId = sessionId;
    }

    public SessionLocator( String sessionId, boolean mind, boolean child ) {
        this( sessionId, mind, child, false );
    }

    public SessionLocator( String sessionId, boolean mind, boolean child, boolean mutable ) {
        this.sessionId = sessionId;
        this.mind = mind;
        this.child = child;
        this.mutable = mutable;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isChild() {
        return child;
    }

    public void setChild(boolean child) {
        this.child = child;
    }

    public boolean isMind() {
        return mind;
    }

    public void setMind(boolean mind) {
        this.mind = mind;
    }

    public boolean isMutable() {
        return mutable;
    }

    public void setMutable( boolean mutable ) {
        this.mutable = mutable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SessionLocator other = (SessionLocator) obj;
        if ((this.sessionId == null) ? (other.sessionId != null) : !this.sessionId.equals(other.sessionId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.sessionId != null ? this.sessionId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "SessionLocator{" + ", sessionId=" + sessionId + ", mind=" + mind + ", child=" + child + '}';
    }

    
    
    
}
