<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  ~ Copyright 2011 Kantega AS
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
        function deleteTopicType(topicmapid, id, name) {
            if (confirm("<kantega:label key="aksess.topicmaps.admin.confirmdelete"/> " + name + "?")) {
                window.location.href = "DeleteTopicType.action?topicMapId=" + topicmapid + "&topicId=" + escape(id);
            }
        }
    </script>
    <admin:box>
        <h1><kantega:label key="aksess.topicmaps.admin.topictypes"/></h1>
        <table>
            <tr class="tableHeading">
                <td><strong><kantega:label key="aksess.topicmaps.admin.topictype"/></strong></td>
                <td>&nbsp;</td>
            </tr>
            <c:forEach var="topic" items="${topics}" varStatus="status">
                <tr class="tableRow${status.index mod 2}">
                    <td>${topic.baseName}</td>
                    <td>
                        <a href="EditTopicType.action?topicMapId=${topic.topicMapId}&topicId=${topic.id}" class="button edit"><kantega:label key="aksess.button.edit"/></a>
                        <a href="Javascript:deleteTopicType(${topic.topicMapId}, '${topic.id}', '${topic.baseName}')" class="button delete"><kantega:label key="aksess.button.delete"/>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <div class="buttonGroup">
            <a href="EditTopicType.action?topicMapId=${topicMapId}" class="button"><span class="add"><kantega:label key="aksess.topicmaps.admin.topictypes.newtopic"/></span></a>
        </div>

    </admin:box>

</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>