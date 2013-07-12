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
   
    <admin:box>
        <h1><kantega:label key="aksess.topicmaps.title"/></h1>
        <div>
            <c:choose>
                <c:when test="${importedTopicMap != null}">
                    <div>Importert topicmap:</div>
                    <div>Antall emner: ${fn:length(importedTopicMap.topicList)}</div>
                    <div>Antall assiosasisjoner: ${fn:length(importedTopicMap.topicAssociationList)}</div>
                </c:when>
                <c:when test="${errormessage != null}">
                    <div>Error importing topicmaps:</div>
                    <div>${errormessage}</div>
                </c:when>
            </c:choose>
        </div>
        <div class="buttonGroup">
            <a href="SaveImportedTopicMap.action" class="button"><span class="add"><kantega:label key="aksess.topicmaps.admin.saveimportedmap"/></span></a>
            <span class="button"><input type="button" class="cancel" onclick="window.location.href='ListTopicMaps.action'" value="<kantega:label key="aksess.button.cancel"/>"></span>
        </div>

    </admin:box>

</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>