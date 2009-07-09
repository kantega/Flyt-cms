<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
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
    <c:choose>
        <c:when test="${attachmentId != -1}"><kantega:label key="aksess.attachment.add"/></c:when>
        <c:otherwise><kantega:label key="aksess.attachment.update"/></c:otherwise>
    </c:choose>
</kantega:section>

<kantega:section id="script">
    <script type="text/javascript">
        function doReplace()
        {
            var search  = document.myform.search.value;
            var replace = document.myform.replace.value;

            if (search == "") {
                alert("<kantega:label key="aksess.replacetext.notext"/>");
                return;
            }

            var txt = "<kantega:label key="aksess.replacetext.confirm"/>";
            txt = txt.replace("${search}", search);

            if ((replace == "") && (!confirm(txt))) {
                return;
            }

            if (window.opener) {
                window.opener.replaceString(search, replace);
            }
            window.close();
        }
    </script>
</kantega:section>

<kantega:section id="body">
    <div id="ReplaceTextForm">
    <form name="myform" action="">
        <fieldset>
            <legend><kantega:label key="aksess.replacetext.title"/></legend>

            <div class="formElement">
                <label for="search"><kantega:label key="aksess.replacetext.soketter"/></label>
                <input type="text" id="search" size="20" maxlength="40" name="search" value="">
            </div>

            <div class="formElement">
                <label for="replace"><kantega:label key="aksess.replacetext.erstattmed"/></label>
                <input type="text" id="replace" size="20" maxlength="40" name="replace" value="">
            </div>
        </fieldset>

        <div class="buttonGroup">
            <a href="Javascript:doReplace()" class="button ok"><kantega:label key="aksess.button.ok"/></a>
            <a href="Javascript:window.close()" class="button cancel"><kantega:label key="aksess.button.avbryt"/></a>
        </div>
    </form>
    </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>
