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
    String roletype = param.getString("roletype");
    String action = param.getString("action");
    boolean select = param.getBoolean("select", false);
    if (action == null || action.length() == 0) {
        action = "AddContentRolePermission";
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title><kantega:label key="aksess.addroleoruser.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" type="text/javascript">
function addRoles() {
    document.getElementById('roles').contentWindow.document.roles.submit();
}
</script>
<body>
    <div class="padded">
        <iframe name="roles" id="roles" width="400" height="250" src="addroleoruser_list.jsp?roletype=<%=roletype%>&action=<%=action%>&select=<%=select%>"></iframe>
    </div>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
             <td background="../bitmaps/<%=skin%>/framework/navigator_bottom.gif"><img src="../bitmaps/blank.gif" width="4" height="4" alt=""></td>
         </tr>
         <tr>
             <td class="framework">
                 <%
                     if (select) {
                 %>
                 <a href="Javascript:addRoles()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0" hspace="4" vspace="4"></a>
                 <%
                     }
                 %>
                 <a href="Javascript:window.parent.close()"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0" hspace="4" vspace="4"></a></td>
         </tr>
         <tr>
             <td class="framework">&nbsp;</td>
         </tr>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
