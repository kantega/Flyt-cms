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

import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.org.OrganizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetOrgUnitTag extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(GetOrgUnitTag.class);

    private String name = "currentorgunit";
    private String orgUnitId = null;
    private static List<OrganizationManager> organizationManagers;

    @Override
    public void setPageContext(PageContext pageContext) {
        super.setPageContext(pageContext);
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
        Collection<OrganizationManager> values = context.getBeansOfType(OrganizationManager.class).values();
        organizationManagers = new ArrayList<>(values);

    }

    public int doStartTag() throws JspException {
        if (orgUnitId == null || orgUnitId.trim().length() == 0) {
            return SKIP_BODY;
        }

        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

            OrgUnit orgUnit = null;

            if (organizationManagers != null && organizationManagers.size() > 0) {
                for (OrganizationManager organizationManager : organizationManagers) {
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
            log.error("", e);
            throw new JspTagException(e);
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


