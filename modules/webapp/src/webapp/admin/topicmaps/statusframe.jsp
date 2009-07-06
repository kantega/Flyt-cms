<%@ page import="no.kantega.publishing.topicmaps.data.Topic" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
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
    RequestParameters param = new RequestParameters(request);
    String activetab = param.getString("activetab");
    String statusmessage = request.getParameter("statusmessage");
    Topic topic  = (Topic)session.getAttribute("currentTopic");
    Topic instanceOf = null;
    if (topic != null && topic.getInstanceOf() != null) {
        instanceOf = topicService.getTopic(topic.getTopicMapId(), topic.getInstanceOf().getId());
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>statusframe.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="framework">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td width="22"><img src="../bitmaps/<%=skin%>/framework/navigatorsplit_bottom.gif" width="22" height="28"></td>
        <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr height="3">
                    <td background="<%=framework_bitmaps%>/status_shadow.gif"><img src="<%=framework_bitmaps%>/status_shadow_corner.gif" width="1" height="3"></td>
                </tr>
                <tr height="2">
                    <td><img src="../bitmaps/blank.gif" width="1" height="2"></td>
                </tr>
                <tr height="21">
                    <td>
                        <table border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <%
                                    if ("topic".equalsIgnoreCase(activetab))  {%>
                                        <script language="Javascript">
                                            if (window.parent && window.parent.parent && window.parent.parent.opener) {
                                                document.write('<td>');
                                                <%
                                                    if (instanceOf != null && instanceOf.isSelectable()) {
                                                %>
                                                    document.write('<a href="Javascript:window.parent.contentmain.addTopic()"><img src="../bitmaps/<%=skin%>/buttons/legg_til.gif" border="0"></a>');
                                                <%
                                                    }
                                                %>
                                                document.write('<a href="Javascript:window.parent.parent.close()"><img src="../bitmaps/<%=skin%>/buttons/lukk_vindu.gif" border="0"></a>');
                                                document.write('</td>');
                                            }
                                        </script>
                                <%
                                    } else if ("edittopic".equalsIgnoreCase(activetab)) {
                                %>
                                        <td>
                                            <a href="Javascript:window.parent.contentmain.saveForm()"><img src="../bitmaps/<%=skin%>/buttons/lagre.gif" border="0"></a>
                                            <a href="Javascript:history.back()"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
                                        </td>
                                <%
                                    }
                                %>
                                <td>
                                <%
                                    if ("topic".equalsIgnoreCase(activetab)) { %>
                                    <script language="Javascript">
                                        if (window.parent && window.parent.parent && window.parent.parent.opener) {
                                            document.write('<img src="../bitmaps/common/textseparator.gif" width="13" height="9">');
                                        }
                                    </script>
                                <%
                                    } else if ("edittopic".equalsIgnoreCase(activetab)) {
                                %>
                                    <img src="../bitmaps/common/textseparator.gif" width="13" height="9">
                                <%
                                    }
                                    if (statusmessage != null) {
                                        String key = "aksess.statusmessage." + statusmessage;
                                %>
                                    <kantega:label key="<%=key%>"/>
                                <%
                                    }
                                %>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>