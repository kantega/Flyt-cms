<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/css/multimedia.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/mimetypes.css">
    <script type="text/javascript">
        $(document).ready(function () {
            $("#SearchResult img.thumbnail").lazyload({
                placeholder : "../bitmaps/blank.gif",
                container: $("#SearchResult")
            });

            $("a.showfolder").click(function(event) {
                event.preventDefault();
                var id = $(event.target).attr("href");
                window.parent.openaksess.multimedia.triggerMultimediaupdateEvent(id);
            });
        });
    </script>
</kantega:section>

<kantega:section id="body">

    <div id="SearchResult">
        <c:choose>
            <c:when test="${numberOfHits eq 0}">
                <div class="hitCount">
                    <kantega:label key="aksess.search.nohits"/>
                </div>
            </c:when>
            <c:otherwise>
                <div class="hitCount">
                    <kantega:label key="aksess.search.numberofhits"/>: ${numberOfHits}
                </div>
            </c:otherwise>
        </c:choose>

        <div id="MultimediaFolders">
            <c:set var="mediaSearch" value="true"/>
            <%@ include file="include/medialist.jspf" %>
        </div>
    </div>
</kantega:section>
<%@ include file="../layout/searchLayout.jsp" %>
