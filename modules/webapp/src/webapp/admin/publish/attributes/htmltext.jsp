<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ page import="no.kantega.commons.configuration.Configuration,
                 no.kantega.commons.log.Log,
                 no.kantega.commons.util.URLHelper"%>
<%@ page import="no.kantega.publishing.admin.AdminRequestParameters"%>
<%@ page import="no.kantega.publishing.admin.content.spellcheck.SpellcheckerService"%>
<%@ page import="no.kantega.publishing.admin.content.util.HTMLEditorHelper"%>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.cache.SiteCache"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.common.data.Site" %>
<%@ page import="no.kantega.publishing.common.data.attributes.HtmltextAttribute" %>
<%@ page import="no.kantega.publishing.common.data.enums.Language" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
<%@ page import="no.kantega.publishing.spring.RootContext" %>
<%@ page import="java.util.Locale" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%
    HtmltextAttribute attribute = (HtmltextAttribute)request.getAttribute("attribute");
    Content   content   = (Content)request.getAttribute("content");
    String    fieldName = (String)request.getAttribute("fieldName");
    String value = attribute.getValue();
    HTMLEditorHelper helper = new HTMLEditorHelper();
    value = helper.preEditFilter(value, URLHelper.getRootURL(request));

    Configuration conf = Aksess.getConfiguration();

    boolean isMiniAdminMode = (request.getAttribute(AdminRequestParameters.MINI_ADMIN_MODE) != null);
    String confPrefix = "editor.";
    if (isMiniAdminMode) {
        confPrefix += attribute.getMiniFeatureSet();
    } else {
        confPrefix += attribute.getFeatureSet();
    }
    confPrefix += ".";

    Site site = SiteCache.getSiteById(content.getAssociation().getSiteId());

    String plugins = conf.getString(confPrefix + "plugins");
    if (plugins == null) {
        plugins = conf.getString("editor.default.plugins");
    }

    String buttons = conf.getString(confPrefix + "buttons");
    if (buttons == null) {
        buttons = conf.getString("editor.default.buttons");
        Log.error("htmltext.jsp", "Button set not defined:" + confPrefix + "buttons", null, null);
    }
    String[] buttonRows = null;
    if (buttons != null) {
        buttonRows = buttons.split("<>");
    }

    String heading = conf.getString(confPrefix + "heading");
    if (heading == null) {
        heading = conf.getString("editor.default.heading");
    }

    String valid_elements = conf.getString(confPrefix+"valid_elements");
    if (valid_elements == null) {
        valid_elements = conf.getString("editor.default.valid_elements");
    }

    boolean hasHtmlEditorRole = false;
    String[] htmlEditorRole = Aksess.getHtmlEditorRoles();
    if (htmlEditorRole != null) {
        ContentManagementService cms = new ContentManagementService(request);
        SecuritySession securitySession = cms.getSecuritySession();
        hasHtmlEditorRole = securitySession.isUserInRole(htmlEditorRole);
    }

    // Let etter /css/site/editor.css og /site/css/editor.css
    String cssPath = "/css" + site.getAlias() + attribute.getCss();

    if (pageContext.getServletContext().getResource(cssPath) == null) {
        cssPath = site.getAlias() + "css/" + attribute.getCss();
    }
    request.setAttribute("cssPath", cssPath);

    int width = attribute.getWidth();
    if (width == -1) width = Aksess.getConfiguration().getInt("editor.default.width", 600);
    request.setAttribute("attributeWidth", width);

    int height = attribute.getHeight();
    if (height == -1) height = Aksess.getConfiguration().getInt("editor.default.height", 350);
    request.setAttribute("attributeHeight", height);

%>
<script type="text/javascript">
    var miniAdminMode = <%=isMiniAdminMode%>;
</script>

<div class="inputs">
    <TEXTAREA name="<%=fieldName%>" id="<%=fieldName%>" cols="80" rows="20" style="width: ${attributeWidth}px; height: ${attributeHeight}px"><%=value%></TEXTAREA><BR>

    <script type="text/javascript">
        tinyMCE_GZ.init({
            plugins : '<%=plugins%>',
            themes : 'simple,advanced',
            languages : 'en,no',
            disk_cache : true,
            debug : false
        });
    </script>

    <script type="text/javascript">
        var plugins = '<%=plugins%>';
        var buttonRows = [];
        <% for (String row : buttonRows) { %>
            buttonRows.push('<%=row%>');
        <% } %>

        var options = {
            // General options
            language : '<%=Aksess.getDefaultAdminLocale().getLanguage().toLowerCase()%>', // en / no
            mode : "exact",
            elements : "<%=fieldName%>",
            theme : "advanced",

            skin : "o2k7",
            skin_variant : "silver",
            button_tile_map : true,
            plugins : plugins,

            valid_elements : '<%=valid_elements%>',

            width : "${attributeWidth}",
            height : "${attributeHeight}",

            // Theme options
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_statusbar_location : "none",
            theme_advanced_resizing : false,
            theme_advanced_blockformats : "p,address,pre,<%=heading%>",

            // Plugin options
            <%
                SpellcheckerService service = (SpellcheckerService)RootContext.getInstance().getBean("aksessSpellCheckerService");
                Locale contentLocale = Language.getLanguageAsLocale(content.getLanguage());
                if (service.supportsLocale(contentLocale)) {
            %>
            spellchecker_languages : "+<%=contentLocale.getDisplayName()%>=<%=contentLocale.toString()%>",
            spellchecker_rpc_url : "<aksess:geturl url="/admin/publish/Spellcheck.action"/>",
            <%
                }
            %>

            // Path to editor.css
            content_css : "${pageContext.request.contextPath}${cssPath}"
        };

        for (var i = 0, n = buttonRows.length; i < n; i++) {
            options['theme_advanced_buttons' + (i+1)] = buttonRows[i];
        }
        for (var i = buttonRows.length; i < 3; i++) {
            options['theme_advanced_buttons' + (i+1)] = "";
        }

        tinyMCE.init(options);
    </script>
</div>