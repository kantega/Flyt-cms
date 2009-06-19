<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.publishing.common.cache.SiteCache"%>
<%@ page import="no.kantega.publishing.common.data.*" %>
<%@ page import="no.kantega.publishing.common.data.enums.AssociationType" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege" %>
<%@ page import="java.util.List" %>
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
    RequestParameters param = new RequestParameters(request, "utf-8");

    // Hent knytning
    int associationId = param.getInt("id");
    Association a = aksessService.getAssociationById(associationId);

    // Hent innholdsside som knytningen peker på
    ContentIdentifier cid = new ContentIdentifier();
    cid.setAssociationId(a.getAssociationId());
    Content content = aksessService.getContent(cid);

    String contentTitle = "";
    if (content.getTitle() != null) {
        contentTitle = content.getTitle();
    }
    if (contentTitle.length() > 30) contentTitle = contentTitle.substring(0, 27) + "...";

    boolean isAuthorized = false;
    if (securitySession.isAuthorized(content, Privilege.APPROVE_CONTENT)) {
        isAuthorized = true;
    }

    boolean isCrossPublished = false;
    List associations = content.getAssociations();
    if (associations != null) {       
        if (associations.size() > 1) {
            isCrossPublished = true;
        }
    }

    String target = param.getString("target");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title><kantega:label key="aksess.confirmdelete.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" type="text/javascript">
function selectAll(btn) {
    var f = document.myform.associationId;
    if (f.length) {
        for (var i = 0; i < f.length; i++) {
            f[i].checked = btn.checked;
        }
    } else {
        f.checked = btn.checked;
    }
}

var hasSubmitted = false;

function doDelete() {
    // Prevent user from clicking several times
    if (!hasSubmitted) {
        hasSubmitted = true;
        document.myform.submit();
    }
}
</script>
<body class="bodyWithMargin">
    <form action="DeleteAssociation.action" name="myform">
<%
    if (!isAuthorized) {
%>
    <p><kantega:label key="aksess.confirmdelete.notauthorized"/></p>
    <p><a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a></p>
<%
    } else if (content.isLocked()) {
%>
    <p><kantega:label key="aksess.confirmdelete.locked"/></p>
    <p><a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a></p>
<%
    } else {
        if (!isCrossPublished) {
    %>
    <input type="hidden" name="associationId" value="<%=associationId%>">
    <p><kantega:label key="aksess.confirmdelete.text"/> <b><%=contentTitle%></b>?
    </p>

    <%
        } else {
    %>
    <p><kantega:label key="aksess.confirmdelete.xptext"/> <b><%=contentTitle%></b> <kantega:label key="aksess.confirmdelete.xptext"/>
    <br>
    <br>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr class="tableHeading">
                <td colspan="2"><kantega:label key="aksess.confirmdelete.xpinstance"/></td>
            </tr>
            <tr class="tableRow1">
                <td><input type="checkbox" name="dummy" value="-1" onclick="selectAll(this)"></td>
                <td><kantega:label key="aksess.confirmdelete.xpall"/></td>
            </tr>
            <%
            associations = content.getAssociations();
            if (associations != null) {
                List path = null;
                for (int i = 0; i < associations.size(); i++) {
                    Association association = (Association)associations.get(i);
                    path = aksessService.getPathByAssociation(association);
                    out.write("<tr class=\"tableRow" + (i%2) + "\">");
                    String sel = "";
                    if (associationId == association.getId()) sel = " checked";
                    out.write("<td><input type=\"checkbox\" name=\"associationId\" value=\"" + association.getId() + "\"" + sel + "></td>");
                    out.write("<td>");
                    for (int j = 0; j < path.size(); j++) {
                        PathEntry entry = (PathEntry)path.get(j);
                        String title = entry.getTitle();
                        if (j == 0) {
                            // First level, print name of site, not pagetitle
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
                        %>
                        (<kantega:label key="aksess.confirmdelete.shortcut"/>)
                        <%
                    }
                    out.write("</td></tr>");
                }
            }
                %>
            <tr>
                <td colspan="2"><br>
                    <div class=helpText><kantega:label key="aksess.confirmdelete.xpinfo"/></div>
                </td>
            </tr>
        </table><br>
    </p>

    <%
        }
    %>
<p>&nbsp;</p>
<p>
    <a href="Javascript:doDelete()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a>&nbsp;&nbsp;
    <a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
</p>
<%
    }
%>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>