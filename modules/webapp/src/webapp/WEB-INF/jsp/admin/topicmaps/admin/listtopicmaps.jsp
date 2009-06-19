<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../../../../../admin/include/jsp_header.jsf" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>listtopicmaps.jsp</title>
    <link rel="stylesheet" type="text/css" href="../../css/<%=skin%>.css">
</head>
<script language="Javascript">
function deleteTopicMap(id, name) {
    if (confirm("<kantega:label key="aksess.topicmaps.admin.confirmdelete"/> " + name + "?")) {
        window.location.href = "DeleteTopicMap.action?id=" + id;
    }
}

</script>
<body class="bodyWithMargin">
<table border="0" cellspacing="0" cellpadding="0" width="600">
    <tr class="tableHeading">
        <td><strong><kantega:label key="aksess.topicmaps.admin.topicmap"/></strong></td>
        <td><strong><kantega:label key="aksess.topicmaps.admin.editable"/></strong></td>
        <td>&nbsp;</td>
    </tr>
<%
    List topicmaps = topicService.getTopicMaps();
    int i;
    for (i = 0; i < topicmaps.size(); i++) {
        TopicMap topicmap = (TopicMap)topicmaps.get(i);
        String statusKey = "aksess.text.nei";
        if (topicmap.isEditable()) {
            statusKey = "aksess.text.ja";
        }
%>
        <tr class="tableRow<%=(i%2)%>">
            <td><%=topicmap.getName()%></td>
            <td><kantega:label key="<%=statusKey%>"/></td>
            <td align="right">
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><a href="EditTopicMap.action?id=<%=topicmap.getId()%>"><img src="../../bitmaps/common/buttons/mini_rediger.gif" border="0"></a></td>
                        <td><a href="EditTopicMap.action?id=<%=topicmap.getId()%>" class="button"><kantega:label key="aksess.button.rediger"/></a></td>
                        <td><img src="../../bitmaps/common/textseparator.gif" alt=""></td>
                        <td><a href="Javascript:deleteTopicMap(<%=topicmap.getId()%>, '<%=topicmap.getName()%>')"><img src="../../bitmaps/common/buttons/mini_slett.gif" border="0"></td>
                        <td><a href="Javascript:deleteTopicMap(<%=topicmap.getId()%>, '<%=topicmap.getName()%>')" class="button"><kantega:label key="aksess.button.slett"/></td>
                    </tr>
                </table>
            </td>
        </tr>
<%

    }
%>
    <tr class="tableRow0">
        <td colspan="3" align="right"><a href="EditTopicMap.action" class="button"><kantega:label key="aksess.topicmaps.admin.newmap"/></a></td>
    </tr>
</table>
</body>
</html>
<%@ include file="../../../../../admin/include/jsp_footer.jsf" %>