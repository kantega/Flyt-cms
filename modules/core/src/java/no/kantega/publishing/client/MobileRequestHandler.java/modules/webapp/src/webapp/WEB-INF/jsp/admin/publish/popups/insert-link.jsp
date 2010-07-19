<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
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
<kantega:section id="title">
    <kantega:label key="aksess.insertlink.title"/>
</kantega:section>

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/jquery.autocomplete.css">
    <script language="Javascript" type="text/javascript" src="${pageContext.request.contextPath}/admin/js/editcontext.jjs"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.autocomplete.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/tiny_mce_3_3_6/tiny_mce_popup.js"></script>
</kantega:section>

<kantega:section id="body">
    <script type="text/javascript">
        function buttonOkPressed() {

            var attribs = getUrlAttributes();
            var editor = getParent().tinymce.EditorManager.activeEditor;

            // IE 7 & 8 looses selection. Must be restored manually.
            tinyMCEPopup.editor.selection.moveToBookmark(tinyMCEPopup.editor.windowManager.bookmark);

            editor.execCommand("mceBeginUndoLevel");
            var elements = getSelectedElements(editor);
            for (var i = 0, n = elements.length; i < n; i++) {
                setAttributes(editor, elements[i], attribs);
            }
            editor.execCommand("mceEndUndoLevel");
            getParent().openaksess.common.modalWindow.close();
        }

        function getSelectedElements(editor) {
            var elements = [];
            var element = editor.selection.getNode();
            element = editor.dom.getParent(element, "A");
            if (element == null) {
                editor.getDoc().execCommand("unlink", false, null);
                editor.execCommand("CreateLink", false, "#insertlink_temp_url#", {skip_undo : 1});
                elements = getParent().tinymce.grep(
                        editor.dom.select("a"),
                        function(n) {
                            return editor.dom.getAttrib(n, 'href') == '#insertlink_temp_url#';
                        });
            } else {
                elements.push(element);
            }
            return elements;
        }

        function setAttributes(editor, element, attributes) {
            for (var key in attributes) {
                editor.dom.setAttrib(element, key, attributes[key]);
            }
        }
    </script>
    <div id="SelectLinkType" class="ui-tabs ui-widget ui-widget-content ui-corner-all">
        <ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
            <li class="<c:if test="${linkType == 'external'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top"><a href="?linkType=external"><kantega:label key="aksess.insertlink.external"/></a></li>
            <li class="<c:if test="${linkType == 'internal'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top"><a href="?linkType=internal""><kantega:label key="aksess.insertlink.internal"/></a></li>
            <li class="<c:if test="${linkType == 'anchor'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top"><a href="?linkType=anchor""><kantega:label key="aksess.insertlink.anchor"/></a></li>
            <li class="<c:if test="${linkType == 'attachment'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top"><a href="?linkType=attachment""><kantega:label key="aksess.insertlink.attachment"/></a></li>
            <li class="<c:if test="${linkType == 'email'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top"><a href="?linkType=email"><kantega:label key="aksess.insertlink.email"/></a></li>
            <li class="<c:if test="${linkType == 'multimedia'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top"><a href="?linkType=multimedia""><kantega:label key="aksess.insertlink.multimedia"/></a></li>
        </ul>
        <div id="InsertLinkForm" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
            <form action="" name="linkform">

                <jsp:include page="insert-link/${linkType}.jsp"/>
                <div class="buttonGroup">
                    <span class="button"><input type="button" class="insert" value="<kantega:label key="aksess.button.insert"/>"></span>
                    <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
                </div>
            </form>
        </div>

    </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>
