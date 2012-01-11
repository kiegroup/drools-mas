/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package mock;

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
    
}
