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
    <kantega:label key="aksess.multimedia.title"/>
</kantega:section>

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/navigate.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery-1.3.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.dimensions.pack.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.interface.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/common.jjs"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/navigator.jjs"></script>
    <script type="text/javascript" src='${pageContext.request.contextPath}/admin/dwr/engine.js'></script>
    <script type="text/javascript">
        var currentItemIdentifier = -1;

        $(document).ready(function() {
            debug("$(document).ready(): select-content");
            updateNavigator(currentItemIdentifier, true);
        });

        function setContextMenus() {
        }

        function getNavigatorAction() {
            return "${pageContext.request.contextPath}/admin/publish/ContentNavigator.action";
        }

        function onNavigatorTitleClick(elm) {
            var href = elm.attr("href");
            var title = elm.attr("title");
            var id;
            var url;
            <c:choose>
                <c:when test="${selectContentId}">
                id = getQueryParam("contentId", href);
                url = "/content.ap?contentId=" + id + "&amp;contextId=$contextId";
                </c:when>
                <c:otherwise>
                id = getQueryParam("thisId", href);
                url = "/content.ap?thisId=" + id;
                </c:otherwise>
            </c:choose>

            var w = window.opener;
            if (w) {
                if (w.doInsertTag) {
                    w.insertValueIntoForm(url);
                } else {
                    w.insertIdAndValueIntoForm(id, title);
                }
            }

            closeWindow();
        }

        function getNavigatorParams() {
            return new Object();
        }

        function getItemIdentifierFromNavigatorHref(href) {
            return getQueryParam("thisId", href);
        }

    </script>
</kantega:section>

<kantega:section id="body">
    <div id="SelectPage">

        <div id="Navigation">
            <div id="Navigator"></div>
        </div>
        <div class="buttonGroup">
            <span class="button"><input type="submit" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
        </div>
    </div>
</kantega:section>


<%@include file="../../layout/popupLayout.jsp"%>