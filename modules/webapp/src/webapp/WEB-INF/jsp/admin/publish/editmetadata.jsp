<%@ page import="no.kantega.publishing.common.data.enums.ExpireAction" %>
<%@ page import="no.kantega.publishing.admin.content.InputScreenRenderer" %>
<%@ page import="no.kantega.publishing.common.data.enums.AttributeDataType" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.spring.RootContext" %>
<%@ page import="no.kantega.publishing.forum.ForumProvider" %>
<%@ page import="no.kantega.publishing.common.data.attributes.OrgunitAttribute" %>
<%@ page import="no.kantega.publishing.common.data.attributes.UserAttribute" %>
<%@ page import="no.kantega.publishing.common.data.enums.Language" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
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
<kantega:section id="content">
<%
    InputScreenRenderer screen = new InputScreenRenderer(pageContext, (Content)session.getAttribute("currentContent"), AttributeDataType.META_DATA);
    Content current = (Content)session.getAttribute("currentContent");
%>
<script language="Javascript" type="text/javascript">
    var hasSubmitted = false;

    function initialize() {
    <%
        screen.generatePreJavascript();
    %>
        try {
            document.myform.elements[0].focus();
        } catch (e) {
            // Invisble field, can't get focus
        }
    }

    function saveContent(status) {
    <%
        screen.generatePostJavascript();
    %>
        if (validatePublishProperties()) {
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.status.value = status;
                document.myform.submit();
            }
        }
    }
