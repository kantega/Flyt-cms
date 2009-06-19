<%@ page import="no.kantega.publishing.common.cache.SiteCache"%>
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
    Content current = (Content)session.getAttribute("currentContent");
    if (current != null) {
        List path = null;
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title><kantega:label key="aksess.showassociations.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
    <table border="0" width="100%" cellspacing="0" cellpadding="0">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.showassociations.plassering"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                <%
                    List associations = current.getAssociations();
                    if (associations != null) {
                        for (int i = 0; i < associations.size(); i++) {
                            Association association = (Association)associations.get(i);
                            path = aksessService.getPathByAssociation(association);
                            out.write("<tr class=\"tableRow" + (i%2) + "\"><td>");
                            for (int j = 0; j < path.size(); j++) {
                                PathEntry entry = (PathEntry)path.get(j);
                                String title = entry.getTitle();
                                if (j == 0) {
                                    // På første nivået skriver vi navnet på nettstedet
                                    Site site = SiteCache.getSiteById(association.getSiteId());
                                    out.write(site.getName());
                                } else {
                                    if (j > 0) {
                                        out.write("&nbsp;&gt;&nbsp;");
                                    }
                                    out.write(title);
                                }
                            }
                            if (association.getAssociationtype() == AssociationType.SHORTCUT) {
                                out.write(" (snarvei)");
                            }
                            out.write("</td></tr>");
                        }
                    }
                %>
                </table>
            </td>
        </tr>
    </table><br>
    <a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a>

</body>
</html>
<%
    }
%>
<%@ include file="../include/jsp_footer.jsf" %>
