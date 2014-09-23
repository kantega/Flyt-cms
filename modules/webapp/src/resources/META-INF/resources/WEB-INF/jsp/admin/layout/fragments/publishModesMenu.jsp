<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%--
~ Copyright 2009 Kantega AS
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~  http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License
--%>

<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>

<div class="buttonGroup">
    <a href="${pageContext.request.contextPath}/admin/publish/Navigate.action" class="button first <c:if test="${navigateActive}"> active</c:if>"><span class="view"><kantega:label key="aksess.mode.view"/></span></a>
    <span class="buttonSeparator"></span>
    <a href="#" class="button <c:if test="${editActive}"> active</c:if>"><span class="edit"><kantega:label key="aksess.mode.edit"/></span></a>
    <span class="buttonSeparator"></span>
    <a href="${pageContext.request.contextPath}/admin/publish/Organize.action" class="button last <c:if test="${organizeActive}"> active</c:if>"><span class="organize"><kantega:label key="aksess.mode.organize"/></span></a>
</div>
<div class="buttonGroup">
    <a href="${pageContext.request.contextPath}/admin/publish/LinkCheck.action" class="button first <c:if test="${linkCheckActive}"> active</c:if>"><span class="linkcheck"><kantega:label key="aksess.mode.linkcheck"/></span></a>
    <span class="buttonSeparator"></span>
    <a href="${pageContext.request.contextPath}/admin/publish/Statistics.action" class="button <c:if test="${statisticsActive}"> active</c:if>"><span class="statistics"><kantega:label key="aksess.mode.statistics"/></span></a>
    <span class="buttonSeparator"></span>
    <a href="${pageContext.request.contextPath}/admin/publish/Notes.action" class="button last <c:if test="${notesActive}"> active</c:if>"><span class="notes"><kantega:label key="aksess.mode.notes"/><span id="NotesCount"></span></span></a>    
</div>
<c:if test="${!hideSearch}">
<div class="buttonGroup search">
    <form action="" method="get" id="SearchForm">
        <input type="text" id="SearchQuery" class="query content" name="query content">
        <input type="submit" id="SearchButton" value="" title="<kantega:label key="aksess.search.submit"/>">
    </form>
</div>
</c:if>

<script>
    var url = "<aksess:geturl url="/admin/publish/CountBrokenLinks.action"/>";
    var linkCheckerBtn = $('.linkcheck');
    jQuery.getJSON(url, function (data) {
        linkCheckerBtn.text(linkCheckerBtn.text() + " (" + data + ")");
    });
</script>
