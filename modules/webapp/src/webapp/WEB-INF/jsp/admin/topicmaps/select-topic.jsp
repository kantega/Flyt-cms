<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<kantega:section id="title"><kantega:label key=""/></kantega:section>

<kantega:section id="head">
    <script type="text/javascript">
        // For multiple items
        function addSelectedTopics() {
            $('.checkbox:checked').each(
                    function() {
                        var topicMapIdAndId = this.value.split(":");
                        var topicMapId = topicMapIdAndId[0];
                        var topicId = topicMapIdAndId[1];
                        window.opener.addTopic(topicMapId, topicId, '');

                    }
                    );
            window.close();
        }

        // When only one item can be selected
        function addTopic(topicMapId, topicId, topicName) {
            window.opener.addTopic(topicMapId, topicId, topicName);
            window.close();
        }


    </script>
</kantega:section>

<kantega:section id="body">
    <form name="myform" action="SelectTopics.action" method="post">
        <input type="hidden" name="selectMultiple" value="${selectMultiple}">
        <div class="fieldset">
            <fieldset>
                <legend><kantega:label key="aksess.selecttopic.title"/></legend>

                <div class="formElement">
                    <div class="heading">
                        <label for="topictype"><kantega:label key="aksess.selecttopic.topictype"/></label>
                    </div>
                    <div class="inputs">
                        <select name="topictype" id="topictype" onchange="document.myform.submit();">
                            <option value=""></option>
                            <c:forEach var="topicMap" items="${topicMaps}">
                                <c:if test="${fn:length(topicMaps) > 1}">
                                    <optgroup label="${topicMap.name}">
                                </c:if>
                                <c:forEach var="topicType" items="${topicMap.topicTypes}">
                                    <option value="${topicMap.id}:<c:out value="${topicType.id}"/>"><c:out value="${topicType.baseName}"/></option>
                                </c:forEach>
                                <c:if test="${fn:length(topicMaps) > 1}">
                                    </optgroup>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="formElement">
                    <div class="content">
                        <table>
                            <tr>
                                <c:if test="${selectMultiple}">
                                    <th width="20">&nbsp;</th>
                                </c:if>
                                <th><kantega:label key="aksess.selecttopic.topic"/></th>
                            </tr>
                            <c:forEach var="topic" items="${topics}" varStatus="">
                                <tr>
                                    <c:choose>
                                        <c:when test="${selectMultiple}">
                                            <td><input type="checkbox" class="checkbox" name="topicId" id="topic${status.index}" value="<c:out value="${topic.topicMapId}"/>:<c:out value="${topic.id}"/>"></td>
                                            <td><label for="topic${status.index}"><c:out value="${topic.baseName}"/></label></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><a href="Javascript:addTopic(${topic.topicMapId}, '<c:out value="${topic.id}"/>', '<c:out value="${topic.baseName}"/>')"><c:out value="${topic.baseName}"/></a></td>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </fieldset>
        </div>

        <div class="buttonGroup">
            <c:if test="${selectMultiple}">
                <input type="button" onclick="addSelectedTopics()" class="button ok" value="<kantega:label key="aksess.button.ok"/>">
            </c:if>

            <input type="button" onclick="window.close()" class="button cancel" value="<kantega:label key="aksess.button.cancel"/>">
        </div>
    </form>
</kantega:section>
<%@ include file="../layout/popupLayout.jsp" %>
