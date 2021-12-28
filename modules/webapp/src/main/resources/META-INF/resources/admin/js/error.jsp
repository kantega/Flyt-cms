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

function reportJSError(message, url, line) {
    var param = "message=" + escape(message) + "&url=" + escape(url) + "&line=" + line;

    // Create a image to log message
    var errorImage = new Image(1,1);
    errorImage.src = "<%=request.getContextPath()%>/admin/LogJSError.action?" + param;
}

onerror = reportJSError;