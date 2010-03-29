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
    
</kantega:section>

<kantega:section id="head">
    <script type="text/javascript" language="Javascript">

        function buttonOkPressed() {
            var note = $("#Note").val();
            if (note == "") {
                alert("<kantega:label key="aksess.reject.missingvalue"/>");
            } else {
                $.post("${pageContext.request.contextPath}/admin/publish/ApproveOrReject.action", {reject: true, note:note, url: ${url}}, function(data) {
                    debug("reject note");
                    getParent().ContentStatus.showApproveOrReject(false);
                    setTimeout("closeWindow()", 10);
                });
            }
            return false;
        }
    </script>
</kantega:section>

<kantega:section id="body">
    <div id="RejectNoteForm">
        <form name="myform" action="">
            <div class="fieldset">
                <fieldset>
                    <div class="formElement">
                        <div class="heading">
                            <label for="Note"><kantega:label key="aksess.reject.note"/></label>
                        </div>
                        <div class="inputs">
                            <textarea rows="4" cols="30" id="Note" class="fullWidth"></textarea>
                        </div>
                    </div>
                    <div class="buttonGroup">
                        <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.reject"/>"></span>
                        <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
                    </div>
                </fieldset>
            </div>
        </form>
    </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>