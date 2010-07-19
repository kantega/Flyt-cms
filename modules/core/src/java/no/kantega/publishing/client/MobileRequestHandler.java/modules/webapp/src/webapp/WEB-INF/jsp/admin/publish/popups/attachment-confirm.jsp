<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.commons.util.URLHelper"%>
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
    int id = param.getInt("attachmentId");
    boolean insertLink = param.getBoolean("insertLink");
%>
<kantega:section id="head">
<script language="Javascript" type="text/javascript">

    var p = getParent();
    if (p) {
        <%
            if (insertLink) {
        %>
                var url = "<%=URLHelper.getRootURL(request)%>attachment.ap?id=<%=id%>";
                p.createLink(url);
                closeWindow();
        <%
            } else {
        %>
                p.location.reload();
        <%
            }
        %>

    }
</script>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>