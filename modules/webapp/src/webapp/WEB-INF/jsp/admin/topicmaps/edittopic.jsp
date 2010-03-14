<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
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
<kantega:section id="title">${topic.baseName}</kantega:section>

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/topicmaps.css">
    <script type="text/javascript">

        function buttonOkPressed() {
            if ($("#TopicName").val() == "") {
                $("#TopicName").focus();
                return false;
            } else {
                $("#EditTopicForm").submit();
            }

            return false;
        }
    </script>
</kantega:section>

<kantega:section id="body">
    <form action="EditTopic.action" method="POST" id="EditTopicForm">
        <c:if test="${associatedTopicId != null}">
            <%-- When creating a new topic, a association to another topic can be created at the same time --%>
            <input type="hidden" name="associatedTopicId" value="${associatedTopicId}">
        </c:if>
        <c:if test="${topicId != null}">
            <%-- Only when editing a existing topic --%>
            <input type="hidden" name="topicId" value="${topicId}">
        </c:if>
        <input type="hidden" name="topicMapId" value="${topic.topicMapId}">

        <div class="fieldset">
            <fieldset>
                <div class="formElement">
                    <div class="heading">
                        <label for="TopicName"><kantega:label key="aksess.topicmaps.name"/></label>
                    </div>
                    <div class="inputs">
                        <input type="text" id="TopicName" name="name"  maxlength="62" value="${topic.baseName}">
                    </div>
                </div>

                <c:if test="${topic.instanceOf == null}">
                    <div class="formElement">
                        <div class="heading">
                            <label for="TopicInstanceOf"><kantega:label key="aksess.topicmaps.instanceof"/></label>
                        </div>
                        <div class="inputs">
                            <select name="instanceOf" id="TopicInstanceOf">
                                <c:forEach var="topicType" items="${topicTypes}">
                                    <option value="${topicType.id}"><c:out value="${topicType.baseName}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </c:if>

                <c:forEach var="occurence" items="${topic.occurences}" varStatus="status">
                    <div class="formElement">
                        <div class="heading">
                            <label for="TopicOccurence${status.index}">
                                <c:choose>
                                    <c:when test="${occurence.instanceOf.baseName != ''}">
                                        <c:out value="${occurence.instanceOf.baseName}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${occurence.instanceOf.id}"/>
                                    </c:otherwise>
                                </c:choose>

                            </label>
                        </div>
                        <div class="inputs">
                            <textarea name="occurence_resourcedata_${status.index}" wrap="soft" rows="6" cols="40"><c:out value="${occurence.resourceData}"/></textarea>
                        </div>
                    </div>
                </c:forEach>

                <div class="buttonGroup">
                    <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                    <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
                </div>
                
            </fieldset>
        </div>
    </form>
</kantega:section>
<%@ include file="../layout/popupLayout.jsp" %>