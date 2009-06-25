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

package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.content.util.ValidatorHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.service.ContentManagementService;

import java.util.*;


public class SavePublishInfoAction extends AbstractSaveContentAction {
    public ValidationErrors saveRequestParameters(Content content, RequestParameters param, ContentManagementService aksessService) throws SystemException {
        ValidationErrors errors = new ValidationErrors();

        try {
            Date startDate = param.getDateAndTime("from", Aksess.getDefaultDateFormat());
            if (startDate != null) {
                // Can't set to null
                content.setPublishDate(startDate);
            }
        } catch(Exception e) {
            Map<String, Object> objects = new HashMap<String, Object>();
            objects.put("dateFormat", Aksess.getDefaultDateFormat());
            errors.add(null, "aksess.feil.dato", objects);
        }
        try {
            Date expireDate = param.getDateAndTime("end", Aksess.getDefaultDateFormat());
            content.setExpireDate(expireDate);
        } catch (Exception e) {
            Map<String, Object> objects = new HashMap<String, Object>();
            objects.put("dateFormat", Aksess.getDefaultDateFormat());
            errors.add(null, "aksess.feil.dato", objects);
        }

        int expireAction = param.getInt("expireaction");
        if (expireAction != -1) {
            content.setExpireAction(expireAction);
        }

        if (aksessService.getSecuritySession().isUserInRole(Aksess.getDeveloperRole())) {
            content.setLocked(param.getBoolean("locked"));
        }

        String alias = param.getString("alias", 62);
        if (alias != null) {
            content.setAlias(alias);
            if (alias.length() > 0) {
                // If alias contains . or = should not be modififed for historic reasons
                if (alias.indexOf(".") == -1 && alias.indexOf("=") == -1) {
                    /*
                    * Aliases are user specified URLs
                    * eg http://www.site.com/news/

                    * Alias always starts and ends with /
                    * Alias / is used for frontpage
                    */
                    if (alias.charAt(0) != '/') {
                        alias = "/" + alias;
                    }
                    if (alias.length() > 1) {
                        if (alias.charAt(alias.length()-1) != '/') {
                            alias = alias + "/";
                        }
                    }
                    alias = alias.toLowerCase();

                    content.setAlias(alias);
                }                

                ValidatorHelper.validateAlias(alias, content, errors);
            }
        }

        int templateId = param.getInt("displaytemplate");
        if (templateId != -1) {
            if (templateId != content.getDisplayTemplateId()) {                
                DisplayTemplate template = aksessService.getDisplayTemplate(templateId);
                if (template != null) {
                    content.setDisplayTemplateId(templateId);
                    content.setContentTemplateId(template.getContentTemplate().getId());
                    ContentTemplate mt = template.getMetaDataTemplate();
                    if (mt != null) {
                        content.setMetaDataTemplateId(mt.getId());
                    } else {
                        content.setMetaDataTemplateId(-1);
                    }
                }
            }
        }

        String sortlist = param.getString("sortlist");
        if (sortlist != null && sortlist.length() > 0) {
            List associations = new ArrayList();
            StringTokenizer tokens = new StringTokenizer(sortlist, ";");
            int i = 0;
            while (tokens.hasMoreTokens()) {
                i++;
                String tmp = tokens.nextToken();
                int uniqueId = Integer.parseInt(tmp);
                Association association = new Association();
                association.setId(uniqueId);
                association.setPriority(i);
                associations.add(association);
            }
            aksessService.setAssociationsPriority(associations);
        }

        return errors;
    }

    public String getEditPage() {
        return "editpublishinfo";
    }
}