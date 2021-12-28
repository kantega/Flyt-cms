<%@ page import="no.kantega.publishing.admin.AdminRequestParameters" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess"%>
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
~ limitations under the License.
--%>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title><kantega:hassection id="title"><kantega:getsection id="title"/> - </kantega:hassection><kantega:label key="aksess.title"/></title>
    <%-- Prevent IE from blocking: http://www.phpied.com/conditional-comments-block-downloads/ --%>
    <!--[if lt IE 8]><![endif]-->
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/admin/favicon.ico" type="image/x-icon">

    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-commonlayout.css"/>">
    <!--[if lt IE 8]>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/default_ie7.css">
    <![endif]-->

    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { };
        }
        properties.title = '<kantega:label key="aksess.title" escapeJavascript="true"/>';
        properties.contextPath = '${pageContext.request.contextPath}';
        properties.loadingText = '<kantega:label key="aksess.ajax.loading" escapeJavascript="true"/>';
        properties.debug = <aksess:getconfig key="javascript.debug" default="false"/>;
        properties.contentRequestHandler = '<%=Aksess.CONTENT_REQUEST_HANDLER%>';
        properties.thisId = '<%=AdminRequestParameters.THIS_ID %>';
    </script>
    <script type="text/javascript" src='<kantega:expireurl url="/wro-oa/admin-commonlayout.js"/>'></script>
    <script type="text/javascript" src='<kantega:expireurl url="/admin/js/jquery-ui-i18n.min.js"/>'></script>

    <kantega:getsection id="head"/>
    <script type="text/javascript">
        $(document).ready(function(){
            $("#MainPane table.dataTable").dataTable({
                "sPaginationType": "two_button",
                "iDisplayLength":25,
                "bLengthChange":false,
                "bPaginate": false,
                "bInfo": false,
                "oLanguage": {
                    "sLengthMenu": "<kantega:label key="aksess.datatable.length"/>",
                    "sSearch": "<kantega:label key="aksess.datatable.search"/>"
                }
            });
            $.datepicker.setDefaults($.datepicker.regional['']);
            $.datepicker.setDefaults($.datepicker.regional['${aksess_locale.language}']);
            $.datepicker.setDefaults( {firstDay: 1, showOn: 'button', buttonImage: '${pageContext.request.contextPath}/admin/bitmaps/common/icons/small/calendar.png', buttonImageOnly: true, dateFormat:'dd.mm.yy'});
        });

    </script>
</head>
<body<kantega:hassection id="bodyclass"> class="<kantega:getsection id="bodyclass"/>"</kantega:hassection>>

<div id="Top">
    <kantega:hassection id="topMenu">
        <script type="text/javascript">
            // Hides top menu when opened in a popup
            if(openaksess.common.isPopup()) {
                document.write('<div style="display:none;">');
            }
        </script>
        <div id="TopMenu">
            <kantega:getsection id="topMenu"/>
        </div>
        <script type="text/javascript">
            if (openaksess.common.isPopup()) {
                document.write('<\/div>');
            }
        </script>

    </kantega:hassection>

    <kantega:hassection id="modesMenu">
        <div id="ModesMenu">
            <kantega:getsection id="modesMenu"/>
        </div>
    </kantega:hassection>

    <kantega:hassection id="toolsMenu">
        <div id="ToolsMenu">
            <kantega:getsection id="toolsMenu"/>
        </div>
    </kantega:hassection>
    <kantega:hassection id="tabToolsMenu">
        <div id="TabToolsMenu">
            <kantega:getsection id="tabToolsMenu"/>
        </div>
    </kantega:hassection>
</div>

<kantega:getsection id="body"/>


</body>
</html>
