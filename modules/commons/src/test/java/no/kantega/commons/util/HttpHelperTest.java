/*
 * Copyright 2011 Kantega AS
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

package no.kantega.commons.util;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HttpHelperTest {
    @Test
    public void shouldCreateCorrectUrlFromParameters() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("pdf", "true");
        request.addParameter("android", "false");
        request.addParameter("iphone", "true");

        String generatedUrl = HttpHelper.createQueryStringFromRequestParameters(request);

        assertNotNull(generatedUrl);
        assertTrue(generatedUrl.contains("pdf=true"));
        assertTrue(generatedUrl.contains("iphone=true"));
        assertTrue(generatedUrl.contains("android=false"));
        assertSame("pdf=true&android=false&iphone=true".length(), generatedUrl.length());
    }

    @Test
    public void shouldHandleMultipleValuesForSameParameterName() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("pageIds", new String[]{"1", "2", "3"});

        String generatedUrl = HttpHelper.createQueryStringFromRequestParameters(request);

        assertNotNull(generatedUrl);
        assertTrue(generatedUrl.contains("pageIds=1"));
        assertTrue(generatedUrl.contains("pageIds=2"));
        assertTrue(generatedUrl.contains("pageIds=3"));
    }

}
