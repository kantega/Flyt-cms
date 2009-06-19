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

package no.kantega.publishing.admin.groovyconsole;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.ServletContextAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.BeansException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.codehaus.groovy.control.CompilationFailedException;

import java.util.Map;
import java.util.HashMap;
import java.io.StringWriter;
import java.io.PrintWriter;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

/**
 */
@Controller
@RequestMapping("/admin/groovy.action")
public class GroovyConsole implements ApplicationContextAware, ServletContextAware {
    private String view = "/WEB-INF/jsp/admin/groovy/groovy.jsp";;
    private ApplicationContext applicationContext;
    private ServletContext servletContext;


    @RequestMapping(method = RequestMethod.GET)
    public String show() {
        return view;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@RequestParam("code") String code, HttpServletRequest request, ModelMap model) {
        Binding binding = new Binding();
        Map<String, Object> inVariables = new HashMap<String, Object>();

        StringWriter out = new StringWriter();

        model.put("code", code);

        inVariables.put("request", request);
        inVariables.put("out", new PrintWriter(out));
        inVariables.put("context", applicationContext);
        inVariables.put("servletContext", servletContext);


        Map beans = new HashMap();
        beans.putAll(BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, Object.class));

        inVariables.put("beans", beans);

        GroovyShell shell = new GroovyShell(binding);

        for(String key : inVariables.keySet()) {
            binding.setVariable(key, inVariables.get(key));
        }

        try {
            Script script= shell.parse(code);

            script.setBinding(binding);


            Object value = script.run();
            model.put("returnValue", value);
            if(out.toString().length() > 0) {
                model.put("out", out.toString());
            }

            Map variables = new HashMap();
            variables.putAll(binding.getVariables());
            for(String key : inVariables.keySet()) {
                variables.remove(key);
            }

            model.put("variables", variables);
        } catch (CompilationFailedException e) {
            model.put("exception", e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            model.put("exception", e);
            model.put("out", sw.toString());

        }  catch (Exception e) {
            model.put("exception", e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            model.put("exception", e);
            model.put("out", sw.toString());

        }

        return view;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
