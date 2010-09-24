<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.admin.AdminRequestParameters" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html id="Popup">
<head>
    <title><kantega:getsection id="title"/></title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-popuplayout.css"/>">
    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { };
        }
        properties.contextPath = '${pageContext.request.contextPath}';
        properties.debug = <aksess:getconfig key="javascript.debug" default="false"/>;
        properties.contentRequestHandler = '<%=Aksess.CONTENT_REQUEST_HANDLER%>';
        properties.thisId = '<%=AdminRequestParameters.THIS_ID %>';
    </script>
    <script type="text/javascript" src='<kantega:expireurl url="/wro-oa/admin-popuplayout.js"/>'></script>
    <script type="text/javascript" src='${pageContext.request.contextPath}/admin/js/jquery-ui-i18n.min.js'></script>
    <script type="text/javascript" src='${pageContext.request.contextPath}/admin/dwr/engine.js'></script>
    <script type="text/javascript">
        $.datepicker.setDefaults( {firstDay: 1, showOn: 'button', buttonImage: '${pageContext.request.contextPath}/admin/bitmaps/common/icons/small/calendar.png', buttonImageOnly: true, dateFormat:'dd.mm.yy'});
        $.datepicker.setDefaults($.datepicker.regional['']);
        $.datepicker.setDefaults($.datepicker.regional['${aksess_locale.language}']);
    </script>

    <script type="text/javascript">
        $(document).ready(function() {
            $("#Content .button .ok, #Content .button .insert").click(function(){
                var close = true;
                if (typeof buttonOkPressed == 'function') {
                    close = buttonOkPressed();                    
                }
                if (close) {
                    closeWindow();
                }
            });
            $("#Content .button .cancel").click(function(){
                openaksess.common.debug("popupLayout: close clicked");
                closeWindow();
            });
        });

        function closeWindow() {
            if (window.opener) {
                window.close();
            } else {
                window.parent.openaksess.common.modalWindow.close();
            }
        }

        function getParent() {
            if (window.opener) {
                return window.opener;
            } else {
                return window.parent;
            }
        }

    </script>
    <kantega:getsection id="head"/>
</head>

<body class="popup">
    <div id="Content" class="popup">
        <kantega:getsection id="body"/>
    </div>
</body>
</html>
