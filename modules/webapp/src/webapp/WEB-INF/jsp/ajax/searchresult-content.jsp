<%@ page contentType="application/json;charset=utf-8" language="java" pageEncoding="utf-8" %><%--
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

[
<c:forEach var="content" items="${contentlist}" varStatus="status">
{"id": "<c:choose><c:when test="${useContentId}">${content.id}</c:when><c:otherwise>${content.association.id}</c:otherwise></c:choose>", "label": "${content.title}", "value": "${content.title}"}
<c:if test="${!status.last}">,</c:if>
</c:forEach>
]
