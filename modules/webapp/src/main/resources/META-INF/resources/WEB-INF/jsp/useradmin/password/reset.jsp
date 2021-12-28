<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/admin" prefix="admin" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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


<kantega:section id="content">
    <%@ include file="../../admin/layout/fragments/infobox.jsp" %>

    <c:if test="${noemail != null}">
        <div class="ui-state-highlight">
            E-postadresse er ikke lagret for denne brukeren.
        </div>
    </c:if>
    <c:if test="${mailtemplate != null}">
        <script type="text/javascript">

            function updateVisibility() {
                var elems = document.getElementById("typeform").elements;
                var passform = document.getElementById("passwordform");
                var mailform = document.getElementById("mailform");

                var sel = "";
                for(var i = 0; i < elems.length; i++) {
                    var e = elems[i];
                    if(e.type == "radio" && e.checked) {
                        sel = e.value;
                    }
                }

                passform.style.display = sel == "type" ? "block" : "none";
                mailform.style.display = sel == "mail" ? "block" : "none";

            }
        </script>
        <form id="typeform">
            <input value="type" name="sendortype" type="radio" <c:if test="${!maildefault}">checked="checked"</c:if> onclick="updateVisibility()"> Skriv inn passord
            <input value="mail" name="sendortype" type="radio" <c:if test="${maildefault}">checked="checked"</c:if> onclick="updateVisibility()"> Send ut generert passord på epost
        </form>

        <form action="reset" name="myform" method="post" id="mailform" style="<c:if test="${!maildefault}">display: none;</c:if>">
            <input type="hidden" name="domain" value="${domain}">
            <input type="hidden" name="userId" value="${userId}">
            <admin:box>
                <p>
                    <label>Fra:</label>
                    <input type="text" name="from" class="textInput" value="${mailfrom}" maxlength="64">
                </p>

                <p>
                    <label>Til:</label>
                    <a href="mailto:${mailto}">${mailto}</a>
                </p>

                <p>
                    <label>Emne:</label>
                    <input type="text" name="subject" class="textInput" value="${mailsubject}" maxlength="64">
                </p>

                <p>
                    <label>Tekst:</label>
                    <textarea name="message" style="width: 600px; height: 110px">${mailtemplate}</textarea>

                </p>

                <div class="buttonGroup">
                    <span class="button"><input type="submit" name="mailsubmit" class="button ok" value="<kantega:label key="aksess.button.send"/>"></span>
                    <span class="button"><input type="submit" class="cancel" onclick="location='../profile/search'" value="<kantega:label key="aksess.button.cancel"/>"></span>
                </div>
            </admin:box>
        </form>

    </c:if>

    <form action="reset" name="myform" method="post" id="passwordform" style="<c:if test="${maildefault}">display: none;</c:if>">
        <input type="hidden" name="domain" value="${domain}">
        <input type="hidden" name="userId" value="${userId}">
        <admin:box>
            <div class="formElement">
                <div class="heading"><label><kantega:label key="useradmin.password.password1"/></label></div>
                <div class="inputs"><input type="password" name="password1" class="textInput" value="${password1}" maxlength="64" autocomplete="off"></div>
            </div>

            <div class="formElement">
                <div class="heading"><label><kantega:label key="useradmin.password.password2"/></label></div>
                <div class="inputs"><input type="password" name="password2" class="textInput" value="${password1}" maxlength="64" autocomplete="off"></div>
            </div>

            <div class="buttonGroup">
                <span class="button"><input type="submit" name="passwordsubmit" class="ok" value="<kantega:label key="aksess.button.save"/>"></span>
                <span class="button"><input type="button" class="cancel" onclick="location='../profile/search'" value="<kantega:label key="aksess.button.cancel"/>"></span>
            </div>
        </admin:box>
    </form>


</kantega:section>

<%@ include file="../../admin/layout/administrationLayout.jsp" %>
