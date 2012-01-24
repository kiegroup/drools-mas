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
package org.drools.mas.body.content;

import java.io.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.drools.mas.Encodings;
import org.drools.mas.mappers.MyMapArgsEntryType;
import org.drools.mas.mappers.MyMapReferenceEntryType;

/**
 * Actual mesasge content, i.e. the object of an ACL communicative act.
 */
@XmlType(name = "AbstractMessageContent", namespace = "http://content.body.mas.drools.org/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({
    Ref.class,
    Action.class,
    Query.class,
    Rule.class,
    Info.class,
    MyMapReferenceEntryType.class,
    MyMapArgsEntryType.class
})
public abstract class AbstractMessageContent implements Serializable {

    private String encodedContent;
    private boolean encoded;
    private Encodings encoding;

    public String getEncodedContent() {
        return encodedContent;
    }

    public void setEncodedContent(String encodedContent) {
        this.encodedContent = encodedContent;
    }

    public boolean isEncoded() {
        return encoded;
    }

    public Encodings getEncoding() {
        return encoding;
    }

    public void setEncoding(Encodings encoding) {
        this.encoding = encoding;
    }

    public void setEncoded(boolean encoded) {
        this.encoded = encoded;
    }
}
