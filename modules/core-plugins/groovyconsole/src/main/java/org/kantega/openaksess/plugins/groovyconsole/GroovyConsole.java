/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kantega.openaksess.plugins.groovyconsole;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Controller
@RequestMapping("/admin/groovy.action")
public class GroovyConsole implements ApplicationContextAware, ServletContextAware {
    private String view = "org/kantega/openaksess/plugins/groovyconsole/views/groovy";
    private ApplicationContext applicationContext;
    private ServletContext servletContext;

    private ApplicationContext rootApplicationContext;

    @RequestMapping(method = RequestMethod.GET)
    public String show(HttpServletRequest request, ModelMap model) throws IOException {
        model.put("contextPath", request.getContextPath());
        return view;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@RequestParam("code") String code, HttpServletRequest request, ModelMap model) {
        Binding binding = new Binding();
        Map<String, Object> inVariables = new HashMap<>();

        StringWriter out = new StringWriter();

        model.put("code", code);

        inVariables.put("request", request);
        inVariables.put("out", new PrintWriter(out));
        inVariables.put("context", applicationContext);
        inVariables.put("servletContext", servletContext);

        Map<String, Object> beans = new HashMap<>();
        beans.putAll(BeanFactoryUtils.beansOfTypeIncludingAncestors(rootApplicationContext, Object.class));

        inVariables.put("beans", beans);

        GroovyShell shell = new GroovyShell(binding);

        for (Map.Entry<String, Object> attribute : inVariables.entrySet()) {
            binding.setVariable(attribute.getKey(), attribute.getValue());
        }

        try {
            Script script= shell.parse(code);

            script.setBinding(binding);


            Object value = script.run();
            model.put("returnValue", value);
            if(out.toString().length() > 0) {
                model.put("out", out.toString());
            }

            Map<String, Object> variables = new HashMap<>();
            variables.putAll(binding.getVariables());
            for(String key : inVariables.keySet()) {
                variables.remove(key);
            }

            model.put("variables", variables);
        } catch (Exception e) {
            model.put("exception", e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            model.put("exception", e);
            model.put("out", sw.toString());

        }

        model.put("contextPath", request.getContextPath());

        return view;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setRootApplicationContext(ApplicationContext rootApplicationContext) {
        this.rootApplicationContext = rootApplicationContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
