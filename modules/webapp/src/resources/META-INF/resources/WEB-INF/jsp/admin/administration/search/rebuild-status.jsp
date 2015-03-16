<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
    <kantega:label key="aksess.search.rebuild.title"/>
</kantega:section>

<kantega:section id="content">
    <ul id="progress"></ul>

    <script type="text/javascript">
        var progressList = $("#progress");
        var previousHasReturned = false;
        function updateStatus() {
            $.get('<aksess:geturl url="/admin/administration/RebuildIndexStatus.action"/>', function (data) {
                var html = "";
                var status = data.status;
                for (var i = 0; i < status.length; i++) {
                    var provider = status[i];
                    html += "<li><kantega:label key="aksess.search.rebuild.indexing"/> ";
                    html += provider.type + " " + provider.current + "/" + provider.total;
                    html += "</li>";
                }
                progressList.html(html);
                if(!data.allDone){
                    setTimeout(updateStatus, 2000);
                } else {
                    progressList.before("<em><kantega:label key="aksess.search.rebuild.done"/></em>");
                }
            });
        }
        updateStatus();
    </script>
    <form action="StopIndex.action" name="stopIndex" method="POST">
        <admin:box>
            <div class="buttonGroup">
                <a href="#" onclick="document.stopIndex.submit()" class="button"><span class="ok"><kantega:label key="aksess.button.stop"/></span></a>
            </div>
        </admin:box>
    </form>
</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>
