<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>

<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<admin:box>
    <h1>
        <c:choose>
            <c:when test="${thisPageOnly}">
                <kantega:label key="aksess.linkcheck.titlesinglepage" pageTitle="${currentNavigateContent.title}"/>
            </c:when>
            <c:otherwise>
                <kantega:label key="aksess.linkcheck.title" pageTitle="${currentNavigateContent.title}"/>
            </c:otherwise>
        </c:choose>
    </h1>
    <c:if test='${lastChecked != ""}'>
        <p><kantega:label key="aksess.linkcheck.lastChecked"/>: ${lastChecked}</p>
    </c:if>
    <input id="updatePageBtn" type="button" class="ui-button" value="<kantega:label key="aksess.linkcheck.updateList"/>">

    <input id="thisPageBtn" type="button" class="ui-button" value="<kantega:label key="aksess.linkcheck.onlyThisPage"/>">

    <div id="statusFilter" class="ui-state-highlight">
        <c:forEach var="linkStatus" items="${checkStatuses}">
            <label>
                <input type="checkbox" id="${linkStatus}" value="${linkStatus}" class="activeStatuses" checked>
                <%@include file="fragments/linkcheckStatus.jsp"%>
            </label>
        </c:forEach>
    </div>
    <c:choose>
        <c:when test="${not empty brokenLinks}">
            <table class="fullWidth">
                <thead>
                <tr>
                    <th><a href="title"><kantega:label key="aksess.linkcheck.page"/></a></th>
                    <th><a href="title"><kantega:label key="aksess.linkcheck.field"/></a></th>
                    <th><a href="url"><kantega:label key="aksess.linkcheck.url"/></a></th>
                    <th><a href="status"><kantega:label key="aksess.linkcheck.status"/></a></th>
                    <th><a href="lastchecked"><kantega:label key="aksess.linkcheck.lastchecked"/></a></th>
                    <th><a href="timeschecked"><kantega:label key="aksess.linkcheck.timeschecked"/></a></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="link" items="${brokenLinks}" varStatus="status">
                    <tr class="tableRow${status.index mod 2} brokenlink" valign="top" data-status="${link.status}">
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/publish/Navigate.action?contentId=${link.contentId}" target="_top">${link.contentTitle}</a>
                        </td>
                        <td>
                            <c:if test="${link.attributeName != null}">
                                ${link.attributeName}
                            </c:if>
                        </td>
                        <c:set var="url" value="${link.url}"/>
                        <%
                            String url = (String) pageContext.getAttribute("url");
                            if (url.startsWith(Aksess.VAR_WEB)) {
                                url = Aksess.getContextPath() + url.substring(Aksess.VAR_WEB.length());
                            }
                        %>
                        <td><a target="external" href="<%=url%>">

                            <%= url.length() > 40 ? url.substring(0, 40) + "..." : url%>
                        </a></td>
                        <td>
                            <c:set var="linkStatus" value="${link.status}"/>
                            <%@include file="fragments/linkcheckStatus.jsp"%>
                        </td>
                        <td>
                            <c:if test="${link.lastChecked != null}">
                                <admin:formatdate date="${link.lastChecked}"/>
                            </c:if>
                        </td>
                        <td>${link.timesChecked}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <div class="ui-state-highlight">
                <kantega:label key="aksess.linkcheck.help"/>
            </div>

        </c:when>
        <c:otherwise>
            <div class="ui-state-highlight">
                <c:choose>
                    <c:when test="${thisPageOnly}">
                        <kantega:label key="aksess.linkcheck.nobrokenlinkssinglepage"/>
                    </c:when>
                    <c:otherwise>
                        <kantega:label key="aksess.linkcheck.nobrokenlinks"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:otherwise>
    </c:choose>
    <script>
        $(document).ready(function () {
            var thisPageBtn = $('#thisPageBtn');
            thisPageBtn.click(function (event) {
                openaksess.linkcheck.currentUrl = "${currentNavigateContent.url}";
                openaksess.linkcheck.updateLinkList("title", {thisPageOnly: true});
            });

            var updatePageBtn = $('#updatePageBtn');
            updatePageBtn.click(function (event) {
                openaksess.linkcheck.currentUrl = "${currentNavigateContent.url}";
                openaksess.linkcheck.updateLinkList("title", {thisPageOnly: false});
            });

            var filterState = {};
            var $activeStatuses = $('.activeStatuses');
            $activeStatuses.each(function(i, element){
                var value = element.value;
                if(localStorage){
                    var typeIsChecked = localStorage.getItem(value) || "true";
                    element.checked = typeIsChecked === "true";
                }
                filterState[value] = element.checked;
            });

            updaterows();

            function updaterows(){
                $('.brokenlink').each(function(i, element){
                    var $element = $(element);
                    var attr = $element.attr('data-status');
                    if(filterState[attr]){
                        $element.show();
                    } else {
                        $element.hide();
                    }
                })
            }

            $activeStatuses.change(function(event){
                var $element = $(this);
                var value = $element.val();
                var isActive = $element.is(':checked');
                filterState[value] = isActive;

                if(localStorage){
                    localStorage.setItem(value, isActive);
                }
                updaterows();
            })

        });
    </script>
</admin:box>
