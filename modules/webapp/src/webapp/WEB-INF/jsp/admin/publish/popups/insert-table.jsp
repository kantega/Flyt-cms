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
<kantega:section id="title"><kantega:label key="aksess.inserttable.title"/></kantega:section>

<kantega:section id="head">
    <script language="Javascript" type="text/javascript">
        function buttonOkPressed() {
            var cellpadding = parseInt(document.myform.cellpadding.value);
            var border = 1;

            if (document.myform.border[1].checked) {
                border = 0;
            }

            if (cellpadding < 0) {
                cellpadding = 0;
            }

            var summary = document.myform.summary.value;

            if ("${modifyExisting}" == "true") {
                if (window.opener.focusField && window.opener.focusField.tagName == "TABLE") {
                    var table = window.opener.focusField;
                    table.border = border;
                    table.cellPadding = cellpadding;
                    table.summary = summary;
                }
            } else {
                var cols = parseInt(document.myform.cols.value);
                var rows = parseInt(document.myform.rows.value);

                if (cols < 1) {
                    alert("<kantega:label key="aksess.inserttable.col.missing"/>");
                    document.myform.cols.focus();
                    return false;
                }

                if (rows < 1) {
                    alert("<kantega:label key="aksess.inserttable.rows.missing"/>");
                    document.myform.rows.focus();
                    return false;
                }

                var insertHeader = document.myform.firstrowisheading.checked;
                if (insertHeader) {
                    rows--;

                }

                var html = "";
                html += '<TABLE border="' + border + '"';
                if (summary != "") {
                    html+= 'summary="' + summary + '"';
                }
                html += 'cellspacing="1" cellpadding="' + cellpadding + '">';
                if (insertHeader) {
                    html += "<THEAD><TR>";
                    for (var j = 0; j < cols; j++) {
                        html += '<TH>&nbsp;</TH>';
                    }
                    html += "</TR></THEAD>";
                }
                html += "<TBODY>";
                for (var i = 0; i < rows; i++) {
                    html += '<TR valign="top">';
                    for (var j = 0; j < cols; j++) {
                        html += '<TD>&nbsp;</TD>';
                    }
                    html += '</TR>';
                }
                html += '</TBODY></TABLE>';

                var editor = getParent().tinymce.EditorManager.activeEditor;
                insertHtml(editor, html);
            }
            return true;
        }

        function insertHtml(editor, html) {
            editor.execCommand("mceBeginUndoLevel");
            editor.execCommand("mceInsertRawHTML", false, html, {skip_undo : 1});
            editor.execCommand("mceEndUndoLevel");
        }

        function initTable() {
            var border = 0;
            var cellpadding = 2;
            var summary = "";
            if ("${modifyExisting}" == "true") {
                var table = getParent().focusField;
                border = table.border;
                cellpadding = table.cellPadding;
                if (table.summary) {
                    summary = table.summary;
                }                
            }

            document.myform.cellpadding.value = cellpadding;
            document.myform.summary.value = summary;

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
    <form action="" name="myform">
    <div id="InsertTableForm">
        <div class="fieldset">
            <fieldset>
                <h1><kantega:label key="aksess.inserttable.title"/></h1>
                <c:if test="${!modifyExisting}">
                    <div class="formElement">
                        <div class="heading">
                            <kantega:label key="aksess.inserttable.size"/>
                        </div>
                        <div class="inputs">
                            <label for="TableCols"><kantega:label key="aksess.inserttable.cols"/></label>
                            <input type="text" id="TableCols" size="2" maxlength="2" name="cols" value="2">
                            <label for="TableRows"><kantega:label key="aksess.inserttable.rows"/></label>
                            <input type="text" id="TableRows" size="2" maxlength="2" name="rows" value="5"><br>
                            <input type="checkbox" name="firstrowisheading" checked="checked"><kantega:label key="aksess.inserttable.firstrowisheading"/>
                        </div>
                    </div>
                </c:if>
                <div class="formElement">
                    <div class="heading">
                        <kantega:label key="aksess.inserttable.summary"/>
                    </div>
                    <div class="inputs">
                        <textarea rows="3" cols="20" class="fullWidth" wrap="soft" name="summary" value=""></textarea>
                    </div>
                </div>
                <div class="formElement">
                    <div class="heading">
                        <kantega:label key="aksess.inserttable.border"/>
                    </div>
                    <div class="inputs">
                        <input type="radio" id="TableBorder" name="border" value="1">
                        <label for="TableBorder"><kantega:label key="aksess.text.ja"/></label>
                        <input type="radio" id="TableNoBorder" name="border" value="0">
                        <label for="TableNoBorder"><kantega:label key="aksess.text.nei"/></label>
                    </div>
                </div>
                <div class="formElement">
                    <div class="heading">
                        <kantega:label key="aksess.inserttable.spacing"/>
                    </div>
                    <div class="inputs">
                        <input type="text" size="2" maxlength="2" name="cellpadding" value=""> (<kantega:label key="aksess.inserttable.pixels"/>)
                    </div>
                </div>
                <div class="buttonGroup">
                    <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                    <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
                </div>
            </fieldset>

        </div>

    </div>
    </form>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>
