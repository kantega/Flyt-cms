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
    <c:set var="nohits" value="0"/>
    <aksess:ifcollectionnotempty contentquery="${cq}" name="changes" skipattributes="true" orderby="${sort}" descending="${descending}">
    <table class="fullWidth sortable">
        <thead>
        <tr>
            <th><strong><kantega:label key="aksess.contentproperty.title"/></strong></th>
            <th><strong><kantega:label key="aksess.contentproperty.lastmodified"/></strong></th>
            <th class="number"><strong><kantega:label key="aksess.contentproperty.numberofviews"/></strong></th>
        </tr>
        </thead>
        <tbody>
        <aksess:getcollection  name="changes" varStatus="status">
            <tr class="tableRow${status.index mod 2}">
                <td><a href="<aksess:geturl/>/admin/publish/Navigate.action?thisId=<aksess:getattribute name="id" collection="changes"/>" target="_top"><aksess:getattribute name="title" collection="changes"/></a></td>
                <td><aksess:getmetadata name="lastmodified" collection="changes"/></td>
                <td class="number"><aksess:getattribute name="numberofviews" collection="changes"/></td>
            </tr>
            <c:set var="nohits" value="${status.index}"/>
        </aksess:getcollection>
        </tbody>
    </table>
    </aksess:ifcollectionnotempty>
    <c:choose>
        <c:when test="${nohits == 0}">
            <div class="ui-state-highlight"><kantega:label key="aksess.search.nohits"/></div>
        </c:when>
        <c:when test="${nohits > maxRecords}">
            <div class="ui-state-highlight"><kantega:label key="aksess.propertysearch.maxrecords"/></div>
        </c:when>
    </c:choose>