</script>
<%@ include file="include/publishbuttons.jsp" %>
<form name="myform" action="SaveMetadata.action" method="post" enctype="multipart/form-data">
    <div id="ContainerPane">
        <div id="EditPane">
            <%@ include file="../../../../admin/include/infobox.jsf" %>
            
            <%
                if (Aksess.isTopicMapsEnabled()) {
            %>
            <div class="fieldset">
                <fieldset>
                    <legend><kantega:label key="aksess.editmetadata.topics"/></legend>
                    <div class="contentAttribute">
                        <div class="inputs">
                        <table border="0" cellspacing="0" cellpadding="0" width="100%">
                        <c:forEach var="topic" items="${currentContent.topics}" varStatus="status">
                            <tr class="tableRow${status.index mod 2}">
                                <td width="80%">${topic.baseName}</td>
                                <td width="20%" align="right">
                                    <a href="Javascript:removeTopic(${topic.topicMapId}, '${topic.id}')" class="button delete"><kantega:label key="aksess.button.fjern"/></a>
                                </td>
                            </tr>

                        </c:forEach>
                        </table>
                        <div align="right" style="margin-top:10px;">
                            <a href="Javascript:selectTopic(null)" class="button add"><kantega:label key="aksess.button.leggtilemner"/></a>
                        </div>
                        </div>
                    </div>
                </fieldset>
            </div>
            <%
                }
            %>
            <div class="fieldset">
                <fieldset>
                    <legend><kantega:label key="aksess.editmetadata.metadata"/></legend>

                    <c:if test="${fn:length(documentTypes) > 1}">
                    <div class="contentAttribute">
                        <div class="heading"><kantega:label key="aksess.editmetadata.doctype"/></div>
                        <div class="inputs">
                            <select name="documenttype" class="inputFullWidth" tabindex="10">
                                <option value="-1"><kantega:label key="aksess.list.ingen"/></option>
                                <c:forEach var="dt" items="${documentTypes}">
                                    <option value="${dt.id}" <c:if test="${dt.id == currentContent.documentTypeId}">selected</c:if>>${dt.name}</option>
                                </c:forEach>
                            </select>
                         </div>
                    </div>

                    <div class="contentAttribute">
                        <div class="heading"><kantega:label key="aksess.editmetadata.doctypeforchildren"/></div>
                        <div class="inputs">
                            <select name="documenttype" class="inputFullWidth" tabindex="10">
                                <option value="-1"><kantega:label key="aksess.list.ingen"/></option>
                                <c:forEach var="dt" items="${documentTypes}">
                                    <option value="${dt.id}" <c:if test="${dt.id == currentContent.documentTypeIdForChildren}">selected</c:if>>${dt.name}</option>
                                </c:forEach>
                            </select>
                         </div>
                    </div>
                    </c:if>

                    <div class="contentAttribute">
                        <div class="heading"><kantega:label key="aksess.editmetadata.alttitle"/></div>
                        <div class="inputs">
                            <input type="text" name="alttitle" size="72" class="inputFullWidth" value="<c:out value="${currentContent.altTitle}"/>" maxlength="255" tabindex="20">
                         </div>
                    </div>

                    <div class="contentAttribute">
                        <div class="heading"><kantega:label key="aksess.editmetadata.publisher"/></div>
                        <div class="inputs">
                            <input type="text" name="publisher" size="64" style="width:600px;" value="<c:out value="${currentContent.publisher}"/>" maxlength="64" tabindex="30">
                         </div>
                    </div>
                    <!-- Owner -->
                    <%
                        OrgunitAttribute orgunit = new OrgunitAttribute();

                        orgunit.setName(LocaleLabels.getLabel("aksess.editmetadata.owner", Aksess.getDefaultAdminLocale()));
                        orgunit.setValue(current.getOwner());
                        request.setAttribute("attribute", orgunit);
                        request.setAttribute("fieldName", "owner");
                        request.getRequestDispatcher("/admin/publish/attributes/" +orgunit.getRenderer() + ".jsp").include(request, response);
                    %>
                    <!-- OwnerPerson-->
                    <%
                        UserAttribute user = new UserAttribute();

                        user.setName(LocaleLabels.getLabel("aksess.editmetadata.ownerperson", Aksess.getDefaultAdminLocale()));
                        user.setValue(current.getOwnerPerson());
                        request.setAttribute("attribute", user);
                        request.setAttribute("fieldName", "ownerperson");
                        request.getRequestDispatcher("/admin/publish/attributes/" +user.getRenderer() + ".jsp").include(request, response);
                    %>
                    <div class="contentAttribute">
                        <div class="heading"><kantega:label key="aksess.editmetadata.keywords"/></div>
                        <div class="inputs">
                            <textarea name="keywords" cols="72" rows="5" style="width:600px;" wrap="soft" tabindex="50"><c:out value="${currentContent.keywords}"/></textarea>
                         </div>
                    </div>
                    <div class="contentAttribute">
                        <div class="heading"><kantega:label key="aksess.editmetadata.language"/></div>
                        <div class="inputs">
                            <select name="language" class="inputFullWidth" tabindex="60">
                            <%
                                int[] languages = Language.getLanguages();
                                for (int i = 0; i < languages.length; i++) {
                                    int id = languages[i];
                                    String code = Language.getLanguageAsISOCode(id);
                                    String label = LocaleLabels.getLabel("aksess.editmetadata.language." + code, Aksess.getDefaultAdminLocale());
                                    if (current.getLanguage() == id) {
                                        out.write("<option value=\"" + id + "\" selected>" + label + "</option>");
                                    } else {
                                        out.write("<option value=\"" + id + "\">" + label + "</option>");
                                    }
                                }
                            %>
                            </select>
                         </div>
                    </div>
                    <%
                        ForumProvider forumProvider = (ForumProvider) request.getAttribute("forumProvider");
                        if (forumProvider != null) {
                            boolean started = forumProvider.getThreadAboutContent(current) > 0;

                    %>
                    <div class="contentAttribute">
                        <div class="heading"><kantega:label key="aksess.editmetadata.forum"/></div>
                        <div class="inputs">
                            <% if(!started) { %>
                                <select name="forumid" style="width:600px;">
                                    <option value="-1" <%= current.getForumId() < 1 ? "selected" : ""%>><kantega:label key="aksess.editmetadata.forum.dontuse"/></option>
                                    <%=forumProvider.getForumsAsOptionList(current.getForumId())%>
                                </select>
                                <% } else  {%>
                                    <kantega:label key="aksess.editmetadata.forum.threadcreated"/>
                                <% } %>
                         </div>
                    </div>
                    <%
                        }
                    %>
                </fieldset>
            </div>

            <!-- Dynamic -->
            <%
                screen.generateInputScreen();
            %>

        </div>
        <%@ include file="include/publishproperties.jsp" %>
    </div>
    <input type="hidden" name="status" value="">
    <input type="hidden" name="action" value="">
    <input type="hidden" name="currentId" value="${currentContent.id}">
    <input type="hidden" name="isModified" value="${currentContent.modified}">
</form>
</kantega:section>
<%@ include file="../design/publish.jsp" %>