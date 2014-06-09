package org.drools.mas.core;

import org.drools.mas.body.content.Action;
import org.drools.mas.util.MessageContentFactory;
import org.kie.api.io.Resource;

import java.util.HashMap;

public class ResourceActions {

    public static final String ADD_RESOURCE = "add_resource";
    public static final String REM_RESOURCE = "rem_resource";

    public static final String ARG = "resource";

    public static Action newAddResourceAction( Resource res ) {
        HashMap<String,Object> args = new HashMap<String,Object>();
        args.put( ARG, res );
        return MessageContentFactory.newActionContent( ADD_RESOURCE, args );
    }

    public static Action newRemResourceAction( Resource res ) {
        HashMap<String,Object> args = new HashMap<String,Object>();
        args.put( ARG, res );
        return MessageContentFactory.newActionContent( REM_RESOURCE, args );
    }
}
