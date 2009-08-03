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

package no.kantega.openaksess.groovyscripts;

import no.kantega.publishing.api.content.ContentRequestListenerAdapter;
import no.kantega.publishing.api.content.ContentRequestListener;
import no.kantega.publishing.common.data.Content;
import org.apache.log4j.Logger;
import org.springframework.web.context.ServletContextAware;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 */
public class GroovyScriptContentRequestListener extends ContentRequestListenerAdapter implements ServletContextAware {
    private Logger log = Logger.getLogger(getClass());

    private GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader());
    private ServletContext servletContext;
    private Map<String, ExecutionContext> scripts = Collections.synchronizedMap(new HashMap<String, ExecutionContext>());

    @Override
    public void beforeDisplayTemplateDispatch(DispatchContext context) {
        String template = context.getTemplateUrl();
        if (template.contains(".")) {
            String groovyPath = template.substring(0, template.lastIndexOf(".")) + ".groovy";


            try {

                final URL resource = servletContext.getResource(groovyPath);
                if (resource != null) {

                    Map<Class, Object> allowedParameters = new HashMap<Class, Object>();
                    allowedParameters.put(Content.class, context.getRequest().getAttribute("aksess_this"));
                    allowedParameters.put(HttpServletRequest.class, context.getRequest());
                    allowedParameters.put(HttpServletResponse.class, context.getResponse());


                    if (!scripts.containsKey(groovyPath)) {
                        Class clazz = classLoader.parseClass(new GroovyCodeSource(resource));
                        Method method = getMethod(clazz, context, allowedParameters);
                        Object script = clazz.newInstance();
                        scripts.put(groovyPath, new ExecutionContext(method, script));
                    }

                    ExecutionContext ex = scripts.get(groovyPath);


                    Object[] parameters = getParameters(ex.getMethod(), allowedParameters);
                    final Object result = ex.getMethod().invoke(ex.getScript(), parameters);
                    if (result != null && Map.class.isAssignableFrom(result.getClass())) {
                        Map<String, Object> attributes = (Map<String, Object>) result;
                        for (String name : attributes.keySet()) {
                            context.getRequest().setAttribute(name, attributes.get(name));
                        }
                    }
                    return;


                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private Object[] getParameters(Method method, Map<Class, Object> allowedParameters) {
        Object[] parameters = new Object[method.getParameterTypes().length];
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class paramClazz = method.getParameterTypes()[i];

            parameters[i] = allowedParameters.get(paramClazz);

        }
        return parameters;
    }

    private Method getMethod(Class clazz, DispatchContext context, Map<Class, Object> allowedParameters) {

        Method[] methods = clazz.getDeclaredMethods();


        for (Method method : methods) {

            if (method.isSynthetic()) {
                continue;
            }
            if (method.getName().contains("$")) {
                continue;
            }

            Object[] parameters = new Object[method.getParameterTypes().length];
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                Class paramClazz = method.getParameterTypes()[i];

                if (!allowedParameters.containsKey(paramClazz)) {
                    break;

                } else {
                    parameters[i] = allowedParameters.get(paramClazz);
                }
            }

            return method;
        }
        throw new IllegalArgumentException("Groovy class: " + clazz + " contains no valid method taking parameters");
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    class ExecutionContext {
        private Method method;
        private Object script;

        ExecutionContext(Method method, Object script) {
            this.method = method;
            this.script = script;
        }

        public Method getMethod() {
            return method;
        }

        public Object getScript() {
            return script;
        }
    }
}
