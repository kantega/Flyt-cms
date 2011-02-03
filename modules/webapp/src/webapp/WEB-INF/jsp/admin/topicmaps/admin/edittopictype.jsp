<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  ~ Copyright 2011 Kantega AS
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
    <kantega:label key="aksess.topicmaps.title"/>
</kantega:section>

<kantega:section id="content">
    <script type="text/javascript">
        function saveForm() {
            var form = document.myform;

            // Reset validationErrors
            validationErrors.length = 0;

            validateChar(form.name, true, false);

            if (showValidationErrors()) {
                form.submit();
            }
        }

    </script>
    <form name="myform" action="EditTopicType.action" method="post">
        <admin:box>
            <h1><kantega:label key="aksess.topicmaps.type.title"/></h1>

            <input type="hidden" name="topicMapId" value="${topicMapId}">
            <c:if test="${not empty topic}">
            <input type="hidden" name="topicId" value="${topic.id}">
            </c:if>

            <div class="formElement">
                <div class="heading"><label for="name"><kantega:label key="aksess.topicmaps.admin.type.name"/></label></div>
                <div class="inputs">
                    <input type="text" name="name" id="name" title="<kantega:label key="aksess.topicmaps.admin.type.name"/>" size="64" maxlength="64" value="${topic.baseName}">
                </div>
            </div>

            <div class="buttonGroup">
                <span class="button"><input type="submit" class="save" value="<kantega:label key="aksess.button.save"/>"></span>
                <span class="button"><input type="submit" class="cancel" onclick="window.location.href='ListTopicTypes.action?topicMapId=${topicMapId}'" value="<kantega:label key="aksess.button.cancel"/>"></span>
            </div>
        </admin:box>
    </form>
</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>