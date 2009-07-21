<%@ page import="no.kantega.publishing.common.Aksess" %>
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
    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/navigate.css">
    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/multimedia.css">
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/jquery-1.3.2.min.js"></script>
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/jquery.dimensions.pack.js"></script>
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/jquery.interface.js"></script>
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/common.jjs"></script>
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/navigator.jjs"></script>
    <script type="text/javascript" src='<%=Aksess.getContextPath()%>/admin/dwr/engine.js'></script>
    <script type="text/javascript">
        var currentItemIdentifier = -1;

        $(document).ready(function() {
            debug("$(document).ready(): select-mediafolder");
            updateNavigator(currentItemIdentifier, true);
        });


        function setContextMenus() {
        }

        function getNavigatorAction() {
            return "<%=Aksess.getContextPath()%>/admin/multimedia/MultimediaNavigator.action";
        }

        function getItemIdentifierFromNavigatorHref(href) {
            return getQueryParam("itemIdentifier", href);
        }

        function getCurrentItemIdentifier() {
            return currentItemIdentifier;
        }

        function onNavigatorTitleClick(elm) {
            var href = elm.attr("href");
            currentItemIdentifier = getItemIdentifierFromNavigatorHref(href);

            var title = elm.attr("title");
            var w = window.opener;
            if (w) {
                if (w.doInsertTag) {
                    // Insert as tag
                    w.insertValueIntoForm("/multimedia.ap?id=" + currentItemIdentifier);
                } else {
                    // Insert as id and value
                    w.insertIdAndValueIntoForm(currentItemIdentifier, title);
                }
            }
            closeWindow();
        }

        function getNavigatorParams() {
            var params = new Object();
            params.getFoldersOnly = true;
            return params;
        }
    </script>

</kantega:section>

<kantega:section id="body">
    <div id="SelectMedia">

        <div id="Navigation">
            <div id="Navigator"></div>
        </div>
        <div class="buttonGroup">
            <input type="button" class="button cancel" value="<kantega:label key="aksess.button.cancel"/>">
        </div>
    </div>

</kantega:section>


<%@include file="../../layout/popupLayout.jsp"%>