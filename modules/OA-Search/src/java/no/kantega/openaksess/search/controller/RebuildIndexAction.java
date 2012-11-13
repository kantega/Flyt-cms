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

package no.kantega.openaksess.search.controller;

import no.kantega.openaksess.search.index.rebuild.IndexRebuilder;
import no.kantega.search.api.index.ProgressReporter;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.kantega.openaksess.search.index.rebuild.ProgressReporterUtils.notAllProgressReportersAreMarkedAsFinished;


public class RebuildIndexAction extends AbstractController {

    private String formView;
    private String statusView;

    @Autowired
    private IndexRebuilder indexRebuilder;
    private List<ProgressReporter> progressReporters;
    @Autowired
    private List<IndexableDocumentProvider> indexableDocumentProviders;


    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        if (progressReporters == null && request.getMethod().equals("POST")) {
            Integer numberOfConcurrentHandlers = ServletRequestUtils.getIntParameter(request, "numberOfConcurrentHandlers");

            progressReporters = indexRebuilder.startIndexing(numberOfConcurrentHandlers, getProvidersToExclude(request));

            map.put("progressReporters", progressReporters);
            return new ModelAndView(statusView, map);
        } else if(progressReporters == null || !notAllProgressReportersAreMarkedAsFinished(progressReporters)) {
            progressReporters = null;
            map.put("providers", indexableDocumentProviders);
            return new ModelAndView(formView, map);
        } else {
            map.put("progressReporters", progressReporters);
            return new ModelAndView(statusView, map);
        }

    }

    public void setFormView(String formView) {
        this.formView = formView;
    }

    public void setStatusView(String statusView) {
        this.statusView = statusView;
    }

    private List<String> getProvidersToExclude(HttpServletRequest request) {
        List<String> excludedProviders = new ArrayList<String>();
        for (IndexableDocumentProvider provider : indexableDocumentProviders) {
            String simpleName = provider.getClass().getSimpleName();
            if(ServletRequestUtils.getBooleanParameter(request, "exclude." + simpleName, false)){
                excludedProviders.add(simpleName);
            }
        }
        return excludedProviders;
    }
}
