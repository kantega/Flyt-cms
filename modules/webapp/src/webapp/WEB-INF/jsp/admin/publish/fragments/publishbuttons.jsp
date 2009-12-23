<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<c:if test="${currentContent != null}">
<div id="EditContentButtons" class="buttonBar">
    <c:choose>
        <c:when test="${canPublish}">
            <span class="barButton"><input type="button" class="publish" value="<kantega:label key="aksess.button.publish"/>"></span>
        </c:when>
        <c:otherwise>
            <span class="barButton"><input type="button" class="save" value="<kantega:label key="aksess.button.save"/>"></span>
        </c:otherwise>
    </c:choose>
    <span class="barButton"><input type="button" class="savedraft" value="<kantega:label key="aksess.button.savedraft"/>"></span>
    <c:if test="${hearingEnabled}">
        <span class="barButton"><input type="button" class="hearing" value="<kantega:label key="aksess.button.hoering"/>"></span>
    </c:if>
    <span class="barButton"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
</div>
</c:if>