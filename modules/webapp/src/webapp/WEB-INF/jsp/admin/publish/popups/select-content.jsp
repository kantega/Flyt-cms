<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~  http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License
  --%>

<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>

<kantega:section id="title">
    <kantega:label key="aksess.popup.selectcontent"/>
</kantega:section>

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/navigate.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/navigate.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/dwr/engine.js"></script>
    <script type="text/javascript">
        var currentItemIdentifier = -1;

        $(document).ready(function() {
            openaksess.common.debug("$(document).ready(): select-content");
            openaksess.navigate.updateNavigator(currentItemIdentifier, true);
        });

        openaksess.navigate.getNavigatorAction = function() {
            return "${pageContext.request.contextPath}/admin/publish/ContentNavigator.action?startId=${startId}";
        };

        openaksess.navigate.onNavigatorTitleClick = function(elm) {
            var href = elm.attr("href");
            var title = elm.attr("title");
            var id;
            var url;
            <c:choose>
                <c:when test="${selectContentId}">
                id = openaksess.common.getQueryParam("contentId", href);
                url = "/content.ap?contentId=" + id + "&amp;contextId=$contextId$";
                </c:when>
                <c:otherwise>
                id = openaksess.common.getQueryParam("thisId", href);
                url = "/content.ap?thisId=" + id;
                </c:otherwise>
            </c:choose>

            var w = getParent();
            if (w) {
                if (w.openaksess.editcontext.doInsertTag) {
                    w.openaksess.editcontext.insertValueIntoForm(url);
                } else {
                    w.openaksess.editcontext.insertValueAndNameIntoForm(id, title);
                }
            }

            closeWindow();
        };


        openaksess.navigate.getItemIdentifierFromNavigatorHref = function(href) {
            return openaksess.common.getQueryParam("thisId", href);
        };

        openaksess.navigate.getCurrentItemIdentifier = function() {
            return currentItemIdentifier;
        };


    </script>
</kantega:section>

<kantega:section id="body">
    <div id="SelectPage">

        <div id="Navigation">
            <div id="Navigator"></div>
        </div>
        <div class="buttonGroup">
            <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
        </div>
    </div>
</kantega:section>


<%@include file="../../layout/popupLayout.jsp"%>