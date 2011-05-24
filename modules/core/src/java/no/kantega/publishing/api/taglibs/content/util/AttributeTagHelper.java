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

package no.kantega.publishing.api.taglibs.content.util;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.StringHelper;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.taglibs.content.GetAttributeCommand;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.attributes.*;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.MultimediaTagCreator;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.security.SecuritySession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public final class AttributeTagHelper {
    public static Content getContent(PageContext pageContext, String collection, String contentId) throws SystemException, NotAuthorizedException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        Content content = null;

        try {
            if (contentId == null) {
                if (collection == null) {
                    ContentManagementService cs = new ContentManagementService(request);

                    // Normalt sett vil denne ligge i requesten
                    content = (Content)request.getAttribute("aksess_this");
                    if (content == null) {
                        // Hent denne siden
                        content = cs.getContent(new ContentIdentifier(request), true);
                        RequestHelper.setRequestAttributes(request, content);
                    }
                } else {
                    content = (Content)pageContext.getAttribute("aksess_collection_" + collection);
                }

            } else if (contentId.indexOf("..") == 0 || contentId.equalsIgnoreCase("group") || contentId.equalsIgnoreCase("next") || contentId.equalsIgnoreCase("previous") || contentId.startsWith("/+")) {
                ContentManagementService cs = new ContentManagementService(request);
                try {
                    if (collection == null) {
                        String key = getCacheKeyPrefix(request);
                        content = (Content)request.getAttribute(key + contentId);
                    }

                    if (content == null) {
                        if (collection == null) {
                            // Normalt sett vil denne ligge i requesten
                            content = (Content)request.getAttribute("aksess_this");
                            if (content == null) {
                                // Hent denne siden
                                content = cs.getContent(new ContentIdentifier(request), true);
                                RequestHelper.setRequestAttributes(request, content);
                            }
                        } else {
                            content = (Content)pageContext.getAttribute("aksess_collection_" + collection);
                        }

                        if (content != null) {
                            // Get parent page, next, previous page etc
                            ContentIdentifier cid = ContentIdHelper.findRelativeContentIdentifier(content, contentId);
                            if (cid != null) {
                                // Next or previous page found
                                content = cs.getContent(cid, false);
                                if (collection == null) {
                                    request.setAttribute(getCacheKeyPrefix(request) + contentId, content);
                                }
                            } else {
                                // Page was not found
                                content = null;
                            }
                        }
                    }
                } catch (NotAuthorizedException e) {
                    // Viser ikke elementet dersom brukeren ikke har tilgang til det
                    return null;
                }

            } else {
                if (contentId.length() > 0) {
                    try {
                        // Det er angitt en contentid som m� sl�s opp
                        ContentIdentifier cid = new ContentIdentifier();
                        try {
                            if (contentId.indexOf(",") != -1) {
                                String contentIds[] = contentId.split(",");
                                if (contentIds.length > 0) {
                                    int random = (int)Math.floor((contentIds.length*Math.random()));
                                    contentId = contentIds[random];
                                }
                            }
                            int id = Integer.parseInt(contentId);
                            cid.setAssociationId(id);
                        } catch (NumberFormatException e) {
                            if (contentId.charAt(0) != '/') contentId = "/" + contentId;
                            if (contentId.charAt(contentId.length() - 1) != '/') contentId = contentId + "/";
                            cid = new ContentIdentifier(request, contentId);
                        }

                        content = (Content)request.getAttribute("aksess_content" + cid.getAssociationId());
                        if (content == null) {
                            RequestParameters param = new RequestParameters(request);
                            int language = param.getInt("language");
                            if (language != -1) {
                                cid.setLanguage(language);
                            }
                            ContentManagementService cs = new ContentManagementService(request);
                            content = cs.getContent(cid);
                            request.setAttribute("aksess_content" + cid.getAssociationId(), content);
                        }

                    } catch (NotAuthorizedException e) {
                        // Viser ikke elementet dersom brukeren ikke har tilgang til det
                        return null;
                    }
                }
            }
        } catch (ContentNotFoundException e) {
            // Content == null
        }

        return content;
    }

    /**
     * Henter en attributt for angitt objekt, b�de faste attributter som f.eks publishdate og vanlige attributter.
     * Kutter lengde p� innhold hvis n�dvendig etc.
     * Leter etter attributt p� alle niv�ene overfor hvis inheritFromAncestors = true
     * @param content
     * @param cmd
     * @return
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    @Deprecated
    public static String getAttribute(Content content, GetAttributeCommand cmd, boolean inheritFromAncestors) throws SystemException, NotAuthorizedException {
        return getAttribute(SecuritySession.createNewAdminInstance(), content, cmd, inheritFromAncestors);
    }

    /**
     * Henter en attributt for angitt objekt, b�de faste attributter som f.eks publishdate og vanlige attributter.
     * Kutter lengde p� innhold hvis n�dvendig etc.
     * Leter etter attributt p� alle niv�ene overfor hvis inheritFromAncestors = true
     * @param content
     * @param cmd
     * @return
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    public static String getAttribute(SecuritySession securitySession, Content content, GetAttributeCommand cmd, boolean inheritFromAncestors) throws SystemException, NotAuthorizedException {
        String value = getAttribute(content, cmd);
        if ((value == null || value.length() == 0) && (content != null && inheritFromAncestors)) {
            // Fant ikke verdi p� dette niv�et, pr�ver � lete lengre opp
            Association a = content.getAssociation();
            if (a != null && a.getPath().length() > 2) {
                String contentList = a.getPath().substring(1, a.getPath().length() - 1);
                contentList = StringHelper.replace(contentList, "/", ",");

                ContentQuery query = new ContentQuery();
                query.setContentList(contentList);
                ContentManagementService cms = new ContentManagementService(securitySession);
                List parents = cms.getContentList(query, -1, new SortOrder(ContentProperty.PRIORITY, false), true, false);
                for (int i = parents.size() - 1; i >= 0 ; i--) {
                    Content parent =  (Content)parents.get(i);
                    value = getAttribute(parent, cmd);
                    if (value != null && value.length() > 0) {
                        return value;
                    }
                }
            }
        }
        return value;
    }



    /**
     * Henter en attributt for angitt objekt, b�de faste attributter som f.eks publishdate og vanlige attributter.
     * Kutter lengde p� innhold hvis n�dvendig etc.
     * @param content
     * @param cmd
     * @return
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    public static String getAttribute(Content content, GetAttributeCommand cmd) throws SystemException, NotAuthorizedException {
        boolean isTextAttribute = false;

        String result = "";

        String name = cmd.getName();
        int width = cmd.getWidth();
        int height = cmd.getHeight();
        String cssClass = cmd.getCssClass();

        if (cmd.getFormat() == null) {
            cmd.setFormat(Aksess.getDefaultDateFormat());
        }

        if (content != null) {
            Attribute attr = content.getAttribute(name, cmd.getAttributeType());
            if (attr != null && attr.getValue() != null && attr.getValue().length() > 0) {
                if (attr instanceof DateAttribute) {
                    if (cmd.getFormat() == null) {
                        cmd.setFormat(Aksess.getDefaultDateFormat());
                    }
                    Locale locale = Language.getLanguageAsLocale(content.getLanguage());
                    DateAttribute date = (DateAttribute)attr;
                    result = date.getValue(cmd.getFormat(), locale);
                } else if(attr instanceof NumberAttribute) {
                    NumberAttribute number = (NumberAttribute)attr;
                    if (cmd.getFormat() != null && cmd.getFormat().length() > 0) {
                        result = number.getValue(cmd.getFormat());
                    } else {
                        result = number.getValue();
                    }
                } else if(attr instanceof MediaAttribute || attr instanceof ImageAttribute) {
                    MediaAttribute media = (MediaAttribute)attr;

                    if (cmd.getProperty().equalsIgnoreCase(AttributeProperty.HTML)) {
                        Multimedia mm = media.getMultimedia();
                        if (mm != null) {
                            result = MultimediaTagCreator.mm2HtmlTag(mm, null, cmd.getWidth(), cmd.getHeight(), cmd.getCssClass());
                        }
                    } else {
                        result = media.getProperty(cmd.getProperty());
                    }

                    // Angi om bilde / medieobjekt skal vises inline eller lastes ned
                    if (cmd.getContentDisposition() != null && AttributeProperty.URL.equalsIgnoreCase(cmd.getProperty())) {
                        result = result + "&contentdisposition=" + cmd.getContentDisposition();
                    }
                } else if (attr instanceof TopicAttribute){
                    TopicAttribute ta = (TopicAttribute)attr;
                    if (cmd.getProperty().equalsIgnoreCase(AttributeProperty.NAME)){
                        result = ta.getValueAsTopic().getBaseName();
                    } else if (cmd.getProperty().equalsIgnoreCase(AttributeProperty.TOPICID)){
                        result = ta.getTopicId();
                    } else if (cmd.getProperty().equalsIgnoreCase(AttributeProperty.TOPICMAPID)){
                        result = String.valueOf(ta.getTopicMapId());
                    } else {
                        result = attr.getProperty(cmd.getProperty());
                    }
                } else {
                    result = attr.getProperty(cmd.getProperty());
                }

                if (attr instanceof TextAttribute) {
                    isTextAttribute = true;
                }
            } else {
                if (name.equals(ContentProperty.TITLE)) {
                    result = content.getTitle();
                    isTextAttribute = true;
                } else if (name.equals(ContentProperty.ID)) {
                    result = "" + content.getAssociation().getId();
                } else if (name.equals(ContentProperty.CONTENTID)) {
                    result = "" + content.getId();
                } else if (name.equals(ContentProperty.NUMBER_OF_VIEWS)) {
                    result = "" + content.getAssociation().getNumberOfViews();
                } else if (name.equals(ContentProperty.URL)) {
                    result = content.getUrl();
                } else if (name.equals(ContentProperty.ALIAS)) {
                    result = content.getAlias();
                } else if (name.equals(ContentProperty.DESCRIPTION)) {
                    result = content.getDescription();
                    if (result != null) {
                        result = StringHelper.replace(result, "\"" + Aksess.VAR_WEB + "\"/", Aksess.getContextPath() + "/");
                        result = StringHelper.replace(result, Aksess.VAR_WEB + "/", Aksess.getContextPath() + "/");
                    }
                    isTextAttribute = true;
                } else if (name.equals(ContentProperty.ALT_TITLE)) {
                    result = content.getAltTitle();
                    isTextAttribute = true;
                } else if (name.equals(ContentProperty.KEYWORDS)) {
                    result = content.getKeywords();
                    isTextAttribute = true;
                } else if (name.equals(ContentProperty.IMAGE)) {
                    MediaAttribute media = new MediaAttribute();
                    media.setValue(content.getImage());
                    if (cmd.getProperty().equalsIgnoreCase(AttributeProperty.HTML)) {
                        Multimedia mm = media.getMultimedia();
                        if (mm != null) {
                            result = MultimediaTagCreator.mm2HtmlTag(mm, null, cmd.getWidth(), cmd.getHeight(), cmd.getCssClass());
                        }
                    } else {
                        result = media.getProperty(cmd.getProperty());
                    }
                } else if (name.equals(ContentProperty.PUBLISH_DATE) || name.equals(ContentProperty.EXPIRE_DATE)|| name.equals(ContentProperty.LAST_MODIFIED) || name.equals(ContentProperty.REVISION_DATE) || name.equals(ContentProperty.LAST_MAJOR_CHANGE)) {
                    Date date = null;
                    if (cmd.getFormat() == null) {
                        cmd.setFormat(Aksess.getDefaultDateFormat());
                    }

                    if (name.equals(ContentProperty.PUBLISH_DATE)) {
                        date = content.getPublishDate();
                    } else if (name.equals(ContentProperty.EXPIRE_DATE)) {
                        date = content.getExpireDate();
                    } else if (name.equals(ContentProperty.LAST_MODIFIED)) {
                        date = content.getLastModified();
                    } else if (name.equals(ContentProperty.LAST_MAJOR_CHANGE)) {
                        date = content.getLastMajorChange();
                    }  else if (name.equals(ContentProperty.REVISION_DATE)) {
                        date = content.getRevisionDate();
                    }
                    if (date != null) {
                        Locale locale = Language.getLanguageAsLocale(content.getLanguage());
                        DateFormat df = new SimpleDateFormat(cmd.getFormat(), locale);
                        result = df.format(date);
                    }
                } else if(name.equals(ContentProperty.MODIFIED_BY)) {
                    result = content.getModifiedBy();
                } else if(name.equals(ContentProperty.LAST_MAJOR_CHANGE_BY)) {
                    result = content.getLastMajorChangeBy();
                } else if(name.equals(ContentProperty.PUBLISHER)) {
                    result = content.getPublisher();
                } else if(name.equals(ContentProperty.OWNER)) {
                    result = content.getOwner();
                } else if(name.equals(ContentProperty.OWNERPERSON)) {
                    result = content.getOwnerPerson();
                } else if (name.equals(ContentProperty.CHANGE_DESCRIPTION)) {
                    result = content.getChangeDescription();
                } else if (name.equals(ContentProperty.RATING_SCORE)) {
                    result = "" + content.getRatingScore();
                } else if (name.equals(ContentProperty.NUMBER_OF_RATINGS)) {
                    result = "" + content.getNumberOfRatings();
                } else if (name.equals(ContentProperty.NUMBER_OF_COMMENTS)) {
                    result = "" + content.getNumberOfComments();
                } else if(name.equals(ContentProperty.DISPLAY_TEMPLATE)) {
                    result = DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId()).getName();
                } else if(name.equals(ContentProperty.DISPLAY_TEMPLATE_ID)) {
                    result = DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId()).getPublicId();
                } else if(name.equals(ContentProperty.VERSION)) {
                    result = Integer.toString(content.getVersion());
                }
            }

            if (result != null && result.indexOf("$") != -1) {
                result = result.replaceAll("\\$contextId\\$", "" + content.getAssociation().getAssociationId());
            }
        }

        if (result == null) {
            result = "";
        }


        int maxLength = cmd.getMaxLength();
        // Cut text after N characters
        if (maxLength > 3 && isTextAttribute && result.length() > maxLength) {
            // Strip HTML-tags
            result = StringHelper.stripHtml(result);
            if(result.length() > maxLength) {
                result = result.substring(0, maxLength - 3) + "...";
            }
        }

        return result;
    }

    public static int getAssociationIdFromIdOrAlias(String id, HttpServletRequest request) {
        int associationId = -1;
        if (id != null && id.length() > 0) {
            if (Character.isDigit(id.charAt(0))) {
                try{
                    associationId = Integer.parseInt(id);
                } catch(NumberFormatException e){
                    Log.error("AttributeTagHelper", e, null, null);
                }
            } else {
                //Alias
                try {
                    associationId = new ContentIdentifier(request, id).getAssociationId();
                } catch (Exception e) {
                }
            }
        }
        return associationId;
    }




    public static String replaceMacros(String url, PageContext pageContext) throws SystemException, NotAuthorizedException {
        if (url != null && pageContext != null) {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            if (url.indexOf("$SITE") != -1 || url.indexOf("$LANGUAGE") != -1) {
                String site = "";
                Integer language = Language.NORWEGIAN_BO;

                Content c = AttributeTagHelper.getContent(pageContext, null, null);
                if (c != null) {
                    site = (String)request.getAttribute("aksess_site");
                    language = (Integer)request.getAttribute("aksess_language");
                } else {
                    Site s = SiteCache.getSiteByHostname(pageContext.getRequest().getServerName());
                    if (s != null) {
                        site = s.getAlias();
                    }
                }

                String lang = Language.getLanguageAsISOCode(language);

                url = url.replaceAll("\\$SITE", site.substring(0, site.length() - 1));
                url = url.replaceAll("\\$LANGUAGE", lang);
            }
        }

        return url;
    }

    private static String getCacheKeyPrefix(HttpServletRequest request) {
        Content tmp = (Content)request.getAttribute("aksess_this");
        String key = "aksess_content";
        if (tmp != null) {
            key = key + tmp.getId();
        }
        return key;
    }
}
