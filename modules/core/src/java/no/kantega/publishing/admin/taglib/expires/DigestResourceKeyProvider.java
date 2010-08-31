/*
 * Copyright 2010 Kantega AS
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

package no.kantega.publishing.admin.taglib.expires;

import no.kantega.commons.taglib.expires.DigestPrettyPrinter;
import no.kantega.commons.taglib.expires.ResourceKeyProvider;
import no.kantega.publishing.spring.RuntimeMode;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class DigestResourceKeyProvider implements ResourceKeyProvider, ServletContextAware {

    private ServletContext servletContext;

    private static final String DIGEST_CACHE_ATTR = DigestResourceKeyProvider.class.getName() + ".CACHE_ATTR";

    private RuntimeMode runtimeMode;

    public String getUniqueKey(HttpServletRequest request, HttpServletResponse response, String path) {
        try {

            Map<String, String> digestCache = getDigestCache(servletContext);

            String digest = digestCache.get(path);

            if (digest == null || runtimeMode == RuntimeMode.DEVELOPMENT) {

                byte[] binaryDigest = createDigestByDispatching(request, response, path);
                digestCache.put(path, digest = DigestPrettyPrinter.prettyPrintDigest(binaryDigest).substring(0, 10));

            }


            return digest;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] createDigest(String path) throws NoSuchAlgorithmException, IOException {
        final URL resource = this.servletContext.getResource(path);

        MessageDigest dig = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[1000];
        int read;

        final InputStream is = resource.openStream();

        while ((read = is.read(buffer)) != -1) {
            dig.update(buffer, 0, read);
        }

        byte[] binaryDigest = dig.digest();
        return binaryDigest;
    }

    private byte[] createDigestByDispatching(HttpServletRequest request, HttpServletResponse response, final String path) throws NoSuchAlgorithmException, IOException, ServletException {
        DigestingServletResponse digestResponse = new DigestingServletResponse(response);
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
            @Override
            public String getRequestURI() {
                return path;
            }
        };
        this.servletContext.getRequestDispatcher(path).include(requestWrapper, digestResponse);

        return digestResponse.getDigest();
    }

    private synchronized Map<String, String> getDigestCache(ServletContext servletContext) {
        Map<String, String> cache = (Map<String, String>) servletContext.getAttribute(DIGEST_CACHE_ATTR);

        if (cache == null) {
            servletContext.setAttribute(DIGEST_CACHE_ATTR, cache = new HashMap<String, String>());
        }
        return cache;
    }


    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setRuntimeMode(RuntimeMode runtimeMode) {
        this.runtimeMode = runtimeMode;
    }

    private class DigestingServletResponse implements HttpServletResponse {
        private MessageDigest dig;
        private HttpServletResponse wrapped;

        private DigestingServletResponse(HttpServletResponse response) {
            this.wrapped = response;
            try {
                dig = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }


        }


        public ServletOutputStream getOutputStream() throws IOException {
            return new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    dig.update((byte) b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    dig.update(b, off, len);
                }

                @Override
                public void write(byte[] b) throws IOException {
                    dig.update(b);
                }
            };
        }

        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(getOutputStream());
        }

        public void setCharacterEncoding(String charset) {

        }

        public void setContentLength(int len) {
        }

        public void setContentType(String type) {
        }

        public void setBufferSize(int size) {
        }

        public void flushBuffer() throws IOException {
        }

        public void reset() {
        }

        public void resetBuffer() {
        }

        public void setLocale(Locale loc) {
        }

        public void addCookie(Cookie cookie) {

        }

        public void sendError(int sc, String msg) throws IOException {
        }

        public void sendError(int sc) throws IOException {
        }

        public void sendRedirect(String location) throws IOException {
        }

        public void setDateHeader(String name, long date) {
        }

        public void addDateHeader(String name, long date) {
        }

        public void setHeader(String name, String value) {
        }

        public void addHeader(String name, String value) {
        }

        public void setIntHeader(String name, int value) {
        }

        public void addIntHeader(String name, int value) {
        }

        public void setStatus(int sc) {
        }

        public void setStatus(int sc, String sm) {
        }


        public boolean containsHeader(String name) {
            return false;
        }

        public String encodeURL(String url) {
            return wrapped.encodeURL(url);
        }

        public String encodeRedirectURL(String url) {
            return wrapped.encodeRedirectURL(url);
        }

        public String encodeUrl(String url) {
            return wrapped.encodeUrl(url);
        }

        public String encodeRedirectUrl(String url) {
            return wrapped.encodeRedirectUrl(url);
        }

        public String getCharacterEncoding() {
            return wrapped.getCharacterEncoding();
        }

        public String getContentType() {
            return wrapped.getContentType();
        }

        public int getBufferSize() {
            return wrapped.getBufferSize();
        }

        public boolean isCommitted() {
            return false;
        }

        public Locale getLocale() {
            return wrapped.getLocale();
        }

        public byte[] getDigest() {
            return dig.digest();
        }
    }
}
