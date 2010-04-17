<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.ContentIdentifier" %>
<%@ page import="no.kantega.publishing.common.service.lock.ContentLock" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
    <kantega:label key="aksess.locks.title"/>
</kantega:section>

<kantega:section id="content">


    <div class="fieldset">
        <fieldset>
            <h1><kantega:label key="aksess.locks.title"/></h1>

            <%
                Map locks = (Map)request.getAttribute("locks");
                if(locks.size() > 0) {
                    Iterator i  = locks.values().iterator();
                    DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());
                    DateFormat tf = new SimpleDateFormat("HH:mm:ss");
                    ContentManagementService cms = new ContentManagementService(request);
            %>
            <table class="fullWidth">
                <tr>
                    <th><kantega:label key="aksess.locks.page"/></th>
                    <th><kantega:label key="aksess.locks.owner"/></th>
                    <th><kantega:label key="aksess.locks.when"/></th>
                    <th>&nbsp;</th>
                </tr>
                <%
                    int count = 0;
                    while (i.hasNext()) {
                        ContentLock contentLock = (ContentLock) i.next();
                        ContentIdentifier cid = new ContentIdentifier();
                        cid.setContentId(contentLock.getContentId());
                        Content c = cms.getContent(cid);
                %>
                <tr class="tableRow<%=count++%2%>" >
                    <td>
                        <a href="<%=c.getUrl()%>" target="_new"><%=c.getTitle()%></a>
                    </td>
                    <td>
                        <%=contentLock.getOwner()%>
                    </td>
                    <td>
                        <%= df.format(contentLock.getCreateTime()) %> <%= tf.format(contentLock.getCreateTime())%>
                    </td>
                    <td>
                        <a href="RemoveContentLock.action?contentId=<%=contentLock.getContentId()%>" class="button delete"><span><kantega:label key="aksess.locks.remove"/></span></a>
                    </td>
                </tr>
                <%}%>
            </table>

            <div class="ui-state-highlight"><kantega:label key="aksess.locks.hjelp"/></div>


            <%
                } else {
            %>
            <div class="ui-state-highlight"><kantega:label key="aksess.locks.notfound"/></div>
            <%
                }
            %>

        </fieldset>

    </div>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>