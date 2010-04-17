<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
<html>
<head>
    <title><kantega:getsection id="title"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/reset.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/base.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/default.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/jquery-ui-1.7.2.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/jquery-ui-additions.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/jquery.autocomplete.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.dimensions.pack.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery-ui-1.8rc3.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.autocomplete.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.interface.js"></script>
    <script type="text/javascript" src='${pageContext.request.contextPath}/admin/dwr/engine.js'></script>    
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/common.jjs"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.roundcorners.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            $("div.fieldset").roundCorners();
        });
        $.datepicker.setDefaults( {firstDay: 1, showOn: 'button', buttonImage: '${pageContext.request.contextPath}/admin/bitmaps/common/icons/small/calendar.png', buttonImageOnly: true, dateFormat:'dd.mm.yy'});
        $.datepicker.setDefaults($.datepicker.regional['${aksess_locale.language}']);
    </script>

    <script type="text/javascript">
        $(document).ready(function() {
            $("#Content .button .ok").click(function(){
                if (buttonOkPressed()) {
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
                return window.opener();
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
