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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.mas.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Info;
import org.drools.mas.body.content.NamedVariable;
import org.drools.mas.body.content.Query;
import org.drools.mas.mappers.MyMapArgsEntryType;
import org.drools.mas.mappers.MyMapReferenceEntryType;
import org.drools.runtime.rule.Variable;

/**
 *
 * @author salaboy
 */
public class MessageContentFactory { 

    public static Action newActionContent(String name, Map<String, Object> args) {
        Action action = new Action();
        action.setReturnVariable("?return");
        boolean hasOutputArg = false;
        action.setActionName(name);
        if(args == null){
            args = new HashMap<String, Object>();
        }
        action.getArgs().addAll(MapArgsAdapterHelper.marshal(args));


        if (args != null) {
            int j = 0;
            for (String key : args.keySet()) {
                if (args.get(key) instanceof Variable) {
                    MyMapReferenceEntryType myMapReferenceEntryType = new MyMapReferenceEntryType();
                    myMapReferenceEntryType.setKey(j);
                    myMapReferenceEntryType.setValue(key);
                    action.getReferences().add(myMapReferenceEntryType);
                    hasOutputArg = true;
                }
                j++;
            }
        }
        if (!hasOutputArg && args != null) {

            MyMapReferenceEntryType myMapReferenceEntryType = new MyMapReferenceEntryType();
            myMapReferenceEntryType.setKey( action.getArgs().size());
            myMapReferenceEntryType.setValue(action.getReturnVariable());
            action.getReferences().add(myMapReferenceEntryType);
            MyMapArgsEntryType myMapArgsEntryType = new MyMapArgsEntryType();
            myMapArgsEntryType.setKey(action.getReturnVariable());
            myMapArgsEntryType.setValue(Variable.v);
            action.getArgs().add(myMapArgsEntryType);
        }
        return action;
    }

    public static Action newActionContent(Action other) {
        Action action = new Action();
        action.setReturnVariable("?return");
        action.setActionName(other.getActionName());
        action.setArgs(new ArrayList(other.getArgs()));
        action.setReferences(new ArrayList(other.getReferences()));
        action.setEncodedContent(other.getEncodedContent());
        action.setEncoding(other.getEncoding());
        action.setEncoded(other.isEncoded());
        return action;
    }

    public static Query newQueryContent(String queryName, Object... args) {
        Query query = new Query();
        query.setQueryName(queryName);

        query.getArgs().addAll(Arrays.asList(args));
        //query.setReferences(new HashMap<Integer,String>());

        for (int j = 0; j < args.length; j++) {
            if (args[j] instanceof NamedVariable) {
                NamedVariable var = (NamedVariable) args[j];
                query.getArgs().set(j, var.getVariable());
                MyMapReferenceEntryType entry = new MyMapReferenceEntryType();
                entry.setKey(j);
                entry.setValue(var.getRef());
                query.getReferences().add(entry);


            }
//            else {
//                this.args.add(j,args[j]);
//            }
        }
        return query;
    }
    
    
    public static Info newInfoContent(Object payload) {
            Info info = new Info();
            info.setData( payload );
            return info;
        }
}
