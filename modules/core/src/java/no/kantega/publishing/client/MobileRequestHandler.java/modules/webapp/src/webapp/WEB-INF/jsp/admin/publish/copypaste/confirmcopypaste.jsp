<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.data.Association" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.PathEntry" %>
<%@ page import="no.kantega.publishing.common.data.enums.AssociationType" %>
<%@ page import="no.kantega.publishing.common.data.AssociationCategory" %>
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

</kantega:section>

<kantega:section id="head">
    <script type="text/javascript">
        var hasSubmitted = false;
        function buttonOkPressed() {
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.submit();
            }
        }
    </script>
</kantega:section>
<kantega:section id="body">

    <form name="myform" method="post" action="CopyPasteContent.action">
        <input type="hidden" name="isCopy" value="<c:out value="${isCopy}"/>">
        <input type="hidden" name="pasteShortCut" value="<c:out value="${pasteShortCut}"/>">
        <input type="hidden" name="uniqueId" value="<c:out value="${uniqueId}"/>">
        <input type="hidden" name="newParentId" value="<c:out value="${newParentId}"/>">

        <c:choose>
            <c:when test="${isCopy && !pasteShortCut}">
                <c:if test="${allowDuplicate && allowCrossPublish}">
                    <p>
                        <strong><kantega:label key="aksess.copypaste.copy.choose"/></strong>
                    </p>
                </c:if>
                <table>
                    <c:if test="${allowDuplicate}">
                        <tr valign="top">
                            <c:choose>
                                <c:when test="${allowCrossPublish}">
                                    <td><input type="radio" name="isTextCopy" value="true" checked="checked"></td>
                                </c:when>
                                <c:otherwise>
                                    <input type="hidden" name="isTextCopy" value="true">
                                </c:otherwise>
                            </c:choose>
                            <td>
                                <strong><kantega:label key="aksess.copypaste.copy.textcopy"/> <i><c:out value="${selectedContentTitle}"/></i> <kantega:label key="aksess.copypaste.under"/> <i><c:out value="${parentTitle}"/></i></strong><br>
                                <kantega:label key="aksess.copypaste.copy.textcopy2"/>
                            </td>
                        </tr>
                    </c:if>
                    <c:if test="${allowCrossPublish}">
                        <tr valign="top">
                            <c:choose>
                                <c:when test="${allowDuplicate}">
                                    <td><input type="radio" name="isTextCopy" value="false"></td>
                                </c:when>
                                <c:otherwise>
                                    <input type="hidden" name="isTextCopy" value="false">
                                </c:otherwise>
                            </c:choose>
                            <td>
                                <strong><kantega:label key="aksess.copypaste.copy.treecopy"/> <i><c:out value="${selectedContentTitle}"/></i> <kantega:label key="aksess.copypaste.copy.treecopy2"/> <i><c:out value="${parentTitle}"/></i></strong><br>
                                <kantega:label key="aksess.copypaste.copy.treecopy3"/>
                            </td>
                        </tr>
                    </c:if>
                </table>
            </c:when>

            <c:when test="${isCopy && pasteShortCut}">
                <p><kantega:label key="aksess.copypaste.copyshortcut"/> <i><c:out value="${selectedContentTitle}"/></i> <kantega:label key="aksess.copypaste.fra"/> <i><c:out value="${parentTitle}"/></i> ? </p>
            </c:when>

            <c:otherwise>
                <p><kantega:label key="aksess.copypaste.move"/> <i><c:out value="${selectedContentTitle}"/></i> <kantega:label key="aksess.copypaste.under"/> <i><c:out value="${parentTitle}"/></i> ? </p>
            </c:otherwise>

        </c:choose>
        <%
            List allowedAssociations = (List)request.getAttribute("allowedAssociations");
            if (allowedAssociations.size() == 1) {
                AssociationCategory tmp = (AssociationCategory)allowedAssociations.get(0);
                out.write("<input type=\"hidden\" name=\"associationCategory\" value=\"" + tmp.getId() + "\">");
            } else {
        %>
        <strong><kantega:label key="aksess.copypaste.kategori"/>:</strong>&nbsp;
        <select name="associationCategory">
            <%
                Content selectedContent = (Content)request.getAttribute("selectedContent");
                int prevAssociationCategory = selectedContent.getAssociation().getCategory().getId();
                for (int i = 0; i < allowedAssociations.size(); i++) {
                    AssociationCategory tmp = (AssociationCategory)allowedAssociations.get(i);
                    out.write("<option value=\"" + tmp.getId() + "\"");
                    if (tmp.getId() == prevAssociationCategory) {
                        out.write(" checked");
                    }
                    out.write(">" + tmp.getName() + "</option>");
                }
            %>
        </select>
        <%
            }
        %>
        <div class="buttonGroup">
            <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
            <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
        </div>
    </form>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>