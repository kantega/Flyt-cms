<%@ page import="no.kantega.publishing.admin.content.InputScreenRenderer" %>
<%@ page import="no.kantega.publishing.api.content.Language" %>
<%@ page import="no.kantega.publishing.common.data.attributes.OrgunitAttribute" %>
<%@ page import="no.kantega.publishing.common.data.attributes.UserAttribute" %>
<%@ page import="no.kantega.publishing.common.data.enums.AttributeDataType" %>
<%@ page import="no.kantega.publishing.forum.ForumProvider" %>
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
<c:set var="editActive" value="true"/>
<c:set var="metadataActive" value="true"/>
<kantega:section id="title">
    <kantega:label key="aksess.edit.title"/>
</kantega:section>

<kantega:section id="content">
    <%
        InputScreenRenderer screen = new InputScreenRenderer(pageContext, (Content)session.getAttribute("currentContent"), AttributeDataType.META_DATA);
        Content current = (Content)session.getAttribute("currentContent");
    %>    
        <%@ include file="../layout/fragments/infobox.jsp" %>
        <c:if test="${fn:length(documentTypes) > 0}">
            <div class="contentAttribute">
                <div class="heading"><kantega:label key="aksess.contentproperty.doctype"/></div>
                <div class="inputs">
                    <select name="documenttype" class="fullWidth" tabindex="10">
                        <option value="-1"><kantega:label key="aksess.list.ingen"/></option>
                        <c:forEach var="dt" items="${documentTypes}">
                            <option value="${dt.id}" <c:if test="${dt.id == currentContent.documentTypeId}">selected</c:if>>${dt.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="contentAttribute">
                <div class="heading"><kantega:label key="aksess.contentproperty.doctypeforchildren"/></div>
                <div class="inputs">
                    <select name="documenttypeidforchildren" class="fullWidth" tabindex="10">
                        <option value="-1"><kantega:label key="aksess.list.ingen"/></option>
                        <c:forEach var="dt" items="${documentTypes}">
                            <option value="${dt.id}" <c:if test="${dt.id == currentContent.documentTypeIdForChildren}">selected</c:if>>${dt.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </c:if>

        <div class="contentAttribute">
            <div class="heading"><kantega:label key="aksess.contentproperty.alttitle"/></div>
            <div class="inputs">
                <input type="text" name="alttitle" size="72" class="fullWidth" value="${currentContent.altTitle}" maxlength="255" tabindex="20">
            </div>
        </div>

        <div class="contentAttribute">
            <div class="heading"><kantega:label key="aksess.contentproperty.publisher"/></div>
            <div class="inputs">
                <input type="text" name="publisher" size="64" class="fullWidth" value="${currentContent.publisher}" maxlength="64" tabindex="30">
            </div>
        </div>
        <!-- Owner -->
        <div class="contentAttribute">
            <div class="heading"><kantega:label key="aksess.contentproperty.owner"/></div>
            <%
                OrgunitAttribute orgunit = new OrgunitAttribute();

                orgunit.setName(LocaleLabels.getLabel("aksess.contentproperty.owner", Aksess.getDefaultAdminLocale()));
                orgunit.setValue(current.getOwner());
                request.setAttribute("attribute", orgunit);
                request.setAttribute("fieldName", "owner");
                pageContext.include("/admin/publish/attributes/" +orgunit.getRenderer() + ".jsp");
            %>
        </div>
        <!-- OwnerPerson-->
        <div class="contentAttribute">
            <div class="heading"><kantega:label key="aksess.contentproperty.ownerperson"/></div>
            <%
                UserAttribute user = new UserAttribute();

                user.setName(LocaleLabels.getLabel("aksess.contentproperty.ownerperson", Aksess.getDefaultAdminLocale()));
                user.setValue(current.getOwnerPerson());
                request.setAttribute("attribute", user);
                request.setAttribute("fieldName", "ownerperson");
                pageContext.include("/admin/publish/attributes/" +user.getRenderer() + ".jsp");
            %>
        </div>
        <div class="contentAttribute">
            <div class="heading"><kantega:label key="aksess.contentproperty.keywords"/></div>
            <div class="inputs">
                <textarea name="keywords" cols="72" rows="5"  class="fullWidth" wrap="soft" tabindex="50">${currentContent.keywords}</textarea>
            </div>
        </div>
        <div class="contentAttribute">
            <div class="heading"><kantega:label key="aksess.contentproperty.language"/></div>
            <div class="inputs">
                <select name="language" class="fullWidth" tabindex="60">
                    <%
                        int[] languages = Language.getLanguages();
                        for (int id : languages) {
                            String code = Language.getLanguageAsISOCode(id);
                            String label = LocaleLabels.getLabel("aksess.contentproperty.language." + code, Aksess.getDefaultAdminLocale());
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
            <div class="heading"><kantega:label key="aksess.contentproperty.forum"/></div>
            <div class="inputs">
                <% if(!started) { %>
                <select name="forumid" class="fullWidth">
                    <option value="-1" <%= current.getForumId() < 1 ? "selected" : ""%>><kantega:label key="aksess.contentproperty.forum.dontuse"/></option>
                    <%=forumProvider.getForumsAsOptionList(current.getForumId())%>
                </select>
                <% } else  {%>
                <kantega:label key="aksess.contentproperty.forum.threadcreated"/>
                <% } %>
            </div>
        </div>
        <%
            }
        %>
        <!-- Dynamic -->
        <%
            screen.generateInputScreen();
            if (screen.hasHiddenAttributes()) {
        %>
            <%@ include file="../layout/fragments/addattributebutton.jsp" %>
        <%
            }
        %>
</kantega:section>
<%@ include file="../layout/editContentLayout.jsp" %>