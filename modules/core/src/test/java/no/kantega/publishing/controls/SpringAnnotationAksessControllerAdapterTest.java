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
import org.springframework.stereotype.Controller;

import static org.junit.Assert.fail;

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

    @Controller
    class AnnotatedController {

    }

    class NotAnnotatedController {

    }
}
