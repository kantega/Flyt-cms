<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
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

<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<kantega:section id="title">
    <kantega:label key="aksess.unusedattachments.title"/>
</kantega:section>

<kantega:section id="content">
    <script>
        function toggleSearchable(id, checked) {
            $.post('${pageContext.request.contextPath}/admin/attachment/' + id + '/togglesearchable', function(response) {
                openaksess.common.debug(id + ' set to searchable: ' + checked);
            }, 'json');
        }

        function deleteAttachment(id) {
            if (confirm("<kantega:label key="aksess.attachments.confirmdelete"/>")) {
                $.post('${pageContext.request.contextPath}/admin/attachment/' + id + '/delete', function(response) {
                    openaksess.common.debug(id + ' deleted: ');
                    $('#attachment' + id).remove();
                }, 'json');
            }
        }
    </script>
    <admin:box>
        <h1><kantega:label key="aksess.unusedattachments.title"/></h1>
        <p><kantega:label key="aksess.unusedattachments.disclaimer"/></p>
        <table>
            <tr>
                <th>Vedlegg</th>
                <th>Modifisert</th>
                <th>Innholdsside</th>
                <th>Søkbart</th>
                <th></th>
            </tr>
        <c:forEach items="${unusedAttachments}" var="attachment">
            <tr id="attachment${attachment.id}">
                <td><a href="${attachment.url}">${attachment.filename}</a></td>
                <td><fmt:formatDate value="${attachment.lastModified}" pattern="dd.MM.yyyy" /></td>
                <td><aksess:link contentid="${attachment.contentId}"><aksess:getattribute name="title" contentid="${attachment.contentId}" /></aksess:link></td>
                <td><input type="checkbox" <c:if test="${attachment.searchable}">checked</c:if> onchange="toggleSearchable(${attachment.id}, this.checked)" /></td>
                <td><button onclick="deleteAttachment(${attachment.id})">Slett</button></td>
            </tr>
        </c:forEach>
        </table>
    </admin:box>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>
