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

package no.kantega.publishing.api.taglibs.mini;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.WorkList;
import no.kantega.publishing.common.service.ContentManagementService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTagSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GetDraftsForUserTag  extends LoopTagSupport {
    private Iterator i;

    protected Object next() throws JspTagException {
        return i.next();
    }
    
    protected boolean hasNext() throws JspTagException {
        return i.hasNext();
    }

    protected void prepare() throws JspTagException {
        ContentManagementService cms = new ContentManagementService((HttpServletRequest)pageContext.getRequest());
        List<WorkList<Content>> worklist = cms.getMyContentList();

        List<Content> content = new ArrayList<Content>();
        for (WorkList<Content> w : worklist) {
            if (w.getDescription().equalsIgnoreCase("draft")) {
                content = w;
            }
        }

        i = content.iterator();
    }
}
