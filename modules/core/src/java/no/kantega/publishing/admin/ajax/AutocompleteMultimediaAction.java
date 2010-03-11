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

package no.kantega.publishing.admin.ajax;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.commons.util.StringHelper;
import no.kantega.commons.client.util.RequestParameters;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutocompleteMultimediaAction implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        RequestParameters param = new RequestParameters(request);
        
        String name = param.getString("q");
        if (name != null && name.length() >= 3) {
            MultimediaService mms = new MultimediaService(request);
            List mmlist = mms.searchMultimedia(name);
            for (int i = 0; i < mmlist.size(); i++) {
                Multimedia m =  (Multimedia)mmlist.get(i);
                String mmName = m.getName();
                mmName = StringHelper.removeIllegalCharsInTitle(mmName);
                m.setName(mmName);                
            }
            model.put("multimedialist", mmlist);
        }

        return new ModelAndView("/WEB-INF/jsp/ajax/searchresult-multimedia.jsp", model);
    }
}

