<?xml version="1.0" encoding="utf-8"?><%@ page contentType="text/xml;charset=utf-8" language="java" pageEncoding="iso-8859-1" %><%--
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fileupload>
    <files>
    <c:forEach var="file" items="${files}">
        <file id="${file.id}"><c:out value="${file.filename}"/></file>
    </c:forEach>
    </files>
</fileupload>