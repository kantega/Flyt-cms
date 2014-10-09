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
import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.api.index.ProgressReporter;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static no.kantega.openaksess.search.index.rebuild.ProgressReporterUtils.notAllProgressReportersAreMarkedAsFinished;

@Controller
public class RebuildIndexAction {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String formView;
    private String statusView;

    @Autowired
    private IndexRebuilder indexRebuilder;
    private List<ProgressReporter> progressReporters;
    @Autowired
    private List<IndexableDocumentProvider> indexableDocumentProviders;


    @RequestMapping(value = "/admin/administration/RebuildIndex.action", method = RequestMethod.GET)
    public ModelAndView handleGet() throws Exception {
        Map<String, Object> map = new HashMap<>();
        if(progressReporters == null || !notAllProgressReportersAreMarkedAsFinished(progressReporters)) {
            progressReporters = null;
            map.put("providers", indexableDocumentProviders);
            return new ModelAndView(formView, map);
        } else {
            map.put("progressReporters", progressReporters);
            return new ModelAndView(statusView, map);
        }
    }

    @RequestMapping(value = "/admin/administration/RebuildIndex.action", method = RequestMethod.POST)
    public ModelAndView handlePost(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();

        if (progressReporters == null) {
            SecuritySession securitySession = SecuritySession.getInstance(request);
            List<String> providersToInclude = getProvidersToInclude(request);
            log.info("Rebuild index started by {}. Providers: {}", securitySession.getUser().getId(), providersToInclude);
            progressReporters = indexRebuilder.startIndexing(providersToInclude);
        }
        return new ModelAndView(statusView, map);
    }

    @RequestMapping(value = "/admin/administration/RebuildIndexStatus.action", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getStatus(){
        Map<String, Object> model = new HashMap<>();
        List<Map<String, String>> status = new ArrayList<>();


        if(progressReporters != null){
            for (ProgressReporter progressReporter : progressReporters) {
                Map<String, String> statusMap = new LinkedHashMap<>();
                statusMap.put("current", String.valueOf(progressReporter.getCurrent()));
                statusMap.put("total", String.valueOf(progressReporter.getTotal()));
                statusMap.put("type", String.valueOf(progressReporter.getDocType()));
                status.add(statusMap);
            }
            model.put("status", status);
            model.put("allDone", !notAllProgressReportersAreMarkedAsFinished(progressReporters));
        }

        return model;
    }

    public void setFormView(String formView) {
        this.formView = formView;
    }

    public void setStatusView(String statusView) {
        this.statusView = statusView;
    }

    private List<String> getProvidersToInclude(HttpServletRequest request) {
        List<String> includedProviders = new ArrayList<>();
        for (IndexableDocumentProvider provider : indexableDocumentProviders) {
            String simpleName = provider.getName();
            if(ServletRequestUtils.getBooleanParameter(request, "include." + simpleName, false)){
                includedProviders.add(simpleName);
            }
        }
        return includedProviders;
    }
}
