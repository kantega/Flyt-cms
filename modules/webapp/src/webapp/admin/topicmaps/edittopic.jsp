<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
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
    TopicMap topicMap = (TopicMap)session.getAttribute("currentTopicMap");
    Topic current = (Topic)session.getAttribute("currentTopic");
    if (topicMap == null) {
        response.sendRedirect("start.jsp");
    }

    RequestParameters param = new RequestParameters(request, "utf-8");   
    String task = param.getString("task");

    Topic topic = new Topic();

    if ("edit".equalsIgnoreCase(task)) {
        // Endre eksisterende
        topic = current;
    } else {
        if (current.isTopicType()) {
            topic.setInstanceOf(current);
        }
    }

    String name = topic.getBaseName();
    if (name == null) {
        name = "";
    }

    List occurences = topic.getOccurences();

    // Hent ut mulige topictypes
    List topictypes = topicService.getTopicTypes(topicMap.getId());
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>edittopicmap.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">

<script language="Javascript" src="../js/validation.js"></script>
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
<%@ include file="/WEB-INF/jsp/admin/publish/fragments/infobox.jsp" %>
<form name="myform" action="UpdateTopic.action" target="content" method="post">
    <input type="hidden" name="task" value="<%=task%>">
    <table border="0" cellspacing="0" cellpadding="0" width="500">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.topicmaps.name"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <%
            if (topicMap.isEditable()) {
        %>
        <tr>
            <td><input type="text" name="name" title="<kantega:label key="aksess.topicmaps.name"/>" maxlength="62" style="width:500px;" value="<%=name%>"></td>
        </tr>

        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16" alt=""></td>
        </tr>
        <%
                if (topic.getInstanceOf() == null) {
        %>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.topicmaps.instanceof"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <tr>
            <td>
                <select name="instanceof" style="width:500px;">
                <%
                    for (int i = 0; i < topictypes.size(); i++) {
                        Topic topicType = (Topic)topictypes.get(i);
                        if (topic.getInstanceOf() != null && topic.getInstanceOf().getId().equalsIgnoreCase(topicType.getId())) {
                            out.write("<option value=\"" + topicType.getId() + "\" selected>" + topicType.getBaseName() + "</option>");
                        } else {
                            out.write("<option value=\"" + topicType.getId() + "\">" + topicType.getBaseName() + "</option>");
                        }
                    }
                %>
                </select>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td><%=name%><input type="hidden" name="name" value="<%=name%>"></td>
        </tr>
        <%
            }
        %>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16" alt=""></td>
        </tr>
        <%
            if (occurences != null && occurences.size() > 0) {
        %>
                <tr>
                    <td class="tableHeading"><b><kantega:label key="aksess.topicmaps.occurences"/></b></td>
                </tr>
                <tr>
                    <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
                </tr>
                <tr>
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0" width="500">
                        <%
                            for (int i = 0; i < occurences.size(); i++) {
                                TopicOccurence occurence = (TopicOccurence)occurences.get(i);
                                String basename = "";
                                Topic occurenceInstanceOf = occurence.getInstanceOf();
                                if (occurenceInstanceOf != null) {
                                    basename =  occurenceInstanceOf.getBaseName();
                        %>
                                    <input type="hidden" name="occurence_instanceof_<%=i%>" value="<%=occurenceInstanceOf.getId()%>">
                        <%
                                }
                        %>
                                <tr>
                                    <td width="200" valign="top"><b><%=basename%></b></td>
                                    <td width="300">
                                        <textarea name="occurence_resourcedata_<%=i%>" wrap="soft" rows="6" cols="40" style="width:300px;"><%=occurence.getResourceData().trim()%></textarea>
                                    </td>
                                </tr>
                        <%
                            }
                        %>
                        </table>
            </td>
        </tr>
        <%
            }
        %>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16" alt=""></td>
        </tr>
    </table>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>