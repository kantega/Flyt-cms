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
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<kantega:section id="title">
    <kantega:label key="aksess.tools.settings"/>
</kantega:section>



<kantega:section id="body">
    <script type="text/javascript">
        function buttonOkPressed() {
            closeWindow();
        }
    </script>

    Settings

    <div class="buttonGroup">
        <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
        <span class="button"><input type="submit" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
    </div>
</kantega:section>

<%@ include file="../layout/popupLayout.jsp" %>