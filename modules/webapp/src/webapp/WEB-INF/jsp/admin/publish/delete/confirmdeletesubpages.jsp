<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
    </script>

</kantega:section>

<kantega:section id="body">
    <form name="myform" method="post" action="DeleteAssociation.action">
        <admin:box>
            <input type="hidden" name="confirmMultipleDelete" value="true">
            <c:forEach var="a" items="${associationIds}">
                <input type="hidden" name="id" value="${a}">
            </c:forEach>

            <p><kantega:label key="aksess.confirmdelete.multiple.sikker"/></p>

            <p><kantega:label key="aksess.confirmdelete.multiple.tekst" subpages="${fn:length(toBeDeleted)}"/></p>

            <div style="overflow:auto; max-height:100px;">
                <ul>
                    <c:forEach var="content" items="${toBeDeleted}">
                        <li>${content.title}</li>
                    </c:forEach>
                </ul>
            </div>

            <div class="buttonGroup">
                <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.delete"/>"></span>
                <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
            </div>
        </admin:box>
    </form>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>