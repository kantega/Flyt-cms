<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<c:forEach var="topic" items="${content.topics}">
<div class="topic">
    <div class="topicname">${topic.baseName}</div> <div class="buttonGroup"><a href="?topicId=${topic.id}&amp;topicMapId=${topic.topicMapId}" class="delete"></a></div>
</div>
</c:forEach>