<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
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
    ContentManagementService cms = new ContentManagementService(request);
    List<Content> forApproval = cms.getContentListForApproval();

    RequestParameters param = new RequestParameters(request);
    int thisId = param.getInt("thisId");
    int contentId = param.getInt("contentId");

    if (forApproval.size() > 0 && thisId == -1 && contentId == -1) {
        // User has pages for approval, show my page
        request.getRequestDispatcher("/admin/mypage/ViewMyPage.action").forward(request,response);
    } else {
        // Goto navigate
        String idUrl = "";
        if (thisId != -1) {
            idUrl = "?thisId=" + thisId;
        } else if (contentId != -1) {
            idUrl = "?contentId=" + contentId;
        }

        request.getRequestDispatcher("/admin/publish/Navigate.action" + idUrl).forward(request,response);
    }
%>

