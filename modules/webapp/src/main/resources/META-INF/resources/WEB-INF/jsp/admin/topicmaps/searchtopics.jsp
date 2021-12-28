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
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>

<input type="hidden" name="topicMapId" value="${topicMapId}" class="topicMapId">

<div id="TopicFilter">
    <label for="TopicQuery${topicMapId}"><kantega:label key="aksess.topics.filter"/>:</label> <input type="text" name="TopicQuery${topicMapId}" class="topicQuery" id="TopicQuery${topicMapId}">
</div>

<div class="topicList">
    <ol class="alphabeticalList columnized columnCount3">
        <c:forEach var="letter" items="${topics}">
            <li class="letter" id="Letter_${letter.key}"><span class="letter">${letter.key}</span>
                <ol>
                    <c:forEach var="topic" items="${letter.value}">
                        <li><a href="ViewTopic.action?topicId=${topic.id}&amp;topicMapId=${topic.topicMapId}" class="topic">${topic.baseName}</a>&nbsp;<span class="type">(${topic.instanceOf.baseName})</span></li>
                    </c:forEach>
                </ol>
            </li>
        </c:forEach>
    </ol>
</div>