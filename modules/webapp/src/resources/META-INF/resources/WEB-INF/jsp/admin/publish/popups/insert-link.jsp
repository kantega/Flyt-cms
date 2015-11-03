<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
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
    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { content : {} };
        }
        properties.contextPath = '${pageContext.request.contextPath}';
    </script>
    <%--<script type="text/javascript" src="<kantega:expireurl url="/aksess/js/aksess-i18n.jjs"/>"></script>--%>
    <script type="text/javascript" src="<kantega:expireurl url="/admin/js/editcontext.js"/>"></script>
    <%--<script type="text/javascript" src="<kantega:expireurl url="/aksess/tiny_mce/tiny_mce_popup.js"/>"></script>--%>
</kantega:section>

<kantega:section id="body">
    <script type="text/javascript">
        function buttonOkPressed() {
            var attribs = getUrlAttributes();
            if (attribs != null) {
                openaksess.editcontext.insertLink(attribs);
                var tinymce = getParent().tinymce;
                var ed = tinymce.editors[0];
                ed.windowManager.windows[0].close();
            }
        }
    </script>
    <div id="SelectLinkType" class="ui-tabs ui-widget ui-widget-content ui-corner-all">
        <c:if test="${miniAdminMode}">
            <c:set var="extraparams" value="&isMiniAdminMode=true"/>
        </c:if>

        <ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
            <li class="<c:if test="${linkType == 'external'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top">
                <a href="?linkType=external${extraparams}"><kantega:label key="aksess.insertlink.external"/></a>
            </li>
            <c:if test="${allowInternalLinks}">
                <li class="<c:if test="${linkType == 'internal'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top">
                    <a href="?linkType=internal${extraparams}"><kantega:label key="aksess.insertlink.internal"/></a>
                </li>
            </c:if>
            <li class="<c:if test="${linkType == 'anchor'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top">
                <a href="?linkType=anchor${extraparams}"><kantega:label key="aksess.insertlink.anchor"/></a>
            </li>
            <c:if test="${allowAttachments}">
                <li class="<c:if test="${linkType == 'attachment'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top">
                    <a href="?linkType=attachment${extraparams}"><kantega:label key="aksess.insertlink.attachment"/></a>
                </li>
            </c:if>
            <li class="<c:if test="${linkType == 'email'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top">
                <a href="?linkType=email${extraparams}"><kantega:label key="aksess.insertlink.email"/></a>
            </li>
            <c:if test="${allowMediaArchive}">
                <li class="<c:if test="${linkType == 'multimedia'}">ui-tabs-selected ui-state-active </c:if>ui-state-default ui-corner-top">
                    <a href="?linkType=multimedia${extraparams}"><kantega:label key="aksess.insertlink.multimedia"/></a>
                </li>
            </c:if>
        </ul>
        <div id="InsertLinkForm" class="ui-tabs-panel ui-widget-content ui-corner-bottom">
            <form action="" name="linkform">
                <c:if test="${(linkType == 'internal' && !allowInternalLinks) || (linkType == 'attachment' && !allowAttachments) || (linkType == 'multimedia' && !allowInternalLinks)}">
                    <c:set var="linkType" value="external"/>
                </c:if>
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
