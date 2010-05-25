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

    if (pageContext.getServletContext().getResource(cssPath) == null) {
        cssPath = site.getAlias() + "css/" + attribute.getCss();
    }
%>
<tr>
    <td class="inpHeading"><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
</tr>
<tr>
    <td class="inpHeading">
    <%
        Boolean miniAksessWysiwyg = (Boolean)request.getAttribute("miniAksessWysiwyg");
        if (miniAksessWysiwyg == null || miniAksessWysiwyg) {
    %>
        <table cellpadding="0" cellspacing="0" border="0">
            <tr valign="top">
                <td width="165">
                    <%
                        if (conf.getBoolean(confPrefix + "heading", true)) {
                    %>
                    <SELECT name="heading_<%=fieldName%>" class="inp" style="width:155px;" onChange="rtSetTextFormat('editor_<%=fieldName%>', this)">
                        <OPTION value=""><kantega:label key="aksess.editor.selectheading"/></OPTION>
                        <%
                            if (conf.getBoolean(confPrefix + "heading.h1", true)) {
                        %>
                        <OPTION value="h1"><kantega:label key="aksess.editor.heading"/> 1</OPTION>
                        <%
                            }
                        %>
                        <%
                            if (conf.getBoolean(confPrefix + "heading.h2", true)) {
                        %>
                        <OPTION value="h2"><kantega:label key="aksess.editor.heading"/> 2</OPTION>
                        <%
                            }
                        %>
                        <%
                            if (conf.getBoolean(confPrefix + "heading.h3", true)) {
                        %>
                        <OPTION value="h3"><kantega:label key="aksess.editor.heading"/> 3</OPTION>
                        <%
                            }
                        %>
                        <%
                            if (conf.getBoolean(confPrefix + "heading.h4", false)) {
                        %>
                        <OPTION value="h4"><kantega:label key="aksess.editor.heading"/> 4</OPTION>
                        <%
                            }
                        %>
                        <%
                            if (conf.getBoolean(confPrefix + "heading.h5", false)) {
                        %>
                        <OPTION value="h5"><kantega:label key="aksess.editor.heading"/> 5</OPTION>
                        <%
                            }
                        %>
                        <%
                            if (conf.getBoolean(confPrefix + "heading.h6", false)) {
                        %>
                        <OPTION value="h6"><kantega:label key="aksess.editor.heading"/> 6</OPTION>
                        <%
                            }
                        %>
                        <OPTION value="p">Normal</OPTION>
                    </SELECT>
                    <%
                        }
                    %>
                </td>
                <td width="415">
                    <%
                        boolean textformat = false;
                        if (conf.getBoolean(confPrefix + "bold", true)) {
                            textformat = true;
                    %>
                        <A href="Javascript:rtFormatText('editor_<%=fieldName%>', 'bold')" title="<kantega:label key="aksess.editor.bold"/>" alt="<kantega:label key="aksess.editor.bold"/>"><IMG src="../bitmaps/editor/editor_bold.gif" width="18" height="20" border="0"></A>
                    <%
                        }
                        if (conf.getBoolean(confPrefix + "italic", true)) {
                            textformat = true;
                    %>
                        <A href="Javascript:rtFormatText('editor_<%=fieldName%>', 'italic')" title="<kantega:label key="aksess.editor.italic"/>" alt="<kantega:label key="aksess.editor.italic"/>"><IMG src="../bitmaps/editor/editor_italic.gif" width="18" height="20" border="0"></A>
                    <%
                        }
                        if (conf.getBoolean(confPrefix + "underline", false)) {
                            textformat = true;
                    %>
                        <A href="Javascript:rtFormatText('editor_<%=fieldName%>', 'underline')" title="<kantega:label key="aksess.editor.underline"/>" alt="<kantega:label key="aksess.editor.underline"/>"><IMG src="../bitmaps/editor/editor_underline.gif" width="18" height="20" border="0"></A>
                    <%
                        }
                        if (textformat) {
                    %>
                        <IMG src="../bitmaps/editor/editor_seperator.gif" width="2" height="20" border="0">
                    <%
                        }
                        if (conf.getBoolean(confPrefix + "symbols", true)) {
                    %>
                    <MAP name="editor_<%=fieldName%>_symbols_map">
                        <area shape="rect" coords="38,1,54,19" href="Javascript:rtInsertChar('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.insertchar"/>" alt="<kantega:label key="aksess.editor.insertchar"/>" >
                        <area shape="rect" coords="20,1,36,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'subscript')" title="<kantega:label key="aksess.editor.subscript"/>" alt="<kantega:label key="aksess.editor.subscript"/>" >
                        <area shape="rect" coords="1,1,17,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'superscript')" title="<kantega:label key="aksess.editor.superscript"/>" alt="<kantega:label key="aksess.editor.superscript"/>" >
                    </MAP>
                    <IMG src="../bitmaps/editor/editor_symbols.gif" width="58" height="20" border="0" usemap="#editor_<%=fieldName%>_symbols_map">
                    <%
                        }
                        if (conf.getBoolean(confPrefix + "justify", true)) {
                    %>
                    <MAP name="editor_<%=fieldName%>_justify_map">
                        <area shape="rect" coords="47,1,67,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'justifyright')" title="<kantega:label key="aksess.editor.justifyright"/>" alt="<kantega:label key="aksess.editor.justifyright"/>" >
                        <area shape="rect" coords="24,1,44,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'justifycenter')" title="<kantega:label key="aksess.editor.justifycenter"/>" alt="<kantega:label key="aksess.editor.justifycenter"/>" >
                        <area shape="rect" coords="1,1,19,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'justifyleft')" title="<kantega:label key="aksess.editor.justifyleft"/>" alt="<kantega:label key="aksess.editor.justifyleft"/>" >
                    </MAP>
                    <IMG src="../bitmaps/editor/editor_justify.gif" width="74" height="20" border="0" usemap="#editor_<%=fieldName%>_justify_map">
                    <%
                        }
                        if (conf.getBoolean(confPrefix + "list", true)) {
                    %>
                    <MAP name="editor_<%=fieldName%>_list_map">
                        <area shape="rect" coords="26,3,44,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'insertunorderedlist')" title="<kantega:label key="aksess.editor.unorderedlist"/>" alt="<kantega:label key="aksess.editor.unorderedlist"/>" >
                        <area shape="rect" coords="3,3,21,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'insertorderedlist')" title="<kantega:label key="aksess.editor.orderedlist"/>" alt="<kantega:label key="aksess.editor.orderedlist"/>" >
                    </MAP>
                    <IMG src="../bitmaps/editor/editor_list.gif" width="48" height="20" border="0" usemap="#editor_<%=fieldName%>_list_map">
                    <%
                        }
                        if (conf.getBoolean(confPrefix + "indent", true)) {
                    %>
                    <MAP name="editor_<%=fieldName%>_indent_map">
                        <area shape="rect" coords="26,3,44,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'indent')" title="<kantega:label key="aksess.editor.indent"/>" alt="<kantega:label key="aksess.editor.indent"/>" >
                        <area shape="rect" coords="3,3,21,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'outdent')" title="<kantega:label key="aksess.editor.outdent"/>" alt="<kantega:label key="aksess.editor.outdent"/>" >
                    </MAP>
                    <IMG src="../bitmaps/editor/editor_indent.gif" width="46" height="20" border="0" usemap="#editor_<%=fieldName%>_indent_map">
                    <%

                        }
                        if (conf.getBoolean(confPrefix + "list", true) || conf.getBoolean(confPrefix + "indent", true)) {
                    %>
                    <IMG src="../bitmaps/editor/editor_seperator.gif" width="2" height="20" border="0">
                    <%
                        }
                        if (conf.getBoolean(confPrefix + "spellchecker", false)) {
                    %>
                        <A href="Javascript:rtSpellCheck('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.spellcheck"/>" alt="<kantega:label key="aksess.editor.spellcheck"/>"><IMG src="../bitmaps/editor/editor_spellcheck.gif" width="24" height="20" border="0"></A>
                        <IMG src="../bitmaps/editor/editor_seperator.gif" width="2" height="20" border="0">
                    <%
                        }
                        if (conf.getBoolean(confPrefix + "link", true)) {
                    %>
                    <A href="Javascript:rtInsertLink('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.link"/>" alt="<kantega:label key="aksess.editor.link"/>"><IMG src="../bitmaps/editor/editor_link.gif" width="25" height="20" border="0"></A>
                    <A href="Javascript:rtInsertAnchor('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.anchor"/>" alt="<kantega:label key="aksess.editor.anchor"/>"><IMG src="../bitmaps/editor/editor_anchor.gif" width="24" height="20" border="0"></A>
                    <%
                        }
                        if (conf.getBoolean(confPrefix + "multimedia", true)) {
                    %>
                    <A href="Javascript:rtInsertMedia('editor_<%=fieldName%>')"  title="<kantega:label key="aksess.editor.multimedia"/>" alt="<kantega:label key="aksess.editor.multimedia"/>"><IMG src="../bitmaps/editor/editor_multimedia.gif" width="24" height="20" border="0"></A>
                    <%
                        }
                    %>

                </td>
            </tr>
            <tr>
                <td>
                    <%
                        if (conf.getBoolean(confPrefix + "css", true)) {
                    %>
                    <SELECT name="style_<%=fieldName%>" class="inp" style="width:155px;" onChange="rtSetStyle('editor_<%=fieldName%>', this)">
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

                        } catch (FileNotFoundException e) {
                            //Filen finnes ikke - ignorerer
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    %>
                    </SELECT>
                    <%
                        }
                    %>
                </td>
                <td>
                    <%
                        if (conf.getBoolean(confPrefix + "cleanuphtml", true)) {
                    %>
                    <A href="Javascript:rtCleanupHTML('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.removeformatting"/>" alt="<kantega:label key="aksess.editor.removeformatting"/>"><IMG src="../bitmaps/editor/editor_removeformat.gif" width="24" height="20" border="0"></A>
                    <IMG src="../bitmaps/editor/editor_seperator.gif" width="2" height="20" border="0">
                    <%
                        }
                    %>
                    <MAP name="editor_<%=fieldName%>_undo_map">
                        <area shape="rect" coords="24,1,46,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'redo')" title="<kantega:label key="aksess.editor.redo"/>" alt="<kantega:label key="aksess.editor.redo"/>" >
                        <area shape="rect" coords="2,1,19,19" href="Javascript:rtFormatText('editor_<%=fieldName%>', 'undo')" title="<kantega:label key="aksess.editor.undo"/>" alt="<kantega:label key="aksess.editor.undo"/>" >
                    </MAP>
                    <IMG src="../bitmaps/editor/editor_undo.gif" width="48" height="20" border="0" usemap="#editor_<%=fieldName%>_undo_map">
                    <%
                        if (conf.getBoolean(confPrefix + "table", true)) {
                    %>
                    <IMG src="../bitmaps/editor/editor_seperator.gif" width="2" height="20" border="0">
                    <MAP name="editor_<%=fieldName%>_table_map">
                        <area shape="rect" coords="26,1,45,20" href="Javascript:rtEditTable('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.edit"/>" alt="<kantega:label key="aksess.editor.table.edit"/>" >
                        <area shape="rect" coords="3,1,22,20" href="Javascript:rtInsertTable('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.insert"/>" alt="<kantega:label key="aksess.editor.table.insert"/>" >
                    </MAP>
                    <IMG src="../bitmaps/editor/editor_table.gif" width="48" height="20" border="0" usemap="#editor_<%=fieldName%>_table_map">
                    <IMG src="../bitmaps/editor/editor_seperator.gif" width="2" height="20" border="0">
                    <MAP name="editor_<%=fieldName%>_tableedit_map">
                        <area shape="rect" coords="126,1,145,20" href="Javascript:rtSplitCell('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.split"/>" alt="<kantega:label key="aksess.editor.table.split"/>" >
                        <area shape="rect" coords="103,1,122,20" href="Javascript:rtMergeCells('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.merge"/>" alt="<kantega:label key="aksess.editor.table.merge"/>" >
                        <area shape="rect" coords="71,1,90,20" href="Javascript:rtDeleteColumn('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.removecol"/>" alt="<kantega:label key="aksess.editor.table.removecol"/>" >
                        <area shape="rect" coords="48,1,67,20" href="Javascript:rtDeleteRow('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.removerow"/>" alt="<kantega:label key="aksess.editor.table.removerow"/>" >
                        <area shape="rect" coords="26,1,45,20" href="Javascript:rtInsertColumn('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.insertcol"/>" alt="<kantega:label key="aksess.editor.table.insertcol"/>" >
                        <area shape="rect" coords="3,1,22,20" href="Javascript:rtInsertRow('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.table.insertrow"/>" alt="<kantega:label key="aksess.editor.table.insertrow"/>" >
                    </MAP>
                    <IMG src="../bitmaps/editor/editor_tableedit.gif" width="148" height="24" border="0" usemap="#editor_<%=fieldName%>_tableedit_map">
                    <%
                        }
                        if (conf.getBoolean(confPrefix + "replace", true)) {
                    %>
                    <IMG src="../bitmaps/editor/editor_seperator.gif" width="2" height="20" border="0">
                    <A href="Javascript:rtReplace('editor_<%=fieldName%>')" title="<kantega:label key="aksess.editor.replace"/>" alt="<kantega:label key="aksess.editor.replace"/>"><IMG src="../bitmaps/editor/editor_replace.gif" width="24" height="20" border="0"></A>
                    <%
                        }
                    %>
                </td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
    <IFRAME name="editor_<%=fieldName%>" id="editor_<%=fieldName%>" width="<%=width%>" height="<%=height%>" src="htmlpage.jsp" tabindex="<%=attribute.getTabIndex()%>"></IFRAME>
    <TEXTAREA name="<%=fieldName%>" type="hidden" style="display:none;"><%=value%></TEXTAREA><BR>
    <%
        if (conf.getBoolean(confPrefix + "htmlmode", true) || hasHtmlEditorRole) {
    %>
    <INPUT type="checkbox" onClick="rtToggleHTMLMode('editor_<%=fieldName%>')">HTML
    <%
        }
    %>
    <%
        } else {
    %>
        <textarea rows="10" cols=72 class="inp htmlText" style="width:600px;" wrap="soft" onFocus="setFocusField(this)" onBlur="blurField()" name="<%=fieldName%>" tabindex="<%=attribute.getTabIndex()%>"><%=value%></textarea>
    <%
        }
    %>
    </td>
</tr>