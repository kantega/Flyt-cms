<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ page import="no.kantega.commons.util.URLHelper,
                 no.kantega.publishing.admin.AdminRequestParameters"%>
<%@ page import="no.kantega.publishing.admin.content.htmlfilter.HTMLEditorHelper"%>
<%@ page import="no.kantega.publishing.api.cache.SiteCache"%>
<%@ page import="no.kantega.publishing.api.configuration.SystemConfiguration"%>
<%@ page import="no.kantega.publishing.api.model.Site"%>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.attributes.HtmltextAttribute" %>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
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
    // Init
    Logger log = LoggerFactory.getLogger("no.kantega.openaksess.jsp.htmltext");
    HtmltextAttribute attribute = (HtmltextAttribute)request.getAttribute("attribute");
    Content   content   = (Content)request.getAttribute("content");
    request.setAttribute("value", HTMLEditorHelper.preEditFilter(attribute.getValue(), URLHelper.getRootURL(request)));

    ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(application);
    SystemConfiguration conf = context.getBean(SystemConfiguration.class);
    Site site = context.getBean(SiteCache.class).getSiteById(content.getAssociation().getSiteId());

    // Config prefix
    boolean isMiniAdminMode = (request.getAttribute(AdminRequestParameters.MINI_ADMIN_MODE) != null);
    String confPrefix = "editor.";
    if (isMiniAdminMode) {
        confPrefix += attribute.getMiniFeatureSet();
    } else {
        confPrefix += attribute.getFeatureSet();
    }
    confPrefix += ".";

    String plugins = conf.getString(confPrefix + "plugins");
    if (plugins == null) {
        plugins = conf.getString("editor.default.plugins");
    }

    String buttons = conf.getString(confPrefix + "buttons");
    if (buttons == null) {
        buttons = conf.getString("editor.default.buttons");
        log.error("Button set not defined:" + confPrefix + "buttons");
    }
    String contextMenu = conf.getString(confPrefix + "contextmenu");
    if (contextMenu == null) {
        contextMenu = conf.getString("editor.default.contextmenu");
    }
    String menubar = conf.getString(confPrefix + "menubar");
    if (menubar == null) {
        menubar = conf.getString("editor.default.menubar");
    }


    boolean hasHtmlEditorRole = false;
    String[] htmlEditorRole = Aksess.getHtmlEditorRoles();
    if (htmlEditorRole != null) {
        SecuritySession securitySession = SecuritySession.getInstance(request);
        hasHtmlEditorRole = securitySession.isUserInRole(htmlEditorRole);
    }
    if(hasHtmlEditorRole){
        plugins += " code";
        buttons += " | code";
    }
    String[] buttonRows = new String[]{};
    if (buttons != null) {
        buttonRows = buttons.split("<>");
        for (int i = 0; i < buttonRows.length; i++) {
            buttonRows[i] = "\"" + buttonRows[i] + "\"";
        }
    }

    String valid_elements = conf.getString(confPrefix+"valid_elements");
    if (valid_elements == null) {
        valid_elements = conf.getString("editor.default.valid_elements");
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

    // Setter bredde og høyde på editoren
    int width = attribute.getWidth();
    if (width == -1) width = conf.getInt("editor.default.width", -1);
    request.setAttribute("attributeWidth", width == -1 ? "100%" : width + "px");

    int height = attribute.getHeight();
    if (height == -1) height = conf.getInt("editor.default.height", 450);
    request.setAttribute("attributeHeight", height + "px");

    String headings = conf.getString(confPrefix + "heading");
    if (headings == null) {
        headings = conf.getString("editor.default.heading");
    }
    request.setAttribute("blockFormats", "Paragraph=p;Address=address;Preformated=pre;" + headings);
%>

<script type="text/javascript">
    var miniAdminMode = <%=isMiniAdminMode%>;
</script>

<aksess:getconfig key="editor.custom.javascript"/>

<div class="inputs">
    <textarea name="${fieldName}" id="${fieldName}" class="tinymce_textfield" cols="80" rows="20" style="width: ${attributeWidth}; height: ${attributeHeight}">${value}</textarea><br>
    <script type="text/javascript">
        var options = {
            schema: "html5",
            selector: "textarea#${fieldName}",
            theme: "modern",

            plugins: '<%=plugins%>',
            valid_elements : '<%=valid_elements%>',

            width : "${attributeWidth}",
            height : "${attributeHeight}",

            toolbar: "<%=buttons%>",
            contextmenu: '<%=contextMenu%>',
            menubar: '<%=menubar%>',

            autosave_interval: "5s", //reminder
            autosave_retention: "30m", //stored local incase of crash
            browser_spellcheck: true,
            entity_encoding : "raw",
            templates : "${pageContext.request.contextPath}/aksess/js/editor_templates.jsp",
            content_css : "${pageContext.request.contextPath}${cssPath}",

            block_formats: "${blockFormats}",

            style_formats: [
                {title: 'Bold text', inline: 'b'},
                {title: 'Red text', inline: 'span', styles: {color: '#ff0000'}},
                {title: 'Red header', block: 'h1', styles: {color: '#ff0000'}},
                {title: 'Example 1', inline: 'span', classes: 'example1'},
                {title: 'Example 2', inline: 'span', classes: 'example2'},
                {title: 'Table styles'},
                {title: 'Table row 1', selector: 'tr', classes: 'tablerow1'}
            ]
            <aksess:getconfig key="editor.custom.tinymceparameters"/>
        };

        if('<%=Aksess.getDefaultAdminLocale().getLanguage().toLowerCase()%>' == 'no' ){
            //Sets Norwegian language if default, otherwise keeps tinyMCE (empty) default en_US.
            options['language'] = 'nb_NO';
        }
        tinymce.init(options);
    </script>

</div>
