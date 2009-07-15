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
    <kantega:label key="aksess.displaytemplates.usages.title"/>
</kantega:section>

<kantega:section id="content">
    <div class="fieldset">
        <fieldset>
            <legend><kantega:label key="aksess.displaytemplates.usages.title"/></legend>
            <div class="helpText"><kantega:label key="aksess.displaytemplates.usages.help"/></div>
            <table border="0" cellspacing="0" cellpadding="0" width="600">
                <tr>
                    <th><kantega:label key="aksess.displaytemplates.usages.title"/></th>
                    <th><kantega:label key="aksess.displaytemplates.usages.lastmodified"/></th>
                </tr>
                <aksess:getcollection name="pages" contentquery="${query}" skipattributes="true" max="50" varStatus="status">
                    <tr class="tableRow<c:out value="${status.index mod 2}"/>">
                        <td><aksess:link collection="pages" target="_blank"><aksess:getattribute name="title" collection="pages"/></aksess:link></td>
                        <td><aksess:getattribute name="lastmodified" collection="pages"/></td>
                    </tr>
                </aksess:getcollection>
            </table>
        </fieldset>
    </div>

</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>
