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

package no.kantega.publishing.controls;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

/**
 * Adapter for transforming an arbitrary Spring annotated controller into an AksessController.
 */
public class SpringAnnotationAksessControllerAdapter implements AksessController, InitializingBean {

    private Object controller;
    private String description;

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AnnotationMethodHandlerAdapter handlerAdapter = new AnnotationMethodHandlerAdapter();
        ModelAndView mav = handlerAdapter.handle(request, response, controller);
        Map model = Collections.emptyMap();
        if (mav != null && mav.getModel() != null && !mav.getModel().isEmpty()) {
            return mav.getModel();
        }
        return model;
    }

    /**
     * Verifies that the controller parameter is set and that the given object is annotated with @Controller
     *<br><br>
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (controller == null || controller.getClass().getAnnotation(Controller.class) == null) {
            throw new RuntimeException("Annotated AksessControllers must be annotated with @Controller");
        }
    }

    /**
     * The Spring controller to transform into an AksessController
     * @param controller Must be annotated with @Controller
     */
    public void setController(Object controller) {
        this.controller = controller;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }
}
