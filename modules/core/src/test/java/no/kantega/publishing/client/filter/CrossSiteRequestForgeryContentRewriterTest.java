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

package no.kantega.publishing.client.filter;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;

/**
 */
public class CrossSiteRequestForgeryContentRewriterTest {
    private CrossSiteRequestForgeryContentRewriter rewriter;

    @Before
    public void setUp() {
        rewriter = new CrossSiteRequestForgeryContentRewriter() {
            @Override
            protected String generateKey(HttpServletRequest request) {
                return "KEY";
            }

            @Override
            protected boolean shouldRewrite(HttpServletRequest request) {
                return true;
            }
        };
    }

    @Test
    public void testRewriteContent() {

        assertEquals("<form><div style=\"display: none\"><input type=\"hidden\" name=\"csrfkey\" value=\"KEY\"></div>", rewriter.rewriteContent(null, "<form>"));

        assertEquals("<form><div style=\"display: none\"><input type=\"hidden\" name=\"csrfkey\" value=\"KEY\"></div></form>", rewriter.rewriteContent(null, "<form></form>"));

        assertEquals("<FORM><div style=\"display: none\"><input type=\"hidden\" name=\"csrfkey\" value=\"KEY\"></div></form>", rewriter.rewriteContent(null, "<FORM></form>"));

        assertEquals("foo<form><div style=\"display: none\"><input type=\"hidden\" name=\"csrfkey\" value=\"KEY\"></div><input type=submit></form>bar", rewriter.rewriteContent(null, "foo<form><input type=submit></form>bar"));
    }


}
