<%@ page import="no.kantega.publishing.common.data.Multimedia" %>
<%@ page import="no.kantega.publishing.common.util.MultimediaHelper" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
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
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>

<h1>${currentFolder.name}</h1>


<c:forEach items="${mediaList}" var="media">
    <c:choose>
        <c:when test="${media.type eq 'FOLDER'}">
            <div class="folder" id="Media${media.id}">
                <div class="icon"></div>
                <div class="mediaInfo">
                    <div class="name">${media.name}</div>
                    <div class="details">0 objekter</div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="media">
                <div class="icon">
                    <a href="ViewMultimedia.action?id=${media.id}">
                        <%
                            Multimedia mm = (Multimedia)pageContext.getAttribute("media");
                            String mimeType = mm.getMimeType().getType();
                            mimeType = mimeType.replace('/', '-');
                            mimeType = mimeType.replace('.', '-');
                            if (mimeType.indexOf("image") != -1) {
                                out.write(MultimediaHelper.mm2HtmlTag(mm, 100, 100));
                            } else {
                                out.write("<div class=\"media " + mimeType + "\"></div>");
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
        </c:otherwise>
    </c:choose>
</c:forEach>