<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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
<%
<%
    RequestParameters param = new RequestParameters(request);
    boolean modifyExisting = param.getBoolean("edit");
%>
%>
<kantega:section id="title"><kantega:label key="aksess.inserttable.title"/></kantega:section>

<kantega:section id="head">
    <script language="Javascript" type="text/javascript">
        function doInsert() {
            var cellpadding = parseInt(document.myform.cellpadding.value);
            var border = 1;

            if (document.myform.border[1].checked) {
                border = 0;
            }

            if (cellpadding < 0) {
                cellpadding = 0;
            }

            if ("<%=modifyExisting%>" == "true") {
                if (window.opener.focusField && window.opener.focusField.tagName == "TABLE") {
                    var table = window.opener.focusField;
                    table.border = border;
                    table.cellPadding = cellpadding;
                }
            } else {
                var cols = parseInt(document.myform.cols.value);
                var rows = parseInt(document.myform.rows.value);

                if (cols < 1) {
                    alert("Angi antall kolonner");
                    document.ftable.cols.focus();
                    return;
                }

                if (rows < 1) {
                    alert("Angi antall rader");
                    document.ftable.rows.focus();
                    return;
                }

                var html = "";
                html += '<TABLE border=' + border + ' cellspacing=1 cellpadding=' + cellpadding + '>';
                for (var i = 0; i < rows; i++) {
                    html += '<TR valign="top">';

                    for (var j = 0; j < cols; j++) {
                        html += '<TD>&nbsp;</TD>';
                    }
                    html += '</TR>';
                }
                html += '</TABLE>';

                if (window.opener) {
                    window.opener.insertTag(html);
                }
            }
            window.close();
        }

        function initTable() {
            var border = 0;
            var cellpadding = 2;
            if ("<%=modifyExisting%>" == "true") {
                var table = window.opener.focusField;
                border = table.border;
                cellpadding = table.cellPadding;
            }

            document.myform.cellpadding.value = cellpadding;

            if (border > 0) {
                document.myform.border[1].checked = false
                document.myform.border[0].checked = true;
            } else {
                document.myform.border[1].checked = true
                document.myform.border[0].checked = false;
            }
        }


        $(document).ready(function() {
            initTable();
        });

    </script>
</kantega:section>

<kantega:section id="body">
    <div id="InsertTableForm">
        <!-- Todo fix layout -->
        <form name="myform" action="">
            <table border="0" width="100%" cellspacing="0" cellpadding="0">
                <tr>
                    <td colspan="2" class="tableHeading"><b><kantega:label key="aksess.inserttable.title"/></b></td>
                </tr>
                <%
                    if (!modifyExisting) {
                %>
                <tr>
                    <td colspan="2"><img src="../bitmaps/blank.gif" width="2" height="2"></td>
                </tr>
                <tr>
                    <td colspan="2" class="inpHeading"><b><kantega:label key="aksess.inserttable.storrelse"/></b></td>
                </tr>
                <tr>
                    <td colspan="2"><img src="../bitmaps/blank.gif" width="2" height="2"></td>
                </tr>
                <tr>
                    <td width="50%"><b><kantega:label key="aksess.inserttable.kolonner"/></b></td>
                    <td width="50%"><b><kantega:label key="aksess.inserttable.rader"/></b></td>
                </tr>
                <tr>
                    <td><input type="text" size="2" maxlength="2" name="cols" value="2"></td>
                    <td><input type="text" size="2" maxlength="2" name="rows" value="2"></td>
                </tr>
                <tr>
                    <td colspan="2"><img src="../bitmaps/blank.gif" width="2" height="8"></td>
                </tr>

                <tr>
                    <td colspan="2" class="inpHeading"><b><kantega:label key="aksess.inserttable.andreegenskaper"/></b></td>
                </tr>
                <%
                    }
                %>
                <tr>
                    <td colspan="2"><img src="../bitmaps/blank.gif" width="2" height="2"></td>
                </tr>
                <tr>
                    <td><b><kantega:label key="aksess.inserttable.visramme"/></b></td>
                    <td><b><kantega:label key="aksess.inserttable.mellomrom"/></b></td>
                </tr>
                <tr>
                    <td><input type="radio" name="border" value="1"><kantega:label key="aksess.text.ja"/> <input type="radio" name="border" value="0"> <kantega:label key="aksess.text.nei"/></td>
                    <td><input type="text" size="2" maxlength="2" name="cellpadding" value=""> (<kantega:label key="aksess.inserttable.antallpixler"/>)</td>
                </tr>
            </table>

            <div class="buttonGroup">
                <a href="Javascript:doInsert()" class="button ok"><kantega:label key="aksess.button.ok"/></a>
                <a href="Javascript:window.close()" class="button cancel"><kantega:label key="aksess.button.avbryt"/></a>
            </div>
        </form>
    </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>
