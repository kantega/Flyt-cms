<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
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
<%@ page import="no.kantega.commons.util.URLHelper,
                 no.kantega.publishing.common.data.Multimedia"%>
<%@ page import="no.kantega.publishing.common.util.MultimediaTagCreator" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>

<%
    Multimedia mm = (Multimedia)request.getAttribute("media");

    String baseUrl = URLHelper.getRootURL(request);
    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);

    Integer maxWidth = (Integer)request.getAttribute("maxWidth");
    if (maxWidth == null || maxWidth == -1) {
        maxWidth = 570;
    }
    if (maxWidth > mm.getWidth() && mm.getWidth() > 0) {
        maxWidth = mm.getWidth();
    }
%>
<!DOCTYPE HTML>

<html>
    <head>
        <title>...</title>

        <script language="Javascript">
            function insertMMObject() {
                var p;
                if (window.opener) {
                    p = window.opener;
                } else {
                    p = window.parent;
                }
                if (p && <%=(!mm.isNew())%>) {
                    var metadata = {};
                    metadata.id = <%=mm.getId()%>;
                    metadata.name = '<%=mm.getName()%>';
                    metadata.url = '<%=mm.getUrl()%>';
                    metadata.mimeType = '<%=mm.getMimeType().getType()%>';
                    metadata.fileExtension = '<%=mm.getMimeType().getFileExtension()%>';

                    var editor = p.tinymce.EditorManager.activeEditor;
                    if (p.openaksess.editcontext.doInsertTag) {
                        // Insert IMG or other tag
                        var str = document.mediaform.tag.value;
                        // IE 7 & 8 looses selection. Must be restored manually.
                        editor.selection.moveToBookmark(editor.windowManager.bookmark);
//                        tinyMCEPopup.editor.selection.moveToBookmark(tinyMCEPopup.editor.windowManager.bookmark);
                        insertHtml(editor, str);
                    } else {
                        insertHtml(editor, "");
                        p.openaksess.editcontext.insertMultimedia(metadata);
                    }
                }

                if (window.opener) {
                    window.close();
                } else {
                    activeTinyPopup = p.tinymce.EditorManager.activeEditor.windowManager.windows[0];
                    if(activeTinyPopup){
                        activeTinyPopup.close();
                    } else {
                        window.setTimeout(p.openaksess.common.modalWindow.close, 300);
                    }
                }
            }

            function insertHtml(editor, html) {
                editor.execCommand("mceInsertRawHTML", false, html);
            }
        </script>
    </head>
    <body onLoad="insertMMObject()">
    <form name="mediaform" style="display:none;">
        <textarea name="tag" rows="2" cols="30"><%=MultimediaTagCreator.mm2HtmlTag(baseUrl, mm, null, maxWidth, -1, null)%></textarea>
    </form>
    </body>
</html>


