<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<c:forEach var="note" items="${notes}">
    <div class="note">
        <h2><admin:formatdate date="${note.date}"/> - ${note.author}<span class="noteButtons"><a href="${note.noteId}" class="delete"><kantega:label key="aksess.button.delete"/></a></span></h2>        
        <c:set var="noteText" value="${note.text}" scope="request"/>
        <%=((String)request.getAttribute("noteText")).replaceAll("\n", "<br>")%>
    </div>
</c:forEach>