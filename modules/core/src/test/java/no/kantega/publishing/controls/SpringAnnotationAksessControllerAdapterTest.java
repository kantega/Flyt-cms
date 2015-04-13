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

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
public class SpringAnnotationAksessControllerAdapterTest {

    private SpringAnnotationAksessControllerAdapter adapter = new SpringAnnotationAksessControllerAdapter();

    @Test
    public void shouldThrowExceptionWhenControllerIsNotAnnotated() {
        adapter.setController(new NotAnnotatedController());
        try {
            adapter.afterPropertiesSet();
            fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            //Expected
        } catch (Exception e) {
            fail("Inncorrect exception thrown.");
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenControllerIsAnnotated() {
        adapter.setController(new AnnotatedController());
        try {
            adapter.afterPropertiesSet();
        } catch (Exception e) {
            fail("Exception should not have been thrown.");
        }
    }

    @Test
    public void shouldThrowExceptionWhenNoRequestMapping() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.setRequestURI("/content/1234/Whatever");

        adapter.setController(new AnnotatedController());
        try {
            adapter.handleRequest(request, response);
            fail("Expected NoSuchRequestHandlingMethodException");
        } catch (NoSuchRequestHandlingMethodException nsrhme) {
            //Expected
        } catch (Exception e) {
            fail("Expected NoSuchRequestHandlingMethodException only. Caught " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void shouldHandleGet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        request.setMethod("GET");
        request.setRequestURI("/content/1234/Whatever");
        adapter.setController(new AnnotatedControllerWithRequestMappings());

        adapter.handleRequest(request, response);
        verify(outputStream).print("works!");
    }

    @Test
    public void shouldHandlePost() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setMethod("POST");
        request.setRequestURI("/content/1234/Whatever");
        String paramValue = "paramValue";
        request.setParameter("postParam", paramValue);
        adapter.setController(new AnnotatedControllerWithRequestMappings());

        Map model = adapter.handleRequest(request, response);
        assertEquals(paramValue, model.get("myAttr"));
    }

    @Test
    public void shouldNotReturnNullModel() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        request.setMethod("GET");
        request.setRequestURI("/content/1234/Whatever");
        adapter.setController(new AnnotatedControllerWithRequestMappings());

        Map model = adapter.handleRequest(request, response);
        assertNotNull("Model should not be null", model);
    }


    @Controller
    class AnnotatedController {}

    class NotAnnotatedController {}

    @Controller
    class AnnotatedControllerWithRequestMappings {

        @RequestMapping(method = RequestMethod.GET)
        public void handleGet(HttpServletResponse response) throws IOException {
            response.getOutputStream().print("works!");
        }

        @RequestMapping(method = RequestMethod.POST)
        public String handlePost(Model model, @RequestParam String postParam) {
            model.addAttribute("myAttr", postParam);
            return "";
        }
    }
}
