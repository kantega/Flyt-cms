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

package no.kantega.publishing.admin.attributes.ajax;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.data.enums.Language;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: Jun 12, 2007
 * Time: 10:25:34 AM
 */
public abstract class AbstractListOptionAction implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters params = new RequestParameters(request);
        String attributeKey = params.getString("attributeKey");
        String value = params.getString("value");
        boolean defaultSelected = params.getBoolean("defaultSelected");
        Locale locale = Language.getLanguageAsLocale(params.getInt("language"));

        Map model = new HashMap();
        try {
            doAction(attributeKey, value, defaultSelected, locale);
            model.put("success", "success");
        } catch (Exception e) {
            model.put("success", "fail");
        }


        return new ModelAndView(view, model);
    }

    private AbstractView view = new AbstractView() {
        protected void renderMergedOutputModel(Map map, HttpServletRequest request, HttpServletResponse response) throws Exception {
            String success = (map.get("success") != null)? ""+map.get("success"): "fail";
            response.getWriter().print("{success:'" + success + "'}");
        }

        public String getContentType() {
            return "text/html";
        }
    };


    protected abstract void doAction(String attributeKey, String value, boolean defaultSelected, Locale locale) throws Exception;

}
