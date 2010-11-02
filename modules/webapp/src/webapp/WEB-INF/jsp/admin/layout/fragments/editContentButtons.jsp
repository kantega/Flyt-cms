<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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

<c:choose>
    <c:when test="${canPublish}">
        <span class="barButton"><input type="button" class="publish" value="<kantega:label key="aksess.button.publish"/>" accesskey="P"></span>
    </c:when>
    <c:otherwise>
        <span class="barButton"><input type="button" class="save" value="<kantega:label key="aksess.button.save"/>" accesskey="P"></span>
    </c:otherwise>
</c:choose>
<span class="barButton"><input type="button" class="savedraft" value="<kantega:label key="aksess.button.savedraft"/>" accesskey="K"></span>
<c:if test="${hearingEnabled}">
    <span class="barButton"><input type="button" class="hearing" value="<kantega:label key="aksess.button.hoering"/>"></span>
</c:if>
<span class="barButton"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>" accesskey="A"></span>

<c:if test="${!currentContent.new}">
    <span>
        <input type="checkbox" class="checkbox" name="minorchange" id="MinorChange" value="true"<c:if test="${currentContent.minorChange}"> checked="checked"</c:if>><label class="checkbox" id="LabelMinorChange" for="MinorChange"><kantega:label key="aksess.publishinfo.minorchange"/></label>
    </span>
</c:if>

