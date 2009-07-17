<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.commons.configuration.Configuration,
                 no.kantega.commons.util.LocaleLabels,
                 no.kantega.publishing.admin.content.util.HTMLEditorHelper,
                 no.kantega.publishing.common.Aksess,
                 no.kantega.publishing.common.cache.SiteCache"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.common.data.Site"%>
<%@ page import="no.kantega.publishing.common.data.attributes.HtmltextAttribute"%>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService"%>
<%@ page import="no.kantega.publishing.security.SecuritySession"%>
<%@ page import="java.io.*"%>
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
    int height = attribute.getHeight();
    int width = attribute.getWidth();
    String value = attribute.getValue();
    HTMLEditorHelper helper = new HTMLEditorHelper(request);
    value = helper.preEditFilter(value);

    Configuration conf = Aksess.getConfiguration();

    String confPrefix = "editor." + attribute.getFeatureSet() + ".";

    Site site = SiteCache.getSiteById(content.getAssociation().getSiteId());

    boolean hasHtmlEditorRole = false;
    String[] htmlEditorRole = Aksess.getHtmlEditorRoles();
    if (htmlEditorRole != null) {
        ContentManagementService cms = new ContentManagementService(request);
        SecuritySession securitySession = cms.getSecuritySession();
        hasHtmlEditorRole = securitySession.isUserInRole(htmlEditorRole);
    }

    // Let etter /css/site/editor.css og /site/css/editor.css
    String cssPath = "/css" + site.getAlias() + attribute.getCss();
    InputStream is = pageContext.getServletContext().getResourceAsStream(cssPath);
    if (is == null) {
        cssPath = site.getAlias() + "css/" + attribute.getCss();
    }
    is.close();
%>
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<div class="buttonRow">
    <%
        if (conf.getBoolean(confPrefix + "heading", true)) {
    %>
        <SELECT name="heading_<%=fieldName%>" class="selectHeading" onChange="rtSetTextFormat('editor_<%=fieldName%>', this)">
            <OPTION value=""><kantega:label key="aksess.editor.selectheading"/></OPTION>
            <%if (conf.getBoolean(confPrefix + "heading.h1", true)) {%>
                <OPTION value="h1"><kantega:label key="aksess.editor.heading"/> 1</OPTION>
            <%}%>
            <%if (conf.getBoolean(confPrefix + "heading.h2", true)) {%>
                <OPTION value="h2"><kantega:label key="aksess.editor.heading"/> 2</OPTION>
            <%}%>
            <%if (conf.getBoolean(confPrefix + "heading.h3", true)) {%>
                <OPTION value="h3"><kantega:label key="aksess.editor.heading"/> 3</OPTION>
            <%}%>
            <%if (conf.getBoolean(confPrefix + "heading.h4", false)) {%>
                <OPTION value="h4"><kantega:label key="aksess.editor.heading"/> 4</OPTION>
            <%}%>
            <%if (conf.getBoolean(confPrefix + "heading.h5", false)) {%>
                <OPTION value="h5"><kantega:label key="aksess.editor.heading"/> 5</OPTION>
            <%}%>
            <%if (conf.getBoolean(confPrefix + "heading.h6", false)) {%>
                <OPTION value="h6"><kantega:label key="aksess.editor.heading"/> 6</OPTION>
            <%}%>
                <OPTION value="p">Normal</OPTION>
        </SELECT>
    <%
        }
    %>
    <%
        if (conf.getBoolean(confPrefix + "css", true)) {
    %>
    <SELECT name="style_<%=fieldName%>" class="selectCSS" onChange="rtSetStyle('editor_<%=fieldName%>', this)">
        <OPTION value=""><kantega:label key="aksess.editor.selectstyle"/></OPTION>
        <OPTION value="normal">normal</OPTION>
    <%
        String tableStyleHeader = LocaleLabels.getLabel("aksess.editor.tablestyle", Aksess.getDefaultAdminLocale());

        try {
            String cssLine = "";

            InputStream in = pageContext.getServletContext().getResourceAsStream(cssPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while(null != (cssLine = reader.readLine())) {
                String cssClass = cssLine.trim();
                if (cssClass.indexOf(".") == 0) {
                    // Skal vises
                    if (cssClass.indexOf("{") != -1) {
                        cssClass = cssClass.substring(1, cssClass.indexOf("{"));
                        cssClass = cssClass.trim();
                        String title = cssClass;
                        if (cssClass.indexOf("tabell") != -1) {
                            title = tableStyleHeader + "-" + cssClass.substring(".tabell".length()-1, cssClass.length());
                        }
                        if (cssClass.indexOf("table") != -1) {
                            title = tableStyleHeader + "-" + cssClass.substring(".table".length()-1, cssClass.length());
                        }
                        out.write("<OPTION value=\"" + cssClass + "\">" + title + "</OPTION>");
                    }
                }
            }
            in.close();

        } catch (Exception e) {
            // Ignore errors opening CSS files
        }
    %>
    </SELECT>
    <%
        }
    %>
    <%
        if (conf.getBoolean(confPrefix + "cleanuphtml", true)) {
    %>
        <a class="button remove-formatting" href="Javascript:rtCleanupHTML('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.removeformatting"/>" alt="<kantega:label key="aksess.editor.removeformatting"/>"></a>
        <span class="seperator"></span>
    <%
        }
    %>
    <%
        boolean textformat = false;
        if (conf.getBoolean(confPrefix + "bold", true)) {
        textformat = true;
    %>
            <a class="button strong" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'bold')" title="<kantega:label key="aksess.editor.bold"/>" alt="<kantega:label key="aksess.editor.bold"/>"></a>
    <%
        }
        if (conf.getBoolean(confPrefix + "italic", true)) {
            textformat = true;
    %>
            <a class="button italic" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'italic')" title="<kantega:label key="aksess.editor.italic"/>" alt="<kantega:label key="aksess.editor.italic"/>"></a>
    <%
        }
        if (conf.getBoolean(confPrefix + "underline", false)) {
            textformat = true;
    %>
            <a class="button underline" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'underline')" title="<kantega:label key="aksess.editor.underline"/>" alt="<kantega:label key="aksess.editor.underline"/>"></a>
    <%
        }
        if (textformat) {
    %>
            <span class="seperator"></span>
    <%
        }
        if (conf.getBoolean(confPrefix + "justify", true)) {
    %>
            <a class="button justify-left" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'justifyleft')" title="<kantega:label key="aksess.editor.justifyleft"/>"></a>
            <a class="button justify-center" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'justifycenter')" title="<kantega:label key="aksess.editor.justifycenter"/>"></a>
            <a class="button justify-right" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'justifyright')" title="<kantega:label key="aksess.editor.justifyright"/>"></a>
            <span class="seperator"></span>
    <%
        }
        if (conf.getBoolean(confPrefix + "list", true)) {
    %>
            <a class="button unorderedlist" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'insertunorderedlist')" title="<kantega:label key="aksess.editor.unorderedlist"/>"></a>
            <a class="button orderedlist" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'insertorderedlist')" title="<kantega:label key="aksess.editor.orderedlist"/>"></a>
    <%
        }
        if (conf.getBoolean(confPrefix + "indent", true)) {
    %>
            <a class="button indent" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'indent')" title="<kantega:label key="aksess.editor.indent"/>"></a>
            <a class="button outdent" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'outdent')" title="<kantega:label key="aksess.editor.outdent"/>"></a>
    <%
        }
    %>
    <a class="button redo" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'redo')" title="<kantega:label key="aksess.editor.redo"/>"></a>
    <a class="button undo" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'undo')" title="<kantega:label key="aksess.editor.undo"/>"></a>
