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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.api.taglibs.content.GetAttributeCommand;
import no.kantega.publishing.client.device.DeviceCategoryDetector;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.attributes.*;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.Cropping;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.MultimediaTagCreator;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.common.util.TemplateMacroHelper;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static no.kantega.publishing.api.ContentUtil.tryGetFromRequest;

/**
 *
 */
public final class AttributeTagHelper {
    private static final Logger log = LoggerFactory.getLogger(AttributeTagHelper.class);
    public final static String COLLECTION_PAGE_VAR = "aksess_collection_";
    public final static String REPEATER_CONTENT_OBJ_PAGE_VAR = "aksess_repeater_contentObj_";
    public final static String REPEATER_OFFSET_PAGE_VAR = "aksess_repeater_offset_";
    private static SiteCache siteCache;
    private static ContentIdHelper contentIdHelper;

    public static Content getContent(PageContext pageContext, String collection, String contentId) throws SystemException, NotAuthorizedException {
        return getContent(pageContext, collection, contentId, null);
    }
    public static Content getContent(PageContext pageContext, String collection, String contentId, String repeaterName) throws SystemException, NotAuthorizedException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        Content content = null;
        if(contentIdHelper == null){
            contentIdHelper = RootContext.getInstance().getBean(ContentIdHelper.class);
        }
        try {
            if (contentId == null) {
                if (collection != null) {
                    content = (Content)pageContext.getAttribute(COLLECTION_PAGE_VAR + collection);
                } else if (repeaterName != null) {
                    content = (Content)pageContext.getAttribute(REPEATER_CONTENT_OBJ_PAGE_VAR + repeaterName);
                } else {
                    ContentManagementService cs = new ContentManagementService(request);

                    // Normalt sett vil denne ligge i requesten
                    content = tryGetFromRequest(request);
                    if (content == null) {
                        // Hent denne siden
                        content = cs.getContent(contentIdHelper.fromRequest(request), true);
                        RequestHelper.setRequestAttributes(request, content);
                    }

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
                            content = tryGetFromRequest(request);
                            if (content == null) {
                                // Hent denne siden
                                content = cs.getContent(contentIdHelper.fromRequest(request), true);
                                RequestHelper.setRequestAttributes(request, content);
                            }
                        } else {
                            content = (Content)pageContext.getAttribute(COLLECTION_PAGE_VAR + collection);
                        }

                        if (content != null) {
                            // Get parent page, next, previous page etc
                            ContentIdentifier cid = contentIdHelper.findRelativeContentIdentifier(content, contentId);
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
                        // Det er angitt en contentid som må slås opp
                        ContentIdentifier cid = new ContentIdentifier();
                        try {
                            if (contentId.contains(",")) {
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
                            cid = contentIdHelper.fromRequestAndUrl(request, contentId);
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
     * Henter en attributt for angitt objekt, både faste attributter som f.eks publishdate og vanlige attributter.
     * Kutter lengde på innhold hvis nødvendig etc.
     * Leter etter attributt på alle nivåene overfor hvis inheritFromAncestors = true
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
     * Henter en attributt for angitt objekt, både faste attributter som f.eks publishdate og vanlige attributter.
     * Kutter lengde på innhold hvis nødvendig etc.
     * Leter etter attributt på alle nivåene overfor hvis inheritFromAncestors = true
     * @param content
     * @param cmd
     * @return
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    public static String getAttribute(SecuritySession securitySession, Content content, GetAttributeCommand cmd, boolean inheritFromAncestors) throws SystemException, NotAuthorizedException {
        String value = getAttribute(content, cmd);
        if ((value == null || value.length() == 0) && (content != null && inheritFromAncestors)) {
            // Fant ikke verdi på dette nivået, prøver å lete lengre opp
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
     * Henter en attributt for angitt objekt, både faste attributter som f.eks publishdate og vanlige attributter.
     * Kutter lengde på innhold hvis nødvendig etc.
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
                    Locale locale = Language.getLanguageAsLocale(content.getLanguage());
                    NumberAttribute number = (NumberAttribute)attr;
                    if (cmd.getFormat() != null && cmd.getFormat().length() > 0) {
                        result = number.getValue(cmd.getFormat(), locale);
                    } else {
                        result = number.getValue();
                    }
                } else if(attr instanceof MediaAttribute) {
                    MediaAttribute media = (MediaAttribute)attr;

                    if (cmd.getProperty().equalsIgnoreCase(AttributeProperty.HTML)) {
                        Multimedia mm = media.getMultimedia();
                        if (mm != null) {
                            result = MultimediaTagCreator.mm2HtmlTag(mm, null, cmd.getWidth(), cmd.getHeight(), cmd.getCropping(), cmd.getCssClass());
                        }
                    } else {
                        result = media.getProperty(cmd.getProperty());

                        if (cmd.getProperty().equalsIgnoreCase(AttributeProperty.URL)) {
                            if (cmd.getWidth() != -1) {
                                result += !result.contains("?") ? "?" : "&amp;";
                                result += "width=" + cmd.getWidth();
                            }
                            if (cmd.getHeight() != -1) {
                                result += !result.contains("?") ? "?" : "&amp;";
                                result += "height=" + cmd.getHeight();
                            }

                            if (cmd.getCropping() != Cropping.CONTAIN){
                                result += !result.contains("?") ? "?" : "&amp;";
                                result +="cropping=" + cmd.getCropping().getTypeAsString();
                            }
                        }

                    }

                    // Angi om bilde / medieobjekt skal vises inline eller lastes ned
                    if (cmd.getContentDisposition() != null && AttributeProperty.URL.equalsIgnoreCase(cmd.getProperty())) {
                        result = result + "&contentdisposition=" + cmd.getContentDisposition();
                    }
                } else {
                    result = attr.getProperty(cmd.getProperty());



                }

                if (attr instanceof TextAttribute) {
                    isTextAttribute = true;
                }
            } else {
                switch (name) {
                    case ContentProperty.TITLE:
                        result = content.getTitle();
                        isTextAttribute = true;
                        break;
                    case ContentProperty.ID:
                        result = String.valueOf(content.getAssociation().getId());
                        break;
                    case ContentProperty.CONTENTID:
                        result = String.valueOf(content.getId());
                        break;
                    case ContentProperty.NUMBER_OF_VIEWS:
                        result = String.valueOf(content.getAssociation().getNumberOfViews());
                        break;
                    case ContentProperty.URL:
                        result = content.getUrl();
                        break;
                    case ContentProperty.ALIAS:
                        result = content.getAlias();
                        break;
                    case ContentProperty.DESCRIPTION:
                        result = content.getDescription();
                        if (result != null) {
                            result = StringHelper.replace(result, "\"" + Aksess.VAR_WEB + "\"/", Aksess.getContextPath() + "/");
                            result = StringHelper.replace(result, Aksess.VAR_WEB + "/", Aksess.getContextPath() + "/");
                        }
                        isTextAttribute = true;
                        break;
                    case ContentProperty.ALT_TITLE:
                        result = content.getAltTitle();
                        isTextAttribute = true;
                        break;
                    case ContentProperty.KEYWORDS:
                        result = content.getKeywords();
                        isTextAttribute = true;
                        break;
                    case ContentProperty.IMAGE:
                        MediaAttribute media = new MediaAttribute();
                        media.setValue(content.getImage());
                        if (cmd.getProperty().equalsIgnoreCase(AttributeProperty.HTML)) {
                            Multimedia mm = media.getMultimedia();
                            if (mm != null) {
                                result = MultimediaTagCreator.mm2HtmlTag(mm, null, cmd.getWidth(), cmd.getHeight(), cmd.getCropping(), cmd.getCssClass());
                            }
                        } else {
                            result = media.getProperty(cmd.getProperty());
                        }
                        break;
                    case ContentProperty.PUBLISH_DATE:
                    case ContentProperty.EXPIRE_DATE:
                    case ContentProperty.LAST_MODIFIED:
                    case ContentProperty.REVISION_DATE:
                    case ContentProperty.LAST_MAJOR_CHANGE:
                        Date date = null;
                        if (cmd.getFormat() == null) {
                            cmd.setFormat(Aksess.getDefaultDateFormat());
                        }

                        switch (name) {
                            case ContentProperty.PUBLISH_DATE:
                                date = content.getPublishDate();
                                break;
                            case ContentProperty.EXPIRE_DATE:
                                date = content.getExpireDate();
                                break;
                            case ContentProperty.LAST_MODIFIED:
                                date = content.getLastModified();
                                break;
                            case ContentProperty.LAST_MAJOR_CHANGE:
                                date = content.getLastMajorChange();
                                break;
                            case ContentProperty.REVISION_DATE:
                                date = content.getRevisionDate();
                                break;
                        }
                        if (date != null) {
                            Locale locale = Language.getLanguageAsLocale(content.getLanguage());
                            DateFormat df = new SimpleDateFormat(cmd.getFormat(), locale);
                            result = df.format(date);
                        }
                        break;
                    case ContentProperty.MODIFIED_BY:
                        result = content.getModifiedBy();
                        break;
                    case ContentProperty.LAST_MAJOR_CHANGE_BY:
                        result = content.getLastMajorChangeBy();
                        break;
                    case ContentProperty.PUBLISHER:
                        result = content.getPublisher();
                        break;
                    case ContentProperty.OWNER:
                        result = content.getOwner();
                        break;
                    case ContentProperty.OWNERPERSON:
                        result = content.getOwnerPerson();
                        break;
                    case ContentProperty.CHANGE_DESCRIPTION:
                        result = content.getChangeDescription();
                        break;
                    case ContentProperty.RATING_SCORE:
                        result = String.valueOf(content.getRatingScore());
                        break;
                    case ContentProperty.NUMBER_OF_RATINGS:
                        result = String.valueOf(content.getNumberOfRatings());
                        break;
                    case ContentProperty.NUMBER_OF_COMMENTS:
                        result = String.valueOf(content.getNumberOfComments());
                        break;
                    case ContentProperty.DISPLAY_TEMPLATE:
                        result = DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId()).getName();
                        break;
                    case ContentProperty.DISPLAY_TEMPLATE_ID:
                        result = DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId()).getPublicId();
                        break;
                    case ContentProperty.VERSION:
                        result = Integer.toString(content.getVersion());
                        break;
                }
            }

            if (result != null && result.contains("$")) {
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
                    log.error("", e);
                }
            } else {
                //Alias
                try {
                    ContentIdentifier contentIdentifier = contentIdHelper.fromRequestAndUrl(request, id);
                    associationId = contentIdentifier.getAssociationId();
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
        return associationId;
    }

    public static String replaceMacros(String url, PageContext pageContext) throws SystemException, NotAuthorizedException {
        setSiteCacheIfNull();
        if (url != null && pageContext != null) {
            if (TemplateMacroHelper.containsMacro(url)) {
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

                DeviceCategoryDetector deviceCategoryDetector = new DeviceCategoryDetector();

                no.kantega.publishing.api.model.Site site;
                Integer language = Language.NORWEGIAN_BO;

                Content content = AttributeTagHelper.getContent(pageContext, null, null);
                if (content != null) {
                    language = content.getLanguage();
                    site = siteCache.getSiteById(content.getAssociation().getSiteId());
                } else {
                    site = siteCache.getSiteByHostname(pageContext.getRequest().getServerName());
                    if(site == null){
                        site = siteCache.getDefaultSite();
                    }
                }

                url = TemplateMacroHelper.replaceMacros(url, site, deviceCategoryDetector.getUserAgentDeviceCategory(request), language);
            }
        }

        return url;
    }

    private static String getCacheKeyPrefix(HttpServletRequest request) {
        Content tmp = tryGetFromRequest(request);
        String key = "aksess_content";
        if (tmp != null) {
            key = key + tmp.getId();
        }
        return key;
    }


    public static String getAttributeName(PageContext pageContext, String attributeName, String repeaterName) {
        String name = attributeName;
        if (repeaterName != null) {
            Integer offset = (Integer)pageContext.getAttribute(REPEATER_OFFSET_PAGE_VAR + repeaterName);
            if (offset == null) {
                log.error( "Returning first element - <aksess:getattribute repeater=" + repeaterName + "> must be used inside a <aksess:repeatattributes name=" + repeaterName + "> tag");
                name = repeaterName + "[0]." + attributeName;
            } else {
                name = repeaterName + "[" + offset + "]." + attributeName;
            }

        }
        return name;
    }

    private static void setSiteCacheIfNull() {
        if(siteCache == null){
            siteCache = RootContext.getInstance().getBean(SiteCache.class);
        }
    }
}
