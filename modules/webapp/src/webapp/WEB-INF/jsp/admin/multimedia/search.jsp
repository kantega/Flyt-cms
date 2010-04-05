<%@ page import="no.kantega.publishing.common.data.Multimedia" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
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
    <script type="text/javascript">
        $(document).ready(function () {
            $("#SearchResult img.thumbnail").lazyload({
                placeholder : "../bitmaps/blank.gif",
                container: $("#SearchResult")
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
            <c:forEach items="${hits}" var="media">
                <div class="media" id="Media${media.id}">
                    <div class="icon">
                        <a href="EditMultimedia.action?id=${media.id}" target="_top">
                            <%
                                Multimedia mm = (Multimedia)pageContext.getAttribute("media");
                                String mimeType = mm.getMimeType().getType();
                                mimeType = mimeType.replace('/', '-');
                                mimeType = mimeType.replace('.', '-');
                                if (mimeType.indexOf("image") != -1) {
                                    out.write("<img class=\"thumbnail\" src=\"../bitmaps/blank.gif\" original=\"../../multimedia.ap?id=" + mm.getId() + "&amp;width=100&amp;height=100\">");
                                } else {
                                    out.write("<div class=\"file " + mimeType + "\"></div>");
                                }
                            %>
                        </a>
                    </div>
                    <div class="mediaInfo">
                        <div class="name">${media.name}</div>
                        <div class="details">
                            <c:choose>
                                <c:when test="${media.height > 0 &&media.width > 0}">
                                    <kantega:label key="aksess.multimedia.size"/> ${media.width}x${media.height}<br>
                                </c:when>
                                <c:otherwise>
                                    ${media.fileType}<br>
                                </c:otherwise>
                            </c:choose>
                            <kantega:label key="aksess.multimedia.lastmodified"/> <admin:formatdate date="${media.lastModified}"/>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</kantega:section>
<%@ include file="../layout/searchLayout.jsp" %>
