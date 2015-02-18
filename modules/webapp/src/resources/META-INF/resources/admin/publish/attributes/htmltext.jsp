<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ page import="no.kantega.commons.configuration.Configuration,
                 no.kantega.commons.util.URLHelper"%>
<%@ page import="no.kantega.publishing.admin.AdminRequestParameters"%>
<%@ page import="no.kantega.publishing.admin.content.htmlfilter.HTMLEditorHelper"%>
<%@ page import="no.kantega.publishing.admin.content.spellcheck.SpellcheckerService"%>
<%@ page import="no.kantega.publishing.api.cache.SiteCache"%>
<%@ page import="no.kantega.publishing.api.content.Language"%>
<%@ page import="no.kantega.publishing.api.model.Site" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.attributes.HtmltextAttribute" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
<%@ page import="no.kantega.publishing.spring.RootContext" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
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
    Logger log = LoggerFactory.getLogger("no.kantega.openaksess.jsp.htmltext");
    HtmltextAttribute attribute = (HtmltextAttribute)request.getAttribute("attribute");
    Content   content   = (Content)request.getAttribute("content");
    String    fieldName = (String)request.getAttribute("fieldName");
    String value = attribute.getValue();
    value = HTMLEditorHelper.preEditFilter(value, URLHelper.getRootURL(request));

    Configuration conf = Aksess.getConfiguration();
    ApplicationContext context = RootContext.getInstance();

    boolean isMiniAdminMode = (request.getAttribute(AdminRequestParameters.MINI_ADMIN_MODE) != null);
    String confPrefix = "editor.";
    if (isMiniAdminMode) {
        confPrefix += attribute.getMiniFeatureSet();
    } else {
        confPrefix += attribute.getFeatureSet();
    }
    confPrefix += ".";

    Site site = context.getBean(SiteCache.class).getSiteById(content.getAssociation().getSiteId());

    String plugins = conf.getString(confPrefix + "plugins");
    if (plugins == null) {
        plugins = conf.getString("editor.default.plugins");
    }

    String buttons = conf.getString(confPrefix + "buttons");
    if (buttons == null) {
        buttons = conf.getString("editor.default.buttons");
        log.error("Button set not defined:" + confPrefix + "buttons");
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

    // Let etter /css/site/editor.css og /site/css/editor.css og /css/editor.css
    String siteAlias = site.getAlias();
    if (!siteAlias.endsWith("/")) {
        siteAlias += "/";
    }
    String cssPath = "/css" + siteAlias + attribute.getCss();

    if (pageContext.getServletContext().getResource(cssPath) == null) {
        cssPath = siteAlias + "css/" + attribute.getCss();
    }
    if (pageContext.getServletContext().getResource(cssPath) == null) {
        cssPath = "/css/" + attribute.getCss();
    }

    request.setAttribute("cssPath", cssPath);

    int width = attribute.getWidth();
    if (width == -1) width = conf.getInt("editor.default.width", -1);
    request.setAttribute("attributeWidth", width == -1 ? "100%" : width + "px");

    int height = attribute.getHeight();
    if (height == -1) height = conf.getInt("editor.default.height", 450);
    request.setAttribute("attributeHeight", height + "px");

%>
<script type="text/javascript">
    var miniAdminMode = <%=isMiniAdminMode%>;
</script>

<div class="inputs">
    <TEXTAREA name="<%=fieldName%>" id="<%=fieldName%>" cols="80" rows="20" style="width: ${attributeWidth}; height: ${attributeHeight}"><%=value%></TEXTAREA><BR>

    <script type="text/javascript">
        tinyMCE_GZ.init({
            plugins : '<%=plugins%>',
            themes : 'simple,advanced',
            languages : 'en,no',
            disk_cache : true,
            debug : false
        });
    </script>

    <aksess:getconfig key="editor.custom.javascript"/>

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
            theme_advanced_statusbar_location : "bottom",
            theme_advanced_path : false,
            theme_advanced_resizing : true,
            theme_advanced_resize_horizontal : false,
            theme_advanced_blockformats : "p,address,pre,<%=heading%>",
            <%
            if (!hasHtmlEditorRole) {
            %>
            theme_advanced_disable: "code",
            <%
            }
            %>

            // Plugin options
            <%
                SpellcheckerService service = context.getBean("aksessSpellCheckerService", SpellcheckerService.class);
                Locale contentLocale = Language.getLanguageAsLocale(content.getLanguage());
                if (service.supportsLocale(contentLocale)) {
            %>
            spellchecker_languages : "+<%=contentLocale.getDisplayName()%>=<%=contentLocale.toString()%>",
            spellchecker_rpc_url : "<aksess:geturl url="/admin/publish/Spellcheck.action"/>",
            <%
                }
            %>
            <aksess:getconfig key="editor.custom.tinymceparameters"/>
            // Path to editor.css
            content_css : "${pageContext.request.contextPath}${cssPath}",
            template_external_list_url : "${pageContext.request.contextPath}/aksess/js/editor_templates.jsp"
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
