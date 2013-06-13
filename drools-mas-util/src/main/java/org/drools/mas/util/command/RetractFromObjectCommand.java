/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.util.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.InternalFactHandle;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

@XmlAccessorType(XmlAccessType.NONE)
public class RetractFromObjectCommand
        implements GenericCommand<Object> {

    private Object object;

    public RetractFromObjectCommand() {
    }

    public RetractFromObjectCommand(Object object) {
        this.object = object;
    }

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        System.out.println("OBJECT INSIDE THE COMMAND: "+this.object);
        FactHandle handle = ksession.getFactHandle(this.object);
        if ( handle != null ) {
            // objects may not be in the WM (anymore). The remote client is not guaranteed to have up-to-date information
            ksession.getWorkingMemoryEntryPoint( ((InternalFactHandle)handle).getEntryPoint().getEntryPointId() ).retract( handle );
        }
        return null;
    }

    public Object getObject() {
        return this.object;
    }

    public String toString() {
        return "session.retractFromObject( " + object + " );";
    }

}
