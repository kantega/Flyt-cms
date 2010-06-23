<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.publishing.common.util.MultimediaTagCreator"%>
<%@ page import="no.kantega.publishing.common.data.Multimedia" %>
<%@ page import="no.kantega.commons.util.URLHelper" %>

<%
    Multimedia mm = (Multimedia)request.getAttribute("media");
    RequestParameters param = new RequestParameters(request, "utf-8");

    String baseUrl = URLHelper.getRootURL(request);
    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);

    //TODO: 
    int maxWidth = 570;//param.getInt("maxWidth");

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
    <title>...</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/tiny_mce_3_3_6/tiny_mce_popup.js"></script>
</head>
<script language="Javascript">
    function insertMMObject() {
        var p;
        if (window.opener) {
            p = window.opener;
        } else {
            p = window.parent;
        }

        if (p && <%=mm.getId()%> != -1) {
            if (p.openaksess.editcontext.doInsertTag) {
                // Insert IMG or other tag
                var str = document.mediaform.tag.value;
                var editor = p.tinymce.EditorManager.activeEditor;
                // IE 7 & 8 looses selection. Must be restored manually.
                tinyMCEPopup.editor.selection.moveToBookmark(tinyMCEPopup.editor.windowManager.bookmark);
                insertHtml(editor, str);
            } else if (p.openaksess.editcontext.doInsertUrl) {
                // Insert url and name
                p.openaksess.editcontext.insertValueAndNameIntoForm('<%=mm.getUrl()%>', '<%=mm.getName()%>');
            } else {
                // Insert id and name
                p.openaksess.editcontext.insertValueAndNameIntoForm(<%=mm.getId()%>, '<%=mm.getName()%>');
            }

        }
        if (window.opener) {
            window.close();
        } else {
            p.openaksess.common.modalWindow.close();
        }

    }

    function insertHtml(editor, html) {
        editor.execCommand("mceBeginUndoLevel");
        editor.execCommand("mceInsertRawHTML", false, html, {skip_undo : 1});
        editor.execCommand("mceEndUndoLevel");
    }
</script>
<body onLoad="insertMMObject()">
<form name="mediaform" style="display:none;">
    <textarea name="tag" rows="2" cols="30"><%=MultimediaTagCreator.mm2HtmlTag(baseUrl, mm, null, maxWidth, -1, null)%></textarea>
</form>
</body>
</html>


