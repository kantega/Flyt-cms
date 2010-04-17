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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/navigate.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/multimedia.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/navigator.jjs"></script>
    <script type="text/javascript">
        var currentItemIdentifier = -1;

        $(document).ready(function() {
            openaksess.common.debug("$(document).ready(): select-mediafolder");
            openaksess.navigate.updateNavigator(currentItemIdentifier, true);
        });


        openaksess.navigate.getNavigatorAction = function() {
            return "${pageContext.request.contextPath}/admin/multimedia/MultimediaNavigator.action";
        };

        openaksess.navigate.getItemIdentifierFromNavigatorHref = function(href) {
            return openaksess.common.getQueryParam("itemIdentifier", href);
        };

        openaksess.navigate.onNavigatorTitleClick = function(elm) {
            var href = elm.attr("href");
            currentItemIdentifier = openaksess.navigate.getItemIdentifierFromNavigatorHref(href);

            var title = elm.attr("title");
            var p = getParent();
            if (p) {
                if (p.openaksess.editcontext.doInsertTag) {
                    // Insert as tag
                    p.openaksess.editcontext.insertValueIntoForm("/multimedia.ap?id=" + currentItemIdentifier);
                } else {
                    // Insert as id and value
                    p.openaksess.editcontext.insertIdAndValueIntoForm(currentItemIdentifier, title);
                }
            }
            closeWindow();
        };

        openaksess.navigate.getNavigatorParams = function() {
            var params = new Object();
            params.getFoldersOnly = true;
            return params;
        };
    </script>

</kantega:section>

<kantega:section id="body">
    <div id="SelectMedia">

        <div id="Navigation">
            <div id="Navigator"></div>
        </div>
        <div class="buttonGroup">
            <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
        </div>
    </div>

</kantega:section>


<%@include file="../../layout/popupLayout.jsp"%>