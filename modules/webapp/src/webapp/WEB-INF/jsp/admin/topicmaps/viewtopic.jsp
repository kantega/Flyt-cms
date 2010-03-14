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

        function bindTopicInfoEvents() {
            debug("ViewTopic.action bindTopicInfoEvents()");

            $("#TopicAssociationTabs a.delete").click(function(event) {
                event.preventDefault();
                var container = $(this).closest(".ui-tabs-panel");
                debug("ViewTopics.action: load new content:" + this.href);
                if (confirm("<kantega:label key="aksess.viewtopic.deleteassociation"/>")) {
                    container.load(this.href, function() {
                        bindTopicInfoEvents();
                    });
                }
            });

            // Add roles
            $("#AssociatedRoles input.add").click(function(event) {
                var container = $(this).closest(".ui-tabs-panel");
                var roleId = $("#AddRole").val();
                debug("ViewTopics.action: add role:" + roleId);
                container.load("ListAssociatedRoles.action?topicId=${topic.id}&topicMapId=${topic.topicMapId}&addId=" + roleId, function() {
                    bindTopicInfoEvents();
                });
            });

            // Add content
            $("#AddExistingTopic input").autocomplete("${pageContext.request.contextPath}/ajax/AutocompleteTopics.action?topicMapId=${topic.topicMapId}").result(function(event, data, formatted) {
                var ids = data[1].split(":");
                var topicMapId = ids[0];
                var topicId = ids[1];
                var container = $(this).closest(".ui-tabs-panel");
                debug("ViewTopic.action add topic: " + topicId);
                container.load("ListAssociatedTopics.action?topicId=${topic.id}&topicMapId=${topic.topicMapId}&addId=" + topicId, function() {
                    bindTopicInfoEvents();
                });
            });

            // Add content
            $("#AddContent").autocomplete("${pageContext.request.contextPath}/ajax/AutocompleteContent.action?useContentId=true").result(function(event, data, formatted) {
                var contentId = data[1];
                var container = $(this).closest(".ui-tabs-panel");
                debug("ViewTopic.action add content: " + contentId);
                container.load("ListAssociatedContent.action?topicId=${topic.id}&topicMapId=${topic.topicMapId}&addId=" + contentId, function() {
                    bindTopicInfoEvents();
                });
            });
        }
        
        $(document).ready(function() {
            $("#TopicAssociationTabs").tabs({
                load: function(event, ui) {
                    bindTopicInfoEvents();
                }
            });
            $("")
        });
    </script>
</kantega:section>

<kantega:section id="body">
    <div class="fieldset">
        <fieldset>
            <h1><c:out value="${topic.baseName}"/> <c:if test="${instanceOf != null}"><span class="instanceof">(<c:out value="${instanceOf.baseName}"/>)</span></c:if></h1>

            <div class="buttonGroup">
                <span class="button"><input type="button" class="delete" value="<kantega:label key="aksess.button.delete"/>"></span>
                <span class="button"><input type="button" class="delete" value="<kantega:label key="aksess.button.delete"/>"></span>
            </div>

            <div id="TopicOccurences">
                <c:forEach var="occurence" items="${topic.occurences}">
                    <c:if test="${occurence.resourceData != ''}">
                        <div class="topicOccurence">
                            <c:if test="${occurence.instanceOf != null}">
                                <div class="baseName">${occurence.instanceOf.baseName}</div>
                            </c:if>
                            <div class="resourceData">${occurence.resourceData}</div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>

            <div id="TopicAssociationTabs">
                <ul>
                    <li><a href="ListAssociatedTopics.action?topicMapId=${topic.topicMapId}&amp;topicId=${topic.id}"><kantega:label key="aksess.viewtopic.associatedtopics"/></a></li>
                    <li><a href="ListAssociatedContent.action?topicMapId=${topic.topicMapId}&amp;topicId=${topic.id}"><kantega:label key="aksess.viewtopic.associatedcontent"/></a></li>
                    <li><a href="ListAssociatedRoles.action?topicMapId=${topic.topicMapId}&amp;topicId=${topic.id}"><kantega:label key="aksess.viewtopic.associatedroles"/></a></li>
                </ul>
            </div>
        </fieldset>
    </div>
</kantega:section>
<%@ include file="../layout/popupLayout.jsp" %>