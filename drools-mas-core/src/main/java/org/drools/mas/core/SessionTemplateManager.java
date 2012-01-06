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

package org.drools.mas.core;

import org.drools.io.ResourceFactory;
import org.mvel2.templates.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.Map;

public class SessionTemplateManager {

    private TemplateRegistry registry;

    public static final String[] NAMED_TEMPLATES = new String[] {
        "requestWhen.drlt"
    };
    public static final String TEMPLATE_PATH = "org/drools/mas/templates/";



    public SessionTemplateManager() {
        this.registry = new SimpleTemplateRegistry();
        this.buildRegistry(registry);
    }



    protected String[] getNamedTemplates(){
        return NAMED_TEMPLATES;
    }

    protected String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    public TemplateRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(TemplateRegistry registry) {
        this.registry = registry;
    }

    protected void buildRegistry(TemplateRegistry registry) {
        for (String ntempl : getNamedTemplates()) {
            try {
                String path = getTemplatePath()+ntempl;
                InputStream stream = ResourceFactory.newClassPathResource(path, this.getClass()).getInputStream();

                registry.addNamedTemplate( path.substring(path.lastIndexOf('/') + 1),
                        TemplateCompiler.compileTemplate(stream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void applyTemplate(String templateName, Object context, Map vars, Formatter fmt) {
        CompiledTemplate template = (CompiledTemplate) getRegistry().getNamedTemplate(templateName);
        try {
            fmt.out().append(TemplateRuntime.execute(template, context, vars).toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    protected String applyTemplate(String templateName, Object context, Map vars) {
        CompiledTemplate template = (CompiledTemplate) getRegistry().getNamedTemplate(templateName);
        return (TemplateRuntime.execute(template, context, vars).toString());
    }

}
