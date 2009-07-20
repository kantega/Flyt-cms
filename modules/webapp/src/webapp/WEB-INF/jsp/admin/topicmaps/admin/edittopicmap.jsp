<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
    <form name="myform" action="EditTopicMap.action" method="post">
        <div class="fieldset">
            <fieldset>
                <legend><kantega:label key="aksess.topicmaps.title"/></legend>

                <input type="hidden" name="id" value="${topicMap.id}">

                <div class="formElement">
                    <div class="heading"><label for="name"><kantega:label key="aksess.topicmaps.admin.name"/></label></div>
                    <div class="inputs">
                        <input type="text" name="name" id="name" title="<kantega:label key="aksess.topicmaps.admin.name"/>" size="64" maxlength="64" value="${topicMap.name}">
                    </div>
                </div>

                <div class="formElement">
                    <div class="heading"><kantega:label key="aksess.topicmaps.admin.iseditable"/></div>
                    <div class="inputs">
                        <input name="iseditable" id="iseditable" type="radio" value="true" <c:if test="${topicMap.editable}"> checked</c:if>><label for="iseditable"><kantega:label key="aksess.text.ja"/><br></label>
                        <input name="iseditable" id="isnoteditable" type="radio" value="false" <c:if test="${!topicMap.editable}"> checked</c:if>><label for="isnoteditable"><kantega:label key="aksess.text.nei"/></label>                        
                    </div>
                </div>

            </fieldset>

            <div class="buttonGroup">
                <input type="submit" class="button save" value="<kantega:label key="aksess.button.save"/>">
                <input type="button" class="button cancel" onclick="window.location.href='ListTopicMaps.action'" value="<kantega:label key="aksess.button.cancel"/>">
            </div>
        </div>
    </form>
</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>