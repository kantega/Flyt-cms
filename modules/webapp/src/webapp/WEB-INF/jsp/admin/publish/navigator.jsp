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
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin"%>

<c:forEach items="${sites}" var="site">
    <admin:printcontentnavigator root="${site}" currentId="${thisId}" highlightCurrent="${highlightCurrent}" startId="${startId}"/>
</c:forEach>



<div id="NavigatorState" style="display: none;">
    <div class="expand">true</div>
    <div class="sort">${sort}</div>
    <div class="openFolders">${openFolders}</div>
</div>