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

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.drools.mas.Act;
import org.drools.mas.Encodings;
import org.drools.mas.body.acts.AbstractMessageBody;
import org.drools.mas.body.acts.Agree;
import org.drools.mas.body.acts.Inform;
import org.drools.mas.body.acts.InformIf;
import org.drools.mas.body.acts.InformRef;
import org.drools.mas.body.acts.QueryIf;
import org.drools.mas.body.acts.QueryRef;
import org.drools.mas.body.acts.Request;
import org.drools.mas.body.acts.RequestWhen;
import org.drools.mas.body.content.Action;
import org.drools.mas.body.content.Query;
import org.drools.mas.body.content.Ref;
import org.drools.mas.body.content.Rule;
import org.drools.runtime.rule.Variable;

/**
 *
 * @author salaboy
 */
public class MessageContentEncoder {

    //TODO: Use provider interfaces to decouple
    private static XStream xmlConverter;
    private static XStream jsonConverter;
    private static Gson gsonConverter;

    public static void decodeBody(AbstractMessageBody body, Encodings encoding) {
        Act act = body.getPerformative();
        Object decoded = null;
        switch (act) {
            case INFORM:
                decoded = MessageContentEncoder.decode(((Inform) body).getProposition().getEncodedContent(), encoding);
                ((Inform) body).getProposition().setData(decoded);
                ((Inform) body).getProposition().setEncoded(false);
                break;
            case INFORM_IF:
                decoded = MessageContentEncoder.decode(((InformIf) body).getProposition().getEncodedContent(), encoding);
                ((InformIf) body).getProposition().setData(decoded);
                ((InformIf) body).getProposition().setEncoded(false);
                break;
            case INFORM_REF:
                decoded = MessageContentEncoder.decode(((InformRef) body).getReferences().getEncodedContent(), encoding);
                ((InformRef) body).setReferences((Ref)decoded);
                ((InformRef) body).getReferences().setEncoded(false);
                break;
            case QUERY_IF:
                decoded = MessageContentEncoder.decode(((QueryIf) body).getProposition().getEncodedContent(), encoding);
                ((QueryIf) body).getProposition().setData(decoded);
                ((QueryIf) body).getProposition().setEncoded(false);
                break;

            case AGREE:
                Object decodedAction = MessageContentEncoder.decode(((Agree) body).getAction().getEncodedContent(), encoding);
                Object decodedCondition = MessageContentEncoder.decode(((Agree) body).getCondition().getEncodedContent(), encoding);
                ((Agree) body).setAction((Action) decodedAction);
                
                ((Agree) body).setCondition((Rule) decodedCondition);
                ((Agree) body).getCondition().setEncoded(false);
                ((Agree) body).getAction().setEncoded(false);
                break;


            case QUERY_REF:
                String oldEncoded = ((QueryRef) body).getQuery().getEncodedContent();
                decoded = MessageContentEncoder.decode(((QueryRef) body).getQuery().getEncodedContent(), encoding);
                ((QueryRef) body).setQuery((Query) decoded);
                ((QueryRef) body).getQuery().setEncodedContent(oldEncoded);
                ((QueryRef) body).getQuery().setEncoded(false);
                ((QueryRef) body).getQuery().setEncoding(encoding);

                String queryName = ((Query) decoded).getQueryName();
                List<Object> args = ((Query) decoded).getArgs();

                if (args != null) {
                    for (int i = 0; i < args.size(); i++) {
                        Object argument = args.get(i);
                        if (argument != null && argument instanceof Variable) {
                            Variable tmpVariable = Variable.v;
                            args.set(i, tmpVariable);

                        }
                    }
                }


                break;
            case REQUEST:
                String oldEncodedAction = ((Request) body).getAction().getEncodedContent();
                decoded = MessageContentEncoder.decode(((Request) body).getAction().getEncodedContent(), encoding);
                ((Request) body).setAction((Action) decoded);
                System.err.println( decoded );
                ((Request) body).getAction().setEncodedContent(oldEncodedAction);
                ((Request) body).getAction().setEncoded(false);
                break;
            case REQUEST_WHEN:
                String oldEncodedActionWhen = ((RequestWhen) body).getAction().getEncodedContent();
                String oldEncodedConditionWhen = ((RequestWhen) body).getCondition().getEncodedContent();
                Object decodedActionRequestWhen = MessageContentEncoder.decode(((RequestWhen) body).getAction().getEncodedContent(), encoding);
                Object decodedConditionRequestWhen = MessageContentEncoder.decode(((RequestWhen) body).getCondition().getEncodedContent(), encoding);
                ((RequestWhen) body).setAction((Action) decodedActionRequestWhen);
                ((RequestWhen) body).getAction().setEncodedContent(oldEncodedActionWhen);
                ((RequestWhen) body).getAction().setEncoded(false);
                
                ((RequestWhen) body).setCondition((Rule) decodedConditionRequestWhen);
                ((RequestWhen) body).getCondition().setEncodedContent(oldEncodedConditionWhen);
                ((RequestWhen) body).getCondition().setEncoded(false);
                break;    
        }
    }

