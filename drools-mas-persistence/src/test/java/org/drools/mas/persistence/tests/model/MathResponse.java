/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.drools.mas.persistence.tests.model;

import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class MathResponse implements Serializable {
    private String msgId;
    private Double x;
    private Double y;
    private Double z;

    public MathResponse() {
    }

    public MathResponse(String msgId, Double x, Double y, Double z) {
        this.msgId = msgId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }
    
}
