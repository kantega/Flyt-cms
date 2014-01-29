<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
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
    <kantega:label key="aksess.aliases.title"/>
</kantega:section>

<kantega:section id="content">
    <admin:box>
        <h1><kantega:label key="aksess.aliases.title"/></h1>
        <table class="fullWidth dataTable">
            <thead>
            <tr>
                <th>
                    <kantega:label key="aksess.aliases.alias"/>
                    <img src="<aksess:geturl url="/admin/bitmaps/common/icons/small/updown.png"/>">
                </th>
                <th>
                    <kantega:label key="aksess.aliases.page"/>
                    <img src="<aksess:geturl url="/admin/bitmaps/common/icons/small/updown.png"/>">
                </th>
            </tr>
            </thead>
            <tbody>
            <aksess:getcollection name="aliases" contentquery="${query}" skipattributes="true" varStatus="status" var="alias">
                <tr class="tableRow${status.index mod 2}">
                    <td><aksess:getattribute name="alias" collection="aliases"/></td>
                    <td>
                        <div style="height: 0; width: 0; overflow: hidden;">
                            <%=pageContext.getAttribute("alias") != null ? ((Content)pageContext.getAttribute("alias")).getTitle().trim() : ""%>
                        </div>
                        <aksess:link collection="aliases" target="_new"><aksess:getattribute name="title" collection="aliases"/></aksess:link>
                    </td>
                </tr>
            </aksess:getcollection>
            </tbody>
        </table>

        <div class="ui-state-highlight"><kantega:label key="aksess.aliases.help"/></div>
    </admin:box>
</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>
