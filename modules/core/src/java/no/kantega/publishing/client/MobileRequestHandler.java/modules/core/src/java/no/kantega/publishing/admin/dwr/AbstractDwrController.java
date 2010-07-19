/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.dwr;

import org.directwebremoting.WebContextFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

/**
 * Common base class for OpenAksess DWR controllers.
 */
public abstract class AbstractDwrController {


    /**
     * Helper method to retrieve the HttpSession
     * @return session
     */
    protected HttpSession getSession() {
        return WebContextFactory.get().getSession();
    }

    /**
     * Helper method to retrieve the HttpServletRequest
     * @return request
     */
    protected HttpServletRequest getRequest() {
        return WebContextFactory.get().getHttpServletRequest();
    }
}
