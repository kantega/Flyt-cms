<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege,
                 no.kantega.publishing.security.data.Role"%>
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
    Topic topic  = (Topic)session.getAttribute("currentTopic");
    if (topic == null || topicMap == null) {
        response.sendRedirect("start.jsp");
    }

    List associations = topicService.getTopicAssociations(topic);

    Topic instanceOf = topic.getInstanceOf();
    if (instanceOf != null) {
        instanceOf = topicService.getTopic(topic.getTopicMapId(), instanceOf.getId());
    }

    boolean canUpdate = securitySession.isAuthorized(topicMap, Privilege.UPDATE_CONTENT);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>topic.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/topicmap.js"></script>
<script language="Javascript">
function addTopic() {
<%
    if (topic != null) {
%>
    window.parent.parent.opener.addTopic(<%=topic.getTopicMapId()%>, '<%=topic.getId()%>', '<%=topic.getBaseName()%>');
    window.parent.parent.close();
<%
    }
%>
}

function removeContent(contentId) {
    if (confirm('<kantega:label key="aksess.topicmaps.removeassociatedcontent"/>')) {
        location = "../publish/DeleteContentTopic.action?contentId=" + contentId + "&topicMapId=<%=topic.getTopicMapId()%>&topicId=<%=topic.getId()%>"
    }
}

function addRole() {
<%
    if (topic != null) {
%>
    var rolewin = window.open("../security/SelectRoles.action?action=AddTopicRole", "rolewin", "toolbar=no,width=400,height=300,resizable=yes,scrollbars=no");
    rolewin.focus();
<%
    }
%>
}

function removeRole(roleId) {
    if (confirm('<kantega:label key="aksess.topicmaps.removeassociatedrole"/>')) {
        location = "../topicmaps/DeleteRoleTopic.action?roleId=" + roleId + "&topicMapId=<%=topic.getTopicMapId()%>&topicId=<%=topic.getId()%>"
    }
}

</script>
<body class="bodyWithMargin">
<table border="0" cellspacing="0" cellpadding="0" width="90%">
    <tr class="inpHeading">
        <td><b><%=topic.getBaseName()%></b></td>
    </tr>
    <tr>
        <td>
            <table border="0" cellspacing="0" cellpadding="0">
            <%
                if (instanceOf != null) {
            %>
            <tr>
                <td><b><kantega:label key="aksess.topicmaps.instanceof"/>:</b></td>
                <td>&nbsp;</td>
                <td><a href="Javascript:gotoInstances(<%=instanceOf.getTopicMapId()%>, '<%=instanceOf.getId()%>')"><%=instanceOf.getBaseName()%></a></td>
            </tr>
            <%
            }
            List occurences = topic.getOccurences();
            if (occurences != null) {
                for (int i = 0; i < occurences.size(); i++) {
                    TopicOccurence occurence = (TopicOccurence)occurences.get(i);
                    String resdata = occurence.getResourceData().trim();
                    String basename = "";
                    if (resdata.length() > 0) {
                        Topic topicOccurence = occurence.getInstanceOf();
                        if (topicOccurence != null) {
                            basename =  topicOccurence.getBaseName();
                        }
                        out.write("<tr valign=\"top\"><td><b>" + basename +":</b></td><td>&nbsp;</td><td>" + resdata + "</td></tr>");
                    }
                }
            }
            %>
            </table>
        </td>
    </tr>
</table>
<p>&nbsp;</p>
<table border="0" cellspacing="0" cellpadding="0" width="90%">
    <tr>
        <td width="5%">&nbsp;</td>
        <td width="95%">&nbsp;</td>
    </tr>
    <tr class="inpHeading">
        <td colspan="2"><b><b><kantega:label key="aksess.topicmaps.associatedtopics"/></b></td>
    </tr>
    <%
        String prevRole = "";

        for (int i = 0; i < associations.size(); i++) {
            TopicAssociation association = (TopicAssociation)associations.get(i);

            Topic topicRef = association.getAssociatedTopicRef();
            String name = topicRef.getBaseName();

            Topic role = association.getRolespec();
            String roleName = role.getBaseName();
            if (!prevRole.equalsIgnoreCase(roleName)) {
                prevRole = roleName;
                out.write("<tr><td colspan=\"2\">" + roleName + "</td></tr>");
            }
%>
            <tr><td>&nbsp;</td><td><a href="Javascript:gotoTopic(<%=topicMap.getId()%>, '<%=topicRef.getId()%>')"><%=name%></a></td></tr>
<%
        }
%>
</table>
<%
    ContentQuery query = new ContentQuery();
    query.setTopic(topic);
    List contentList = aksessService.getContentSummaryList(query, -1, new SortOrder("title", false));
    if (contentList != null && contentList.size() > 0) {
%>
<table border="0" cellspacing="0" cellpadding="0" width="90%">
    <tr>
        <td width="5%">&nbsp;</td>
        <td width="85%">&nbsp;</td>
        <td width="10%">&nbsp;</td>
    </tr>
    <tr class="inpHeading">
        <td colspan="3"><b><b><kantega:label key="aksess.topicmaps.associatedcontent"/></b></td>
    </tr>
    <%
        for (int i = 0; i < contentList.size(); i++) {
            Content content = (Content)contentList.get(i);
    %>
        <tr>
            <td>&nbsp;</td>
            <td><a href="<%=content.getUrl()%>" target="_new"><%=content.getTitle()%></a></td>
            <td align="right">
            <%
                if (securitySession.isAuthorized(content, Privilege.UPDATE_CONTENT)) {
            %>
                    <table border="0">
                        <tr>
                            <td><a href="Javascript:removeContent(<%=content.getId()%>)"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></td>
                            <td><a href="Javascript:removeContent(<%=content.getId()%>)" class="button"><kantega:label key="aksess.button.delete"/></td>
                        </tr>
                    </table>
            <%
                }
            %>
            </td>
        </tr>
    <%
        }
    %>
</table>
<%
    }
%>

<%
    if (securitySession.isUserInRole(Aksess.getAdminRole())) {
        List roles = topicService.getRolesByTopic(topic);
%>
<table border="0" cellspacing="0" cellpadding="0" width="90%">
    <tr>
        <td width="5%">&nbsp;</td>
        <td width="85%">&nbsp;</td>
        <td width="10%">&nbsp;</td>
    </tr>
    <tr class="inpHeading">
        <td colspan="3"><b><b><kantega:label key="aksess.topicmaps.associatedroles"/></b></td>
    </tr>
    <%
        for (int i = 0; i < roles.size(); i++) {
            Role role = (Role)roles.get(i);
    %>
    <tr>
        <td>&nbsp;</td>
        <td><%=role.getName()%></td>
        <td align="right">
            <%
                if (canUpdate) {
            %>
            <table border="0">
                <tr>
                    <td><a href="Javascript:removeRole('<%=role.getId()%>')"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></td>
                    <td><a href="Javascript:removeRole('<%=role.getId()%>')" class="button"><kantega:label key="aksess.button.delete"/></td>
                </tr>
            </table>
            <%
                }
            %>
        </td>
    </tr>
    <%
        }
        if (canUpdate) {

    %>
    <tr>
        <td colspan="3" align="right">
            <table border="0">
                <tr>
                    <td><a href="Javascript:addRole()"><img src="../bitmaps/common/buttons/innhold_legg_til_emner.gif" border="0"></td>
                    <td><a href="Javascript:addRole()" class="button"><kantega:label key="aksess.button.leggtil"/></td>
                </tr>
            </table>

        </td>
    </tr>
    <%
            }
        }
    %>
</table>

</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>