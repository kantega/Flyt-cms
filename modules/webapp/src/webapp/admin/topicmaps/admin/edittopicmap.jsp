<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ include file="../../include/jsp_header.jsf" %>
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

<%
    RequestParameters param = new RequestParameters(request, "utf-8");

    TopicMap topicmap = new TopicMap();
    int id = param.getInt("id");
    if (id != -1) {
        topicmap = topicService.getTopicMap(id);
    }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>edittopicmap.jsp</title>
    <link rel="stylesheet" type="text/css" href="../../css/<%=skin%>.css">

<script language="Javascript" src="../../js/validation.js"></script>
<script language="Javascript">
function saveForm() {
    var form = document.myform;

    // Reset validationErrors
    validationErrors.length = 0;

    validateChar(form.name, true, false);

    if (showValidationErrors()) {
        form.submit();
    }
}
</script>
</head>

<body class="bodyWithMargin">
<%@ include file="../../include/infobox.jsf" %>
<form name="myform" action="UpdateTopicMap.action" method="post">
    <input type="hidden" name="id" value="<%=topicmap.getId()%>">

    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.topicmaps.admin.name"/></b></td>
        </tr>
        <tr>
            <td><img src="../../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td><input type="text" name="name" title="<kantega:label key="aksess.topicmaps.admin.name"/>" maxlength="64" style="width:600px;" value="<%=topicmap.getName()%>"></td>
        </tr>
        <tr>
            <td><img src="../../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.topicmaps.admin.iseditable"/></b></td>
        </tr>
        <tr>
            <td><img src="../../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <input name="iseditable" type="radio" value="true" <%if (topicmap.isEditable()) out.write(" checked");%>><kantega:label key="aksess.text.ja"/><br>
                <input name="iseditable" type="radio" value="false" <%if (!topicmap.isEditable()) out.write(" checked");%>><kantega:label key="aksess.text.nei"/><br>
            </td>
        </tr>
        <tr>
            <td><img src="../../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
        <!-- WS Operation -->
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.topicmaps.admin.wsoperation"/></b></td>
        </tr>
        <tr>
            <td><img src="../../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>

        <tr>
            <td><input type="text" name="wsoperation" title="<kantega:label key="aksess.topicmaps.admin.wsoperation"/>" maxlength="64" style="width:600px;" value="<%=topicmap.getWSOperation()%>"></td>
        </tr>
        <tr>
            <td><img src="../../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
        <!-- WS SOAP Action -->
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.topicmaps.admin.wssoapaction"/></b></td>
        </tr>
        <tr>
            <td><img src="../../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>

        <tr>
            <td><input type="text" name="name" title="<kantega:label key="aksess.topicmaps.admin.wssoapaction"/>" maxlength="255" style="width:600px;" value="<%=topicmap.getWSSoapAction()%>"></td>
        </tr>
        <tr>
            <td><img src="../../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
        <!-- WS Endpoint -->

        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.topicmaps.admin.wsendpoint"/></b></td>
        </tr>
        <tr>
            <td><img src="../../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>

        <tr>
            <td><input type="text" name="wsendpoint" title="<kantega:label key="aksess.topicmaps.admin.wsendpoint"/>" maxlength="255" style="width:600px;" value="<%=topicmap.getWSEndPoint()%>"></td>
        </tr>
        <tr>
            <td><img src="../../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
    </table>
    <p>
        <a href="Javascript:saveForm()"><img src="../../bitmaps/<%=skin%>/buttons/lagre.gif" border="0"></a>&nbsp;&nbsp;<a href="Javascript:location='index.jsp'"><img src="../../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
    </p>
</form>
</body>
</html>
<%@ include file="../../include/jsp_footer.jsf" %>