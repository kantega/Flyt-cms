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

<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>

<kantega:section id="title">
    <kantega:label key="aksess.popup.selectorgunit"/>
</kantega:section>

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/admin/css/navigate.css"/>">
    <script type="text/javascript" src="<kantega:expireurl url="/admin/js/navigate.js"/>"></script>

    <script type="text/javascript">
        var currentItemIdentifier = -1;

        $(document).ready(function() {
            openaksess.common.debug("$(document).ready(): select-orgunit");
            openaksess.navigate.updateNavigator(currentItemIdentifier, true);
        });

        openaksess.navigate.getNavigatorAction = function() {
            return "${pageContext.request.contextPath}/admin/publish/OrgUnitNavigator.action";
        };

        openaksess.navigate.onNavigatorTitleClick = function(elm) {
            var href = elm.attr("href");
            var title = elm.attr("title");
            var id = "";
            if (href != "") {
                id = openaksess.common.getQueryParam("itemIdentifier", href);
            }

            var w = getParent();
            if (w) {
                w.openaksess.editcontext.insertValueAndNameIntoForm(id, title);
            }

            closeWindow();
        };


        openaksess.navigate.getItemIdentifierFromNavigatorHref = function(href) {
            return openaksess.common.getQueryParam("itemIdentifier", href);
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