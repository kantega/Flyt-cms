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

package no.kantega.publishing.common.util;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * HttpServletResponseWrapper for gathering responsebody and rewriting it in ContentRewriteFilter.
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {
    private CharArrayWriter output;
    private String contentType;
    private boolean shouldWrap = true;

    public String toString() {
        return output.toString();
    }

    public CharResponseWrapper(HttpServletResponse response){
        super(response);
        output = new CharArrayWriter();
    }


    public PrintWriter getWriter() throws IOException {
        if (isWrapped()) {
	    super.getWriter();
            return new PrintWriter(output);
        } else {
            return super.getWriter();
        }
    }

    public ServletOutputStream getOutputStream() throws IOException {
        shouldWrap = false;
        return super.getOutputStream();
    }


    public void setContentType(String contentType) {
        super.setContentType(contentType);
        this.contentType = contentType;
    }

    public boolean isWrapped() {
        return shouldWrap && contentType != null
                && (contentType.startsWith("text/html") || contentType.startsWith("text/xml"));
    }

    public String getContentType() {
        return contentType;
    }
}
