<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<div class="roundCorners"><div class="top"><div class="corner"></div></div><div class="body"><div class="left"><div class="right">
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

<div class="repeaterButtons repeaterHandle">
    <c:if test="${repeater.numberOfRows > repeater.minOccurs}">
        <a href="#" onclick="openaksess.editcontext.deleteRepeaterRow('${repeater.nameIncludingPath}', this)"><span class="delete"><kantega:label key="aksess.button.delete"/></span></a>
    </c:if>
</div>
