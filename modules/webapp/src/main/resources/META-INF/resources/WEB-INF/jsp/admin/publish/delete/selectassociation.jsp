<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ page import="no.kantega.publishing.api.path.PathEntry"%>
<%@ page import="no.kantega.publishing.common.data.Association" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.enums.AssociationType" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="java.util.List" %>
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

<kantega:section id="head">
    <script type="text/javascript">
        var hasSubmitted = false;

        function buttonOkPressed() {
            // Prevent user from clicking several times
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.submit();
            }

            return false;
        }
    </script>

</kantega:section>

<kantega:section id="body">
    <form name="myform" method="post" action="DeleteAssociation.action">
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
                        <td><input id="selectAll" type="checkbox" name="dummy" value="-1" ></td>
                        <td><label for="selectAll"><kantega:label key="aksess.confirmdelete.xpall"/></label></td>
                    </tr>
                    <%
                        Content content = (Content)request.getAttribute("content");
                        Integer associationId = (Integer)request.getAttribute("associationId");
                        ContentManagementService aksessService = new ContentManagementService(request);
                        List associations = content.getAssociations();
                        if (associations != null) {
                            for (int i = 0; i < associations.size(); i++) {
                                Association association = (Association)associations.get(i);
                                List<PathEntry> path = aksessService.getPathByAssociation(association);
                                out.write("<tr class=\"tableRow" + (i%2) + "\">");
                                String sel = "";
                                if (associationId == association.getId()) sel = " checked";
                                out.write("<td><input id=\"");
                                out.write(String.valueOf(association.getId()));
                                out.write("\" type=\"checkbox\" name=\"id\" class=\"crossassociation\" value=\"" + association.getId() + "\"" + sel + "></td>");
                                out.write("<td><label for=\"");
                                out.write(String.valueOf(association.getId()));
                                out.write("\">");
                                for (int j = 0; j < path.size(); j++) {
                                    String title = path.get(j).getTitle();
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
                                out.write("</label></td></tr>");
                            }
                        }
                    %>
                    <tr>
                        <td colspan="2"><br>
                            <div class="ui-state-highlight"><kantega:label key="aksess.confirmdelete.xpinfo"/></div>
                        </td>
                    </tr>
                    </tbody>
                </table>

            </c:when>
            <c:otherwise>
                <input type="hidden" name="id" value="${associationId}">
                <kantega:label key="aksess.confirmdelete.text" title="${contentTitle}"/>
            </c:otherwise>
        </c:choose>

        <div class="buttonGroup">
            <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.delete"/>"></span>
            <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
        </div>
    </form>

    <script type="text/javascript">
        $('#selectAll').click(function(){
            var isChecked = this.checked;
            $('.crossassociation').each(function(){
                this.checked = isChecked;
            })
        });
    </script>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>