/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.mas.mock;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class MockFact implements Serializable{
    private String name;
    private Integer age;

    public MockFact() {
    }

    public MockFact(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MockFact other = (MockFact) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.age != other.age && (this.age == null || !this.age.equals(other.age))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 17 * hash + (this.age != null ? this.age.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "MockFact{" + "name=" + name + ", age=" + age + '}';
    }
    
    
    
}
