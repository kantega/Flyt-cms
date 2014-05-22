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
    <kantega:label key="aksess.topics.title"/>
</kantega:section>

<kantega:section id="contentclass">topicmaps</kantega:section>

<kantega:section id="head extras">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.columnizer.js"></script>
    <script type="text/javascript">        
        function addTopic() {
            var selected = $("#TopicTabs").tabs('option', 'selected');
            var container = $("#TopicTabs .ui-tabs-panel").eq(selected);
            var topicMapId = $(".topicMapId", container).val();

            openaksess.common.modalWindow.open({title:'<kantega:label key="aksess.viewtopic.title" escapeJavascript="true"/>', iframe:true, href: "EditTopic.action?topicMapId=" + topicMapId, width: 600, height:550, close: function(){
                    // Reload content with ajax
                var $TopicTabs = $("#TopicTabs");
                var selected = $TopicTabs.tabs('option', 'selected');
                    $TopicTabs.tabs('load', selected);
                }
            });
        }

        $(document).ready(function() {
            $("#AddTopicButton").click(function() {
                addTopic();
            });

            $("#TopicTabs a.topic").live('click', function(event) {
                event.preventDefault();
                openaksess.common.modalWindow.open({title:'<kantega:label key="aksess.viewtopic.title" escapeJavascript="true"/>', iframe:true, href: this.href, width: 600, height:600, close: function(){
                        // Reload content with ajax
                    var $TopicTabs = $("#TopicTabs");
                    var selected = $TopicTabs.tabs('option', 'selected');
                        $TopicTabs.tabs('load', selected);
                    }
                });
            });

            $("#TopicTabs").tabs({
                load: function (event, ui) {
                    openaksess.common.debug("Topics.action TopicTabs.load:" + ui.panel.id);
                    openaksess.common.columnize();
                    var parent = $(ui.panel);
                    $(".topicQuery", parent).keyup(function(event) {
                        var q = $(".topicQuery", $(ui.panel)).val().toUpperCase();
                        openaksess.common.debug("Topics.action TopicQuery: " + q);
                        if (q != "") {
                            $(".topicList li.letter", parent).each(function() {
                                var hasElements = false;
                                $("li", this).each(function() {
                                    var txt = $("a", this).html().toUpperCase();
                                    if (txt.indexOf(q) != -1) {
                                        hasElements = true;
                                        $(this).show();
                                    } else {
                                        $(this).hide();
                                    }

                                });
                                if (hasElements) {
                                    $(this).show();
                                } else {
                                    $(this).hide();
                                }
                            });
                        } else {
                            $(".topicList li.letter", parent).show();
                            $(".topicList li.letter li", parent).show();
                        }
                    });

                }
            });
        });
    </script>
</kantega:section>

<kantega:section id="modesMenu">
</kantega:section>

<kantega:section id="toolsMenu">
    <div class="buttonGroup">
        <a href="#" class="button" id="AddTopicButton"><span class="add"><kantega:label key="aksess.tools.newtopic"/></span></a>
    </div>
</kantega:section>

<kantega:section id="content">
    <%-- The content is loaded with ajax by the SearchTopicsAction --%>
    <div id="TopicTabs">
        <ul>
            <c:forEach var="topicMap" items="${topicMaps}" varStatus="status">
                <li><a href="SearchTopics.action?topicMapId=${topicMap.id}">${topicMap.name}</a></li>
            </c:forEach>
        </ul>
    </div>
</kantega:section>
<%@ include file="../layout/fullwidthLayout.jsp" %>