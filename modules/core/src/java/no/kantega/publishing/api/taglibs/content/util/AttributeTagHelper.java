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
import no.kantega.commons.util.StringHelper;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.taglibs.content.GetAttributeCommand;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.DateAttribute;
import no.kantega.publishing.common.data.attributes.MediaAttribute;
import no.kantega.publishing.common.data.attributes.TextAttribute;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.RequestHelper;

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
                        content = (Content)request.getAttribute("aksess_content" + contentId);
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
                                    request.setAttribute("aksess_content" + contentId, content);
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
     * Henter en attributt for angitt objekt, både faste attributter som f.eks publishdate og vanlige attributter.
     * Kutter lengde på innhold hvis nødvendig etc.
     * Leter etter attributt på alle nivåene overfor hvis inheritFromAncestors = true
     * @param content
     * @param cmd
     * @return
     * @throws SystemException
     * @throws NotAuthorizedException
     */
    public static String getAttribute(Content content, GetAttributeCommand cmd, boolean inheritFromAncestors) throws SystemException, NotAuthorizedException {
        String value = getAttribute(content, cmd);
        if ((value == null || value.length() == 0) && (content != null && inheritFromAncestors)) {
            // Fant ikke verdi på dette nivået, prøver å lete lengre opp
            Association a = content.getAssociation();
            if (a != null && a.getPath().length() > 2) {
                String contentList = a.getPath().substring(1, a.getPath().length() - 1);
                contentList = StringHelper.replace(contentList, "/", ",");

                ContentQuery query = new ContentQuery();
                query.setContentList(contentList);

                List parents = ContentAO.getContentList(query, -1, new SortOrder(ContentProperty.PRIORITY, false), true);
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
        int width = cmd.getWidth();
        int height = cmd.getHeight();
        String cssClass = cmd.getCssClass();

        if (cmd.getFormat() == null) {
            cmd.setFormat(Aksess.getDefaultDateFormat());
        }

        if (content != null) {
            if (name.equals(ContentProperty.TITLE)) {
                result = content.getTitle();
                isTextAttribute = true;
            } else if (name.equals(ContentProperty.ID)) {
                result = "" + content.getAssociation().getId();
            } else if (name.equals(ContentProperty.CONTENTID)) {
                result = "" + content.getId();
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
            } else if(name.equals(ContentProperty.DISPLAY_TEMPLATE)) {
                result = DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId()).getName();
            } else if(name.equals(ContentProperty.VERSION)) {
                result = Integer.toString(content.getVersion());
            } else if (name.equals(ContentProperty.IMAGE)) {
                MediaAttribute ma = new MediaAttribute();
                if (width != -1) {
                    ma.setMaxWidth(width);
                }
                if (height != -1) {
                    ma.setMaxHeight(height);
                }
                if (cssClass != null) {
                    ma.setCssclass(cssClass);
                }
                ma.setValue(content.getImage());
                result = ma.getProperty(cmd.getProperty());
            } else if (name.equals(ContentProperty.PUBLISH_DATE) || name.equals(ContentProperty.EXPIRE_DATE)|| name.equals(ContentProperty.LAST_MODIFIED) || name.equals(ContentProperty.REVISION_DATE)) {
                Date date = null;
                if (name.equals(ContentProperty.PUBLISH_DATE)) {
                    date = content.getPublishDate();
                } else if (name.equals(ContentProperty.EXPIRE_DATE)) {
                    date = content.getExpireDate();
                } else if (name.equals(ContentProperty.LAST_MODIFIED)) {
                    date = content.getLastModified();
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
            } else if(name.equals(ContentProperty.PUBLISHER)) {
                result = content.getPublisher();
            }else if(name.equals(ContentProperty.OWNER)) {
                result = content.getOwner();
            }else if(name.equals(ContentProperty.OWNERPERSON)) {
                result = content.getOwnerPerson();
            }else if (name.equals(ContentProperty.CHANGE_DESCRIPTION)) {
                result = content.getChangeDescription();
            } else {
                Attribute attr = content.getAttribute(name, cmd.getAttributeType());
                if (attr != null) {
                    if (attr instanceof DateAttribute) {
                        Locale locale = Language.getLanguageAsLocale(content.getLanguage());
                        DateAttribute date = (DateAttribute)attr;
                        result = date.getValue(cmd.getFormat(), locale);
                    } else if(attr instanceof MediaAttribute) {
                        MediaAttribute media = (MediaAttribute)attr;
                        if (width != -1) {
                            media.setMaxWidth(width);
                        }
                        if (height != -1) {
                            media.setMaxHeight(height);
                        }
                        if (cssClass != null) {
                            media.setCssclass(cssClass);
                        }
                        result = media.getProperty(cmd.getProperty());

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
        // Brukeren har angitt en maks lengde på tekst (f.eks ingress)
        if (maxLength > 3 && isTextAttribute && result.length() > maxLength) {
            // strip HTML-tags
            result = result.replaceAll("<(.|\\n)+?>", "");
            if(result.length() > maxLength)
            {
                result = result.substring(0, maxLength - 3) + "...";
            }
        }

        return result;
    }


    public static String replaceMacros(String url, PageContext pageContext) throws SystemException, NotAuthorizedException {
        if (url != null && pageContext != null) {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            if (url.indexOf("$SITE") != -1 || url.indexOf("$LANGUAGE") != -1) {
                String site = "";
                Integer language = new Integer(Language.NORWEGIAN_BO);

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

                String lang = Language.getLanguageAsISOCode(language.intValue());

                url = url.replaceAll("\\$SITE", site.substring(0, site.length() - 1));
                url = url.replaceAll("\\$LANGUAGE", lang);
            }
        }

        return url;
    }
}
