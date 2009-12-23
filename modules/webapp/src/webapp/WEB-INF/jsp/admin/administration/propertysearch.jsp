<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.data.UserContentChanges" %>
<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>
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
                <h1><kantega:label key="aksess.userchanges.username"/>:</strong> ${username}</h1>

                <div class="formElement">
                    <div class="heading"><kantega:label key="aksess.contentexpire.period"/></div>

                    <div class="inputs">
                        <table class="noborder">
                            <tr>
                                <td><label>Plassering</label></td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><label>Endret etter</label></td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><label>Dokument type</label></td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><label>Innholdsmal</label></td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><label>Ansvarlig person</label></td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><label>Ansvarlig enhet</label></td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><label>Sortering</label></td>
                                <td>
                                    <select>
                                        <option>Sist endret</option>
                                        <option value="<%=ContentProperty.TITLE%>">Tittel</option>
                                        <option value="<%=ContentProperty.LAST_MODIFIED%> desc">Sist endret - nyeste først</option>
                                        <option value="<%=ContentProperty.LAST_MODIFIED%>">Sist endret - eldst først</option>
                                        <option value="<%=ContentProperty.NUMBER_OF_VIEWS%> desc">Mest besøkt</option>
                                        <option value="<%=ContentProperty.NUMBER_OF_VIEWS%>">Minst besøkt</option>
                                    </select>
                                    <select>
                                        <option></option>
                                    </select>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>

                <div class="buttonGroup">
                    <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                </div>

                <div class="helpText"><kantega:label key="aksess.userchanges.documents.help"/></div>
            </fieldset>
        </div>
        <div class="fieldset">
            <fieldset>
                <h1><kantega:label key="aksess.userchanges.username"/>:</strong> ${username}</h1>
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
            </fieldset>
        </div>

    </form>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>