/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.util.helper;

import org.drools.definition.type.Position;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class NodeLocator implements Serializable {

    @Position(0)
    private String nodeId;

    @Position(1)
    private boolean mindNode;


    public NodeLocator( String nodeId, boolean mindNode ) {
        this.nodeId = nodeId;
        this.mindNode = mindNode;
    }


    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public boolean isMindNode() {
        return mindNode;
    }

    public void setMindNode(boolean mindNode) {
        this.mindNode = mindNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeLocator that = (NodeLocator) o;

        if (mindNode != that.mindNode) return false;
        if (nodeId != null ? !nodeId.equals(that.nodeId) : that.nodeId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeId != null ? nodeId.hashCode() : 0;
        result = 31 * result + (mindNode ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NodeLocator{" +
                "nodeId='" + nodeId + '\'' +
                ", mindNode=" + mindNode +
                '}';
    }
}

