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

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import no.kantega.publishing.api.annotations.RequestHandler;
import no.kantega.publishing.api.requestlisteners.ContentRequestListenerAdapter;
import no.kantega.publishing.common.data.Content;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * A ContentRequestListener that executes groovy scripts with the same name as a jsp.
 */
public class GroovyScriptContentRequestListener extends ContentRequestListenerAdapter implements ServletContextAware {

    private GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader());
    private ServletContext servletContext;
    private Map<String, ExecutionContext> scripts = Collections.synchronizedMap(new HashMap<String, ExecutionContext>());

    private ApplicationContext rootApplicationContext;

    @Override
    public void beforeDisplayTemplateDispatch(DispatchContext context) {
        executeTemplate(context);

    }

    @Override
    public void beforeIncludeTemplateDispatch(DispatchContext context) {
        executeTemplate(context);
    }

    @Override
    public void beforeMiniviewDispatch(DispatchContext context) {
        executeTemplate(context);
    }

    private void executeTemplate(DispatchContext context) {
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
                    allowedParameters.put(ServletContext.class, servletContext);


                    synchronized (scripts) {
                        if (!scripts.containsKey(groovyPath) || getLastModified(resource) > scripts.get(groovyPath).getLastModified()) {
                            Class clazz = classLoader.parseClass(new GroovyCodeSource(resource));
                            Method method = getMethod(clazz, groovyPath, allowedParameters);
                            Object script = clazz.newInstance();
                            scripts.put(groovyPath, new ExecutionContext(method, script, getLastModified(resource)));
                        }
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

    private long getLastModified(URL source) {
        if (source.getProtocol().equals("file")) {
            String path = source.getPath().replace('/', File.separatorChar).replace('|', ':');
            File file = new File(path);
            return file.lastModified();
        } else {
            try {
                URLConnection conn = source.openConnection();
                long lastMod = conn.getLastModified();
                conn.getInputStream().close();
                return lastMod;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Object[] getParameters(Method method, Map<Class, Object> allowedParameters) {
        Object[] parameters = new Object[method.getParameterTypes().length];
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class paramClazz = method.getParameterTypes()[i];

            if (allowedParameters.containsKey(paramClazz)) {
                parameters[i] = allowedParameters.get(paramClazz);
            } else {
                final Map beans = rootApplicationContext.getBeansOfType(paramClazz);
                if (beans.size() == 1) {
                    parameters[i] = beans.values().iterator().next();
                } else {
                    final Annotation[] annotations = method.getParameterAnnotations()[i];

                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Qualifier) {
                            Qualifier q = (Qualifier) annotation;
                            if (beans.containsKey(q.value())) {
                                parameters[i] = beans.get(q.value());
                                break;
                            }

                        }
                    }
                }
            }

        }
        return parameters;
    }

    private Method getMethod(Class clazz, String groovyPath, Map<Class, Object> allowedParameters) {

        Method[] methods = clazz.getDeclaredMethods();

        List<Method> candidateMethods = new ArrayList<Method>();
        Method annotatedMethod  = null;

        for (Method method : methods) {

            if (method.isSynthetic()) {
                continue;
            }
            if (method.getName().contains("$")) {
                continue;
            }
            if (method.getName().equals("run")) {
                continue;
            }
            if (method.getName().equals("main")) {
                continue;
            }
            if (method.getModifiers() == Modifier.PRIVATE) {
                continue;
            }

            boolean methodHasUnsatisfiableParameter= false;

            for (int i = 0; i < method.getParameterTypes().length; i++) {
                Class paramClazz = method.getParameterTypes()[i];

                if (!allowedParameters.containsKey(paramClazz)) {
                    final Map beans = rootApplicationContext.getBeansOfType(paramClazz);

                    if (beans.size() == 0) {
                        methodHasUnsatisfiableParameter = true;
                        break;
                    }else if (beans.size() > 1) {

                        for (Annotation annotation : method.getParameterAnnotations()[i]) {
                            if (annotation instanceof Qualifier) {
                                Qualifier q = (Qualifier) annotation;
                                if (!beans.containsKey(q.value())) {
                                    methodHasUnsatisfiableParameter = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (!methodHasUnsatisfiableParameter) {
                candidateMethods.add(method);
                if(method.getAnnotation(RequestHandler.class) != null) {
                    if(annotatedMethod != null) {
                        throw new IllegalArgumentException("Only a single method can be annotated with @" + RequestHandler.class.getSimpleName());
                    } else {
                        annotatedMethod = method;
                    }
                }
            }
        }

        if(annotatedMethod != null) {
            return annotatedMethod;
        } else if(candidateMethods.size() == 1) {
            return candidateMethods.get(0);
        } else if(candidateMethods.size() > 1) {
            throw new IllegalArgumentException("Detected multiple candidate methods for execution, please annotate your handler method with @" + HandlerMapping.class.getSimpleName());
        } else {
            throw new IllegalArgumentException("Groovy class: " + clazz + " contains no valid method taking allowed parameters");
        }

    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setRootApplicationContext(ApplicationContext rootApplicationContext) {
        this.rootApplicationContext = rootApplicationContext;
    }

    class ExecutionContext {
        private Method method;
        private Object script;
        private long lastModified;

        ExecutionContext(Method method, Object script, long lastModified) {
            this.method = method;
            this.script = script;
            this.lastModified = lastModified;
        }

        public Method getMethod() {
            return method;
        }

        public Object getScript() {
            return script;
        }

        public long getLastModified() {
            return lastModified;
        }
    }
}