</div>
<div class="buttonRow">
    <%
        if (conf.getBoolean(confPrefix + "link", true)) {
    %>
            <a class="button link" href="Javascript:rtInsertLink('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.link"/>"></a>
            <a class="button anchor" href="Javascript:rtInsertAnchor('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.anchor"/>"></a>
    <%
        }
        if (conf.getBoolean(confPrefix + "multimedia", true)) {
    %>
            <a class="button media" href="Javascript:rtInsertLink('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.multimedia"/>"></a>
    <%
        }
        if (conf.getBoolean(confPrefix + "link", true) || conf.getBoolean(confPrefix + "multimedia", true)) {
    %>
            <span class="seperator"></span>
    <%
        }
    %>
    <%
        if (conf.getBoolean(confPrefix + "table", true)) {
    %>
            <a class="button inserttable" href="Javascript:rtInsertTable('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.insert"/>"></a>
            <a class="button edittable" href="Javascript:rtEditTable('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.edit"/>"></a>
            <span class="seperator"></span>
            <a class="button splitcell" href="Javascript:rtSplitCell('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.split"/>"></a>
            <a class="button mergecells"  href="Javascript:rtMergeCells('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.merge"/>"></a>
            <a class="button insertcol" href="Javascript:rtInsertColumn('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.insertcol"/>"></a>
            <a class="button insertrow" href="Javascript:rtInsertRow('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.insertrow"/>"></a>
            <a class="button removecol" href="Javascript:rtDeleteColumn('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.removecol"/>"></a>
            <a class="button removerow" href="Javascript:rtDeleteRow('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.insertrow"/>"></a>
            <span class="seperator"></span>
    <%
        }
    %>
    <%
        if (conf.getBoolean(confPrefix + "symbols", true)) {
    %>
            <a class="button insertchar" href="Javascript:rtInsertChar('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.insertchar"/>"></a>
            <a class="button subscript" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'subscript')" title="<kantega:label key="aksess.editor.subscript"/>"></a>
            <a class="button superscript" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'superscript')" title="<kantega:label key="aksess.editor.superscript"/>"></a>
    <%
        }
        if (conf.getBoolean(confPrefix + "replace", true)) {
    %>
            <a class="button replace" href="Javascript:rtReplace('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.replace"/>"></a>
    <%
        }
    %>
</div>
<div class="inputs">
    <IFRAME name="editor_<%=fieldName%>" id="editor_<%=fieldName%>" width="<%=width%>" height="<%=height%>" src="htmlpage.jsp" tabindex="<%=attribute.getTabIndex()%>"></IFRAME>
    <TEXTAREA name="<%=fieldName%>" type="hidden" style="display:none;"><%=value%></TEXTAREA><BR>
    <%
        if (conf.getBoolean(confPrefix + "htmlmode", true) || hasHtmlEditorRole) {
    %>
    <INPUT type="checkbox" onClick="rtToggleHTMLMode('editor_<%=fieldName%>')">HTML
    <%
        }
    %>
</div>