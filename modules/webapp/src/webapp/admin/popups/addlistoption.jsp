<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
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

<%--
  User: Kristian Lier Selnæs, Kantega AS
  Date: Jun 11, 2007
  Time: 12:52:03 PM
--%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
<%
    RequestParameters param = new RequestParameters(request);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title><kantega:label key="aksess.editablelist.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>

<script type="text/javascript" language="Javascript" src="../../aksess/js/common.js"></script>
<script language="Javascript">

    function addListOption() {

        var optionValue = document.myform.optionValue.value;
        var attributeKey = '<%=param.getString("attributeKey")%>';
        var defaultSelected = document.myform.defaultSelected.value;
        var language = <%=param.getString("language")%>;

        if (optionValue == "") {
            alert("<kantega:label key="aksess.editablelist.missingvalue"/>");
        }
        else {
            var xmlhttp = getXmlHttp();
            xmlhttp.open("POST",  "../publish/AddListOption.action", true);
            xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            xmlhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xmlhttp.onreadystatechange=function() {
                if (xmlhttp.readyState==4) {
                    if(xmlhttp.responseText == "success") {
                        window.opener.insertOptionIntoList(optionValue);
                        setTimeout("window.close()", 1);
                    }
                    else {
                        alert("<kantega:label key="aksess.editablelist.error"/>");
                    }
                }
            }
            xmlhttp.send("value=" + optionValue + "&attributeKey=" + attributeKey + "&defaultSelected=" + defaultSelected + "&language=" + language);
        }
    }


</script>

<body class="bodyWithMargin">
<form name="myform">
    <table border="0" width="100%" cellspacing="0">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.editablelist.title"/></b></td>
        </tr>
        <tr>
            <td>
                <b><kantega:label key="aksess.editablelist.value"/></b><br>
                <input type="text" name="optionValue" style="width:250px;" maxlength="255">
            </td>
        </tr>
        <tr>
            <td>
                <input type="checkbox" name="defaultSelected" value="true">
                <kantega:label key="aksess.editablelist.defaultSelected"/>
            </td>
        </tr>
    </table>
    <p>
        <a href="Javascript:addListOption()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a>&nbsp;&nbsp;<a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
    </p>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>