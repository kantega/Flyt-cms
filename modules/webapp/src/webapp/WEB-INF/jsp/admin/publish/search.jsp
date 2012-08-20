<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/search" prefix="search" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
<kantega:section id="body">
<div id="SearchDrilldown">
    <ul>
    <c:forEach items="${searchResponse.facets}" var="facet">
        <li>
           <h3><kantega:label key="aksess.search.${facet.key}"/></h3>
            <ul>
           <c:forEach items="${facet.value}" var="facetEntry">
                <c:set var="facetLabel" value="${facet.key}.${facetEntry.value}"/>
                <li><a href="${facetEntry.url}"><search:labelresolver key="${facetLabel}" /> (${facetEntry.count})</a></li>
           </c:forEach>
            </ul>
        </li>
    </c:forEach>
    </ul>
</div>

<div id="SearchResult">
    <c:choose>
        <c:when test="${searchResponse.numberOfHits eq 0}">
            <div class="hitCount">
                <kantega:label key="aksess.search.nohits"/>
            </div>
        </c:when>
        <c:otherwise>
            <div class="hitCount">
                <kantega:label key="aksess.search.numberofhits"/>: ${searchResponse.numberOfHits}
            </div>
        </c:otherwise>
    </c:choose>


    <c:forEach items="${searchResponse.documentHits}" var="searchHit">
        <div class="hit">
            <a href="${searchHit.url}" target="contentmain"><c:out value="${searchHit.title}" escapeXml="false"/></a><br>
            <c:out value="${searchHit.description}" escapeXml="false"/>
        </div>
    </c:forEach>

    <c:if test="${searchResponse.numberOfHits gt 10}">
        <div class="paging">
            <c:if test="${not empty links.prevPageUrl}">
                <a class="previous" href="<c:out value="${links.prevPageUrl}"/>"><kantega:label key="aksess.search.results.previous"/></a>
            </c:if>

            <div class="pagenumbers">
                <c:forEach items="${pageUrls}" varStatus="status">
                    <c:choose>
                        <c:when test="${status.current.key == searchResponse.currentPage + 1}">
                            <a class="currentpage" href="<c:out value="${status.current.value}"/>"><b><c:out value="${status.current.key}"/></b></a>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:out value="${status.current.value}"/>"><c:out value="${status.current.key}"/></a>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${!status.last}">
                        <span class="seperator">|</span>
                    </c:if>
                </c:forEach>
            </div>
            <c:if test="${not empty nextPageUrl}">
                <a class="next" href="<c:out value="${nextPageUrl}"/>"><kantega:label key="aksess.search.results.next"/></a>
            </c:if>
        </div>
    </c:if>
</div>
</kantega:section>
<%@ include file="../layout/searchLayout.jsp" %>