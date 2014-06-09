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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.drools.core.QueryResultsImpl;
import org.drools.core.rule.Declaration;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.NamedVariable;
import org.drools.mas.body.content.Query;
import org.drools.mas.body.content.Ref;
import org.drools.mas.mappers.MyMapArgsEntryType;
import org.drools.mas.mappers.MyMapReferenceEntryType;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author salaboy
 */
public class MessageContentHelper {

    public static Logger logger = LoggerFactory.getLogger(MessageContentHelper.class);

    public static Object[] getActionArgsArray(Action action) {
        Object[] myArray = new Object[action.getArgs().size()];
        int i = 0;
        for (MyMapArgsEntryType entry : action.getArgs()) {
            myArray[i] = entry.getValue();
            i++;
        }
        return myArray;
    }

    public static Ref getActionReferences(Action action, QueryResults results) {
        if (results.size() == 0) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        List<MyMapReferenceEntryType> pointers = action.getReferences();
        QueryResultsImpl inner = ( QueryResultsImpl ) results;

        Declaration[] params = inner.getParameters();
        for (MyMapReferenceEntryType entry : pointers) {
            Declaration dec = params[ entry.getKey()];

            if (logger.isDebugEnabled()) {
                logger.debug(" $$$ Params [" + entry.getKey() + "] = " + params[ entry.getKey()]);
                logger.debug(" $$$ inner.get(0).get( " + dec.getIdentifier() + ") = " + inner.get(0).get(dec.getIdentifier()));
                logger.debug(" $$$ entry.getValue() " + entry.getValue());
            }

            map.put(entry.getValue(), inner.get(0).get(dec.getIdentifier()));
        }

        Ref ref = new Ref();
        ref.setReferences(MapArgsAdapterHelper.marshal(map));
        return ref;
    }

    public static NamedVariable variable(String ref) {
        return new NamedVariable(ref);
    }

    public static Ref getQueryReferences(Query query, QueryResults results) {
        if (results.size() == 0) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        QueryResultsImpl inner = ( QueryResultsImpl ) results;

        Declaration[] params = inner.getParameters();

        for (MyMapReferenceEntryType entry : query.getReferences()) {
            Declaration dec = params[ entry.getKey()];
            map.put( entry.getValue(), inner.get(0).get(dec.getIdentifier()) );
        }

        Ref ref = new Ref();
        ref.setReferences(MapArgsAdapterHelper.marshal(map));

        return ref;
    }
    
     public static Ref getActionReferences(Action action, Map<String, Object> results) {
         if (results.size() == 0) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<MyMapArgsEntryType> iterator = action.getArgs().iterator();
        while(iterator.hasNext()){
            MyMapArgsEntryType entry = iterator.next();
            if(entry.getValue() instanceof Variable ){
                 map.put(entry.getKey(), results.get(entry.getKey()));   
            }
        }
        Ref ref = new Ref();
        ref.setReferences(MapArgsAdapterHelper.marshal(map));
        return ref;
     }
}
