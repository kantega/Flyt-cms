<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess"%>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
~ limitations under the License.
--%>

<c:set var="linkCheckActive" value="true"/>
<kantega:section id="title">
    <kantega:label key="aksess.linkcheck.title" pageTitle="${currentNavigateContent.title}"/>
</kantega:section>

<kantega:section id="contentclass">linkCheck</kantega:section>

<kantega:section id="head extras">
    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { };
        }
        properties.contextPath = '${pageContext.request.contextPath}';
    </script>
    <script type="text/javascript" src="<kantega:expireurl url="/admin/js/linkcheck.js"/>"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            openaksess.common.debug("linkcheck.$(document).ready()");
            openaksess.linkcheck.currentUrl = "${currentNavigateContent.url}";
            openaksess.linkcheck.updateLinkList("title");
            $("#BrokenLinks th a").live('click', function(event) {
                event.preventDefault();
                var sort = $(this).attr("href");
                openaksess.common.debug("linkcheck.click sort: " + sort);
                openaksess.linkcheck.updateLinkList("title");
            });
        });
    </script>
</kantega:section>

<kantega:section id="content">
    <div id="MainPaneContent">
    <%-- The content is loaded with ajax by the ListBrokenLinks.action --%>
    </div>
</kantega:section>

<%@include file="../layout/contentNavigateLayout.jsp"%>
