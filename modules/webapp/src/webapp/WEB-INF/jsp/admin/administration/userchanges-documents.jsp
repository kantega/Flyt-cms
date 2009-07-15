<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.data.UserContentChanges" %>
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
    <kantega:label key="aksess.userchanges.title"/>
</kantega:section>

<kantega:section id="content">
    <form name="myform" action="" method="get">

        <div class="fieldset">
            <fieldset>
                <legend><kantega:label key="aksess.userchanges.username"/>:</strong> ${username}</legend>
                <table>
                    <tr>
                        <th><strong><kantega:label key="aksess.userchanges.documents.title"/></strong></th>
                        <th><strong><kantega:label key="aksess.userchanges.documents.changed"/></strong></th>
                    </tr>
                    <aksess:getcollection contentquery="${cq}" name="changes" skipattributes="true" orderby="lastmodified" max="200" descending="true" varStatus="status">
                        <tr class="tableRow${status.index mod 2}">
                            <td><a href="<aksess:geturl/>/admin/publish/Navigate.action?thisId=<aksess:getattribute name="id" collection="changes"/>" target="_top"><aksess:getattribute name="title" collection="changes"/></a></td>
                            <td><aksess:getmetadata name="lastmodified" collection="changes"/></td>
                        </tr>
                    </aksess:getcollection>

                </table>
                <div class="helpText"><kantega:label key="aksess.userchanges.documents.help"/></div>
            </fieldset>
        </div>
    </form>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>