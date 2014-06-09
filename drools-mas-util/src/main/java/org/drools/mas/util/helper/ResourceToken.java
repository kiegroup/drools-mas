package org.drools.mas.util.helper;

import org.kie.api.definition.type.Position;

public class ResourceToken {

    @Position(0)
    private String id;

    public ResourceToken( String id ) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        ResourceToken that = (ResourceToken) o;

        if ( !id.equals( that.id ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ResourceToken{" +
               "id='" + id + '\'' +
               '}';
    }
}
