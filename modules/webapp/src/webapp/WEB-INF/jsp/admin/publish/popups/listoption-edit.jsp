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
    <kantega:label key="aksess.editablelist.title"/>
</kantega:section>

<kantega:section id="head">
    <script type="text/javascript" language="Javascript">

        function buttonOkPressed() {

            var optionValue = document.myform.optionValue.value;
            var attributeKey = '<%=param.getString("attributeKey")%>';
            var defaultSelected = document.myform.defaultSelected.value;
            var language = <%=param.getString("language")%>;

            if (optionValue == "") {
                alert("<kantega:label key="aksess.editablelist.missingvalue"/>");
            } else {
                var xmlhttp = getXmlHttp();
                xmlhttp.open("POST",  "../publish/AddListOption.action", true);
                xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
                xmlhttp.onreadystatechange=function() {
                    if (xmlhttp.readyState==4) {
                        if(xmlhttp.responseText == "success") {
                            window.opener.insertOptionIntoList(optionValue);
                            setTimeout("closeWindow()", 1);
                        }
                        else {
                            alert("<kantega:label key="aksess.editablelist.error"/>");
                        }
                    }
                }
                xmlhttp.send("value=" + optionValue + "&attributeKey=" + attributeKey + "&defaultSelected=" + defaultSelected + "&language=" + language);
            }

            return false;
        }
    </script>
</kantega:section>

<kantega:section id="body">
    <div id="EditListOptionForm">
        <form name="myform" action="">
            <div class="fieldset">
                <fieldset>
                    <h1><kantega:label key="aksess.editablelist.title"/></h1>

                    <div class="formElement">
                        <div class="heading">
                            <label for="optionValue"><kantega:label key="aksess.editablelist.value"/></label>
                        </div>
                        <div class="inputs">
                            <input type="text" id="optionValue" name="optionValue" maxlength="255"><br>
                            <input type="checkbox" id="defaultSelected" name="defaultSelected" value="true">
                            <label for="defaultSelected"><kantega:label key="aksess.editablelist.defaultSelected"/></label>
                        </div>
                    </div>
                    <div class="buttonGroup">
                        <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                        <span class="button"><input type="submit" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
                    </div>
                </fieldset>
            </div>
        </form>
    </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>