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
        var hasSubmitted = false;
        function buttonOkPressed() {
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.submit();
            }
        }
    </script>
</kantega:section>
<kantega:section id="body">

    <form name="myform" method="post" action="CopyPasteMultimedia.action">
        <input type="hidden" name="mmId" value="${multimedia.id}">
        <input type="hidden" name="newParentId" value="${newParent.id}">

        <admin:box>
            <p>
                <kantega:label key="aksess.copypaste.move"/> <b>${multimedia.name}</b> <kantega:label key="aksess.copypaste.under"/> <b>${newParent.name}</b> ?
            </p>

            <div class="buttonGroup">
                <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
            </div>
        </admin:box>
    </form>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>