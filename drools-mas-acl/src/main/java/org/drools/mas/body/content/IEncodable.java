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

import org.drools.mas.Encodings;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/9/11
 * Time: 2:00 AM
 */
public interface IEncodable {

    public void encode(Encodings encoding);

    public void decode(Encodings encoding);

    public String getEncodedContent();

}
