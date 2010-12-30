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

package no.kantega.publishing.admin.administration.action;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import no.kantega.search.index.IndexManager;
import no.kantega.search.index.IndexSearcherManager;
import no.kantega.publishing.search.index.jobs.RebuildIndexJob;
import no.kantega.publishing.search.index.jobs.OptimizeIndexJob;
import no.kantega.publishing.search.index.jobs.RebuildSpellCheckIndexJob;
import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.search.index.rebuild.ProgressReporter;


public class RebuildIndexAction extends AdminController {

    private String formView;
    private String statusView;

    int current = -1;
    int total = -1;

    private IndexManager indexManager;
    private String docType ="";

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        if (current < 0) {
            if (request.getMethod().equals("POST")) {
                String rebuild = request.getParameter("rebuild");
                String spellCheck = request.getParameter("spelling");
                String optimize = request.getParameter("optimize");
                if(rebuild != null) {
                    startIndex();
                }
                if(optimize != null) {
                    indexManager.addIndexJob(new OptimizeIndexJob());
                }
                if(spellCheck != null) {
                    indexManager.addIndexJob(new RebuildSpellCheckIndexJob());
                }

                map.put("current", current);
                map.put("total", total);
                return new ModelAndView(new RedirectView("RebuildIndex.action"));
            } else {
                return new ModelAndView(formView);
            }
        } else {
            map.put("current", current);
            map.put("total", total);
            map.put("docType", docType);
            return new ModelAndView(statusView, map);
        }
    }

    private synchronized void startIndex() {
        current = 0;
        ProgressReporter p = new ProgressReporter() {

            public void reportProgress(int c, String d, int t) {
                current = c;
                total = t;
                docType = d;
            }

            public void reportFinished() {
                current = -1;
                total = -1;
            }
        };


        indexManager.addIndexJob(new RebuildIndexJob(p));

    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public void setFormView(String formView) {
        this.formView = formView;
    }

    public void setStatusView(String statusView) {
        this.statusView = statusView;
    }
}
