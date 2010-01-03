<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.data.Association" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.PathEntry" %>
<%@ page import="no.kantega.publishing.common.data.enums.AssociationType" %>
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
    <script type="text/javascript" language="Javascript">
        var hasSubmitted = false;

        function buttonOkPressed() {
            // Prevent user from clicking several times
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.submit();
            }

            return false;
        }

        function selectAll(btn) {
            var f = document.myform.associationId;
            if (f.length) {
                for (var i = 0; i < f.length; i++) {
                    f[i].checked = btn.checked;
                }
            } else {
                f.checked = btn.checked;
            }
        }
    </script>

</kantega:section>

<kantega:section id="body">
    <form name="myform" method="post" action="DeleteAssociation.action">
        <div class="fieldset">
            <fieldset>
                <c:choose>
                    <c:when test="${isCrossPublished}">
                        <kantega:label key="aksess.confirmdelete.xptext" contentTitle="${contentTitle}"/>
                        <table class="fullWidth">
                            <thead>
                            <tr class="tableHeading">
                                <th colspan="2"><kantega:label key="aksess.confirmdelete.xpinstance"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="tableRow1">
                                <td><input type="checkbox" name="dummy" value="-1" onclick="selectAll(this)"></td>
                                <td><kantega:label key="aksess.confirmdelete.xpall"/></td>
                            </tr>
                            <%
                                Content content = (Content)request.getAttribute("content");
                                Integer associationId = (Integer)request.getAttribute("associationId");
                                ContentManagementService aksessService = new ContentManagementService(request);
                                List associations = content.getAssociations();
                                if (associations != null) {
                                    List path = null;
                                    for (int i = 0; i < associations.size(); i++) {
                                        Association association = (Association)associations.get(i);
                                        path = aksessService.getPathByAssociation(association);
                                        out.write("<tr class=\"tableRow" + (i%2) + "\">");
                                        String sel = "";
                                        if (associationId == association.getId()) sel = " checked";
                                        out.write("<td><input type=\"checkbox\" name=\"id\" value=\"" + association.getId() + "\"" + sel + "></td>");
                                        out.write("<td>");
                                        for (int j = 0; j < path.size(); j++) {
                                            PathEntry entry = (PathEntry)path.get(j);
                                            String title = entry.getTitle();
                                            if (j > 0) {
                                                out.write("&nbsp;&gt;&nbsp;");
                                            }
                                            out.write(title);
                                        }
                                        if (association.getAssociationtype() == AssociationType.SHORTCUT) {
                            %>
                            (<kantega:label key="aksess.confirmdelete.shortcut"/>)
                            <%
                                        }
                                        out.write("</td></tr>");
                                    }
                                }
                            %>
                            <tr>
                                <td colspan="2"><br>
                                    <div class="helpText"><kantega:label key="aksess.confirmdelete.xpinfo"/></div>
                                </td>
                            </tr>
                            </tbody>
                        </table>

                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="associationId" value="${associationId}">
                        <kantega:label key="aksess.confirmdelete.text"/> <strong>${contentTitle}</strong>
                    </c:otherwise>
                </c:choose>


                <div class="buttonGroup">
                    <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.delete"/>"></span>
                    <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
                </div>
            </fieldset>
        </div>
    </form>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>