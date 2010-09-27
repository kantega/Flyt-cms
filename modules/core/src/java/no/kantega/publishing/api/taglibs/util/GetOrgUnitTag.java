/*
 * Copyright 2010 Kantega AS
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

package no.kantega.publishing.api.taglibs.util;

import no.kantega.commons.log.Log;
import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.org.OrganizationManager;
import no.kantega.publishing.spring.RootContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Map;

public class GetOrgUnitTag extends TagSupport {

    private static final String SOURCE = "aksess.GetOrgUnitTag";
    private String name = "currentorgunit";
    private String orgUnitId = null;

    @SuppressWarnings("unchecked")
    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            Map<String, OrganizationManager> organizationManagers =  RootContext.getInstance().getBeansOfType(OrganizationManager.class);

            OrgUnit orgUnit = null;

            if (organizationManagers != null && organizationManagers.size() > 0) {
                for (OrganizationManager organizationManager : organizationManagers.values()) {
                    orgUnit = organizationManager.getUnitByExternalId(orgUnitId);
                    if (orgUnit != null) {
                        break;
                    }
                }
            }

            if (orgUnit != null) {
                request.setAttribute(name, orgUnit);
            }
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            throw new JspTagException(SOURCE + ":" + e.getMessage());
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        orgUnitId = null;
        name = "currentorgunit";
        return EVAL_PAGE;
    }

    public void setOrgunitid(String orgUnitId) {
        this.orgUnitId = orgUnitId;
    }

    public void setName(String name) {
        this.name = name;
    }

}


