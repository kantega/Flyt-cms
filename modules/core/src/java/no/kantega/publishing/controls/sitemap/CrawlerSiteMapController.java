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

package no.kantega.publishing.controls.sitemap;

import no.kantega.publishing.common.Aksess;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * User: tarkil
 * Date: Mar 11, 2008
 * Time: 10:07:44 AM
 */
public class CrawlerSiteMapController implements Controller {


    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav;
        Map model = new HashMap();

        boolean enabled = Aksess.getConfiguration().getBoolean("crawler.sitemap.enabled", false);
        String associationcategory = Aksess.getConfiguration().getString("crawler.sitemap.associationcategory", null);
        int depth = Aksess.getConfiguration().getInt("crawler.sitemap.depth", -1);

        if (enabled) {
            model.put("associationcategory", associationcategory);
            model.put("depth", new Integer(depth));
            mav = new ModelAndView("/WEB-INF/jsp/sitemap/sitemap.jsp", model);
        } else {
            response.sendError(404);
            mav = new ModelAndView();
        }
        return mav;
    }

}
