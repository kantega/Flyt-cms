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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><kantega:hassection id="title"><kantega:getsection id="title"/> - </kantega:hassection><kantega:label key="aksess.title"/></title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/admin/favicon.ico" type="image/x-icon">

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/reset.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/default.css">
    <!--[if lt IE 8]>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/default_ie7.css">
    <![endif]-->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/jquery-ui-1.8.1.custom.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/jquery-ui-additions.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/jquery.autocomplete.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.dimensions.pack.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery-ui-1.8.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery-ui-i18n.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.contextMenu.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.ba-bbq-1.0.3.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.autocomplete.min.js"></script>
    <script type="text/javascript" src='${pageContext.request.contextPath}/admin/dwr/interface/ContentStateHandler.js'></script>
    <script type="text/javascript" src='${pageContext.request.contextPath}/admin/dwr/interface/UserPreferencesHandler.js'></script>
    <script type="text/javascript" src='${pageContext.request.contextPath}/admin/dwr/interface/ContentClipboardHandler.js'></script>
    <script type="text/javascript" src='${pageContext.request.contextPath}/admin/dwr/engine.js'></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/common.jjs"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/admin.jjs"></script>
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
            $.datepicker.setDefaults($.datepicker.regional['${aksess_locale.language}']);
            $.datepicker.setDefaults( {firstDay: 1, showOn: 'button', buttonImage: '${pageContext.request.contextPath}/admin/bitmaps/common/icons/small/calendar.png', buttonImageOnly: true, dateFormat:'dd.mm.yy'});
        });

    </script>
</head>
<body>

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
