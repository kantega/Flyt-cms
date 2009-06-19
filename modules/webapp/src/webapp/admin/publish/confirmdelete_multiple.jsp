<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
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

    int[] associationIds = param.getInts("associationId");
    List contentList = (List)request.getAttribute("toBeDeleted");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title><kantega:label key="aksess.confirmdelete.multiple.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" type="text/javascript">
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
        <input type="hidden" name="confirmMultipleDelete" value="true">
    <%
        for (int i = 0; i < associationIds.length; i++) {
            %><input type="hidden" name="associationId" value="<%= associationIds[i]%>"><%
        }
    %>
    <p><kantega:label key="aksess.confirmdelete.multiple.sikker"/></p>
    <p><kantega:label key="aksess.confirmdelete.multiple.tekst"/></p>

    <div style="overflow:auto; height:200px;">
        <ul>
            <%
                for (int i = 0; i < contentList.size(); i++) {
                    Content c =  (Content)contentList.get(i);
                    %><li><%=c.getTitle()%></li><%
                }
            %>
        </ul>
    </div>
    <p>
       <a href="Javascript:doDelete()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a>&nbsp;&nbsp;
       <a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
    </p>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>