    public static void encodeBody(AbstractMessageBody body, Encodings encoding) {
        Act act = body.getPerformative();
        String encoded = "";
        switch (act) {
            case INFORM:
                encoded = MessageContentEncoder.encode(((Inform) body).getProposition().getData(), encoding);
                ((Inform) body).getProposition().setEncodedContent(encoded);
                ((Inform) body).getProposition().setEncoded(true);
                ((Inform) body).getProposition().setEncoding(encoding);
                ((Inform) body).getProposition().setData(null);
                break;
            case INFORM_IF:
                encoded = MessageContentEncoder.encode(((InformIf) body).getProposition().getData(), encoding);
                ((InformIf) body).getProposition().setEncodedContent(encoded);
                ((InformIf) body).getProposition().setEncoded(true);
                ((InformIf) body).getProposition().setEncoding(encoding);
                ((InformIf) body).getProposition().setData(null);
                break;
            case INFORM_REF:
                encoded = MessageContentEncoder.encode(((InformRef) body).getReferences(), encoding);
                ((InformRef) body).getReferences().setEncodedContent(encoded);
                ((InformRef) body).getReferences().setEncoded(true);
                ((InformRef) body).getReferences().setEncoding(encoding);
                ((InformRef) body).getReferences().setReferences(null);
                break;
            case QUERY_IF:
                encoded = MessageContentEncoder.encode(((QueryIf) body).getProposition().getData(), encoding);
                ((QueryIf) body).getProposition().setEncodedContent(encoded);
                ((QueryIf) body).getProposition().setEncoded(true);
                ((QueryIf) body).getProposition().setEncoding(encoding);
                ((QueryIf) body).getProposition().setData(null);
                break;
            case QUERY_REF:
                encoded = MessageContentEncoder.encode(((QueryRef) body).getQuery(), encoding);
                ((QueryRef) body).getQuery().setEncodedContent(encoded);
                ((QueryRef) body).getQuery().setEncoded(true);
                ((QueryRef) body).getQuery().setEncoding(encoding);
                ((QueryRef) body).getQuery().getArgs().clear();
                ((QueryRef) body).getQuery().getReferences().clear();
                ((QueryRef) body).getQuery().setQueryName("");
                break;
            case AGREE:
                String encodedAction = MessageContentEncoder.encode(((Agree) body).getAction(), encoding);
                String encodedCondition = MessageContentEncoder.encode(((Agree) body).getCondition(), encoding);
                ((Agree) body).getAction().setEncoded(true);
                ((Agree) body).getAction().setEncodedContent(encodedAction);
                ((Agree) body).getAction().setEncoding(encoding);
                ((Agree) body).getAction().getArgs().clear();
                ((Agree) body).getAction().getReferences().clear();
                ((Agree) body).getCondition().setEncoded(true);
                ((Agree) body).getCondition().setEncoding(encoding);
                ((Agree) body).getCondition().setEncodedContent(encodedCondition);


                break;
            case REQUEST:
                
                encoded = MessageContentEncoder.encode(((Request) body).getAction(), encoding);
                ((Request) body).getAction().setEncoded(true);
                ((Request) body).getAction().setEncodedContent(encoded);
                ((Request) body).getAction().setEncoding(encoding);
                ((Request) body).getAction().getArgs().clear();
                ((Request) body).getAction().getReferences().clear();


                break;    
           case REQUEST_WHEN:
                String encodedConditionRequestWhen = MessageContentEncoder.encode(((RequestWhen) body).getCondition(), encoding);
                String encodedActionRequestWhen = MessageContentEncoder.encode(((RequestWhen) body).getAction(), encoding);
                ((RequestWhen) body).getAction().setEncoded(true);
                ((RequestWhen) body).getAction().setEncodedContent(encodedActionRequestWhen);
                ((RequestWhen) body).getAction().setEncoding(encoding);
                ((RequestWhen) body).getAction().getArgs().clear();
                ((RequestWhen) body).getAction().getArgs().clear();
                ((RequestWhen) body).getCondition().setEncoded(true);
                ((RequestWhen) body).getCondition().setEncodedContent(encodedConditionRequestWhen);

                break;   
        }
    }

    public static String encode(Object obj, Encodings encoding) {
        switch (encoding) {
            case BYTE:
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(obj);
                    oos.flush();
                    oos.close();
                    bos.close();
                    return new String(bos.toByteArray());
                } catch (IOException ioe) {
                    return null;
                }
            case JSON:
                String bodyString = getJsonConverter().toXML(obj);
                //TODO : Check - bug in XStream ?? Class name has one \" too much
                bodyString = bodyString.replaceAll("\"\"", "\"");
                return bodyString;
            case GSON:
                return getGsonConverter().toJson(obj);
            case XML:
                return getXmlConverter().toXML(obj);
            default:
                return null;
        }
    }

    private static XStream getJsonConverter() {
        if (jsonConverter == null) {
            jsonConverter = new XStream(new JettisonMappedXmlDriver());
        }
        return jsonConverter;
    }

    private static Gson getGsonConverter() {
        if (gsonConverter == null) {
            gsonConverter = new Gson();
        }
        return gsonConverter;
    }

    protected static XStream getXmlConverter() {
        if (xmlConverter == null) {
            xmlConverter = new XStream();
        }
        return xmlConverter;
    }

    protected static Object decode(String encodedContent, Encodings encoding) {
        switch (encoding) {
            case BYTE:
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(encodedContent.getBytes()));
                    return ois.readObject();
                } catch (Exception e) {
                    return null;
                }
            case JSON:

                return getJsonConverter().fromXML(encodedContent);
            case XML:
            default:
                return getXmlConverter().fromXML(encodedContent);
        }
    }
}