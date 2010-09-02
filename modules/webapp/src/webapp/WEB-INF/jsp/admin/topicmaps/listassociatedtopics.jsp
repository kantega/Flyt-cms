<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%--
  ~ /*
  ~  * Copyright 2009 Kantega AS
  ~  *
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
<table id="AssociatedTopics">
    <thead>
        <tr>
            <th><kantega:label key="aksess.viewtopic.association"/></th>
            <th><kantega:label key="aksess.contentproperty.topics"/></th>
            <c:if test="${canDelete}">
                <th>&nbsp;</th>
            </c:if>
        </tr>
    </thead>
    <c:set var="prevRoleSpec" value=""/>
    <c:forEach var="topicAssociation" items="${associatedTopics}" varStatus="status">
        <tr class="tableRow${status.index mod 2}">
            <td>
                <c:if test="${topicAssociation.rolespec != null && topicAssociation.rolespec.baseName != prevRoleSpec}">
                    <c:out value="${topicAssociation.rolespec.baseName}"/>
                    <c:set var="prevRoleSpec" value="${topicAssociation.rolespec.baseName}"/>
                </c:if>
            </td>
            <td><a href="ViewTopic.action?topicId=${topicAssociation.associatedTopicRef.id}&amp;topicMapId=${topicAssociation.associatedTopicRef.topicMapId}"><c:out value="${topicAssociation.associatedTopicRef.baseName}"/></a></td>
            <c:if test="${canDelete}">
                <td align="right"><a href="ListAssociatedTopics.action?topicMapId=${topic.topicMapId}&amp;topicId=${topic.id}&amp;deleteId=${topicAssociation.associatedTopicRef.id}" target="_new" class="button delete"><span><kantega:label key="aksess.button.delete"/></span></a></td>
            </c:if>
        </tr>
    </c:forEach>
</table>
<c:if test="${canAdd}">
    <div id="AssociatedTopicsButtonGroup" class="buttonGroup">
    <div id="AddNewTopic"><a class="button" href="EditTopic.action?associatedTopicId=${topic.id}&amp;topicMapId=${topic.topicMapId}"><span class="add"><kantega:label key="aksess.viewtopic.newrelatedtopic"/></span></a></div>
    <div id="AddExistingTopic"><label><kantega:label key="aksess.viewtopic.addexistingtopic"/>:</label> <input type="text" size="20"></div>
    </div>
</c:if>
