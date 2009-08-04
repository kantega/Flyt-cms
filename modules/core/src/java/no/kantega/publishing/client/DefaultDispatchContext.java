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

package no.kantega.publishing.client;

import no.kantega.publishing.api.content.ContentRequestListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public class DefaultDispatchContext implements ContentRequestListener.DispatchContext {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String templateUrl;

    public DefaultDispatchContext(HttpServletRequest request, HttpServletResponse response, String templateUrl) {
        this.request = request;
        this.response = response;
        this.templateUrl = templateUrl;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }
}