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
package org.drools.mas;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author salaboy
 */
@XmlType(name = "Encodings", namespace="http://mas.drools.org/")
public enum Encodings {

    XML("text/xml"),
    JSON("application/json"),
    GSON("application/json"),
    BYTE("application/octet-stream"),
    NONE("application/x-java-serialized-object");
    private String encoding;

    private Encodings(String enc) {
        encoding = enc;
    }

    public String getEncoding() {
        return encoding;
    }
}
