<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<kantega:section id="title">
    <kantega:label key="aksess.topicmaps.title"/>
</kantega:section>

<kantega:section id="content">
    <script type="text/javascript">
        function deleteTopicMap(id, name) {
            if (confirm("<kantega:label key="aksess.topicmaps.admin.confirmdelete"/> " + name + "?")) {
                window.location.href = "DeleteTopicMap.action?id=" + id;
            }
        }
        function importTopicMap(id, name) {
            if (confirm("<kantega:label key="aksess.topicmaps.admin.confirmimport"/> " + name + "?")) {
                window.location.href = "ImportTopicMap.action?id=" + id;
            }
        }
    </script>
    <admin:box>


        <h1><kantega:label key="aksess.topicmaps.title"/></h1>
        <table>
            <tr class="tableHeading">
                <td><strong><kantega:label key="aksess.topicmaps.admin.topicmap"/></strong></td>
                <td><strong><kantega:label key="aksess.topicmaps.admin.editable"/></strong></td>
                <td>&nbsp;</td>
            </tr>
            <c:forEach var="topicMap" items="${topicMaps}" varStatus="status">
                <tr class="tableRow${status.index mod 2}">
                    <td>${topicMap.name}</td>
                    <td>
                        <c:choose>
                            <c:when test="${topicMap.editable}"><kantega:label key="aksess.text.ja"/></c:when>
                            <c:otherwise><kantega:label key="aksess.text.nei"/></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a href="EditTopicMap.action?id=${topicMap.id}" class="button edit"><kantega:label key="aksess.button.edit"/></a>
                        <a href="Javascript:deleteTopicMap(${topicMap.id}, '${topicMap.name}')" class="button delete"><kantega:label key="aksess.button.delete"/></a>
                        <a href="ListTopicTypes.action?topicMapId=${topicMap.id}"><kantega:label key="aksess.topicmaps.admin.topictypes"/></a>
                        <c:if test="${fn:length(topicMap.url) > 0}">
                            <a href="Javascript:importTopicMap(${topicMap.id}, '${topicMap.name}')"><kantega:label key="aksess.topicmaps.admin.importtopicmap"/></a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <div class="buttonGroup">
            <a href="EditTopicMap.action" class="button"><span class="add"><kantega:label key="aksess.topicmaps.admin.newmap"/></span></a>
        </div>

    </admin:box>

</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>