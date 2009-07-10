/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package no.kantega.publishing.admin.multimedia.ajax;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.data.Multimedia;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ViewFolderAction implements Controller {

    private String view;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters params = new RequestParameters(request);
        MultimediaService mediaService = new MultimediaService(request);

        int folderId = params.getInt(AdminRequestParameters.ITEM_IDENTIFIER);
        if (folderId == -1) {
            folderId = 0;
        }

        Multimedia currentMultimedia = mediaService.getMultimedia(folderId);
        List<Multimedia> mmlist = mediaService.getMultimediaList(folderId);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put(AdminRequestParameters.MULTIMEDIA_CURRENT_FOLDER, currentMultimedia);
        model.put(AdminRequestParameters.MULTIMEDIA_ITEMS_LIST, mmlist);

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
