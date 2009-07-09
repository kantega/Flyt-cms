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

<kantega:section id="script">
    <script type="text/javascript" language="Javascript" src="../../aksess/js/common.js"></script>
    <script type="text/javascript" language="Javascript">

        function addListOption() {

            var optionValue = document.myform.optionValue.value;
            var attributeKey = '<%=param.getString("attributeKey")%>';
            var defaultSelected = document.myform.defaultSelected.value;
            var language = <%=param.getString("language")%>;

            if (optionValue == "") {
                alert("<kantega:label key="aksess.editablelist.missingvalue"/>");
            }
            else {
                var xmlhttp = getXmlHttp();
                xmlhttp.open("POST",  "../publish/AddListOption.action", true);
                xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
                xmlhttp.onreadystatechange=function() {
                    if (xmlhttp.readyState==4) {
                        if(xmlhttp.responseText == "success") {
                            window.opener.insertOptionIntoList(optionValue);
                            setTimeout("window.close()", 1);
                        }
                        else {
                            alert("<kantega:label key="aksess.editablelist.error"/>");
                        }
                    }
                }
                xmlhttp.send("value=" + optionValue + "&attributeKey=" + attributeKey + "&defaultSelected=" + defaultSelected + "&language=" + language);
            }
        }
    </script>
</kantega:section>

<kantega:section id="body">
    <table border="0" width="100%" cellspacing="0">
        <tr>
            <td class="tableHeading"><b></b></td>
        </tr>
        <tr>
            <td>
                <b><kantega:label key="aksess.editablelist.value"/></b><br>
                <input type="text" name="optionValue" style="width:250px;" maxlength="255">
            </td>
        </tr>
        <tr>
            <td>
                <input type="checkbox" name="defaultSelected" value="true">
                <kantega:label key="aksess.editablelist.defaultSelected"/>
            </td>
        </tr>
    </table>
    <p>
        <a href="Javascript:addListOption()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a>&nbsp;&nbsp;<a href="Javascript:window.close()"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
    </p>

    <div id="EditListOptionForm">
    <form name="myform" action="">
        <fieldset>
            <legend><kantega:label key="aksess.editablelist.title"/></legend>

            <div class="formElement">
                <label for="search"><kantega:label key="aksess.editablelist.value"/></label>
                <input type="text" name="optionValue" maxlength="255">
            </div>
            
            <div class="formElement">
                <input type="checkbox" id="defaultSelected" name="defaultSelected" value="true">
                <label for="defaultSelected"><kantega:label key="aksess.editablelist.defaultSelected"/></label>
            </div>

        </fieldset>
        <div class="buttonGroup">
            <a href="Javascript:addListOption()" class="button ok"><kantega:label key="aksess.button.ok"/></a>
            <a href="Javascript:window.close()" class="button cancel"><kantega:label key="aksess.button.avbryt"/></a>
        </div>
    </form>
   </div>
</kantega:section>
<%@ include file="../../design/popup.jsp" %>