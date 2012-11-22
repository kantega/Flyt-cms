/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.mypage.plugins;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.enums.ContentProperty;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PropertySearchAction  implements Controller {
    private String formView;
    private String resultsView;
    private int maxRecords = 501;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            return new ModelAndView(formView);
        } else {
            Map<String, Object> model = new HashMap<String, Object>();

            RequestParameters param = new RequestParameters(request);

            ContentQuery query = new ContentQuery();
            int parent = param.getInt("parent");
            if (parent != -1) {
                ContentIdentifier cidParent =  ContentIdentifier.fromAssociationId(parent);
                query.setPathElementId(cidParent);
            }

            Date lastModified = param.getDate("lastmodified", Aksess.getDefaultDateFormat());
            if (lastModified != null) {
                query.setModifiedDate(lastModified);
            }

            int docType = param.getInt("doctype");
            if (docType != -1) {
                query.setDocumentType(docType);
            }

            String ownerperson = param.getString("ownerperson");
            if (ownerperson != null && ownerperson.length() > 0) {
                query.setOwnerPerson(ownerperson);
            }

            String[] sort = param.getString("sort").split(" ");
            boolean descending = false;
            if (sort != null) {
                model.put("sort", sort[0]);
                if (sort.length == 2 && "desc".equalsIgnoreCase(sort[1])) {
                    descending = true;
                }
                model.put("descending", descending);
            } else {
                model.put("sort", ContentProperty.TITLE);
                model.put("descending", Boolean.TRUE);
            }

            query.setMaxRecords(maxRecords);

            model.put("cq", query);
            model.put("maxRecords", maxRecords);

            return new ModelAndView(resultsView, model);
        }
    }

    public void setFormView(String formView) {
        this.formView = formView;
    }

    public void setResultsView(String resultsView) {
        this.resultsView = resultsView;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

}

