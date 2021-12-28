<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
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
    if (request.getAttribute("attachmentId") != null){
        int rid = (Integer) request.getAttribute("attachmentId");
        if (id == -1 && rid > -1) id = rid;
    }
    String mimeType = (request.getAttribute("mimeType") != null)? ((String) request.getAttribute("mimeType")).replaceAll("(\\.|/)", "-") : "";
    String fileExtension = (request.getAttribute("fileExtension") != null)? (String) request.getAttribute("fileExtension") : "";
    boolean insertLink = param.getBoolean("insertlink");
%>
<kantega:section id="head">
    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { content : {} };
        }
        properties.contextPath = '${pageContext.request.contextPath}';
    </script>
    <script type="text/javascript" src="<kantega:expireurl url="/aksess/js/aksess-i18n.jjs"/>"></script>
    <script type="text/javascript" src="<kantega:expireurl url="/admin/js/editcontext.js"/>"></script>
    <%--<script type="text/javascript" src="<kantega:expireurl url="/aksess/tiny_mce/tiny_mce_popup.js"/>"></script>--%>

    <script language="Javascript" type="text/javascript">

        var p = getParent();
        if (p) {
        <%
            if (insertLink) {
        %>
            var url = "<%=URLHelper.getRootURL(request)%>attachment.ap?id=<%=id%>",
            clz = 'file <%=fileExtension%> <%=mimeType%>';

            openaksess.editcontext.insertLink({'href': url, 'class': clz});
            closeWindow();
        <%
            } else {

        %>
            window.onbeforeunload = null;
            p.location.reload();
        <%
            }
        %>

        }
    </script>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>