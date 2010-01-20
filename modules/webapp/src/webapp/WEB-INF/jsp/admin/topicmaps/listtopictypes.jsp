<%@ page import="no.kantega.publishing.common.Aksess" %>
<%--
~ Copyright 2009 Kantega AS
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~     http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
--%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>

<div class="tabGroup">
<c:forEach items="${topicTypes}" var="topicType">
    <div class="tab <c:if test="${currentTopicType != null && currentTopicType.topicMapId == topicType.topicMapId && currentTopicType.id == topicType.id}"> active</c:if>">
        <a href="?topicMapId=${topicType.topicMapId}&amp;topicId=${topicType.id}"><c:out value="${topicType.baseName}"/></a>
    </div>
</c:forEach>
</div>
<div class="tabContent">
    <ul>
        <c:forEach var="topic" items="${topics}">
            <li>
                <a href="ViewTopic.action?topicMapId=${topic.topicMapId}&amp;topicId=${topic.id}"><c:out value="${topic.baseName}"/></a>
            </li>
        </c:forEach>
    </ul>

</div>

