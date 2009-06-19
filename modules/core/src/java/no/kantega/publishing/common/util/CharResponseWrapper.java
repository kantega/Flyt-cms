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

import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.common.Aksess;

import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * User: Anders Skar, Kantega AS
 * Date: Oct 28, 2008
 * Time: 3:50:47 PM
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
        if (shouldWrap && contentType != null && (contentType.startsWith("text/html") || contentType.startsWith("text/xml"))) {
            return true;
        } else {
            return false;
        }
    }

    public String getContentType() {
        return contentType;
    }
}
