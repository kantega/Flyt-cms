<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
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
    <script type="text/javascript">
        function buttonOkPressed() {
            getParent().stateHandler.setState("${currentPage.url}");
            return true;
        }
    </script>
</kantega:section>
<kantega:section id="body">
    <p>
        <kantega:label key="aksess.copypaste.duplicatealias"/>
    </p>

    <ul>
        <c:forEach items="${aliases}" var="alias">
            <li>${alias}</li>
        </c:forEach>
    </ul>

    <div class="buttonGroup">
        <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
    </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>