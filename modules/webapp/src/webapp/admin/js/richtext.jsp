<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
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
    request.setAttribute("aksess_locale", Aksess.getDefaultAdminLocale());
%>

/**
 Rich text editor functions
 */
function rtInitEditor(editorId, valuefld, css) {
    var editor = document.getElementById(editorId);
    editor.css = css;

    var val = valuefld.value;

    var initOk = false;

    try {
        if (browser.isIE) {
            editor.contentWindow.document.designMode="on";
        } else {
            editor.contentDocument.designMode = "on";
        }
        initOk = true;
    } catch (e) {
        alert(e);
    }

    if (initOk) {
        rtWriteContent(editor, val);
    }
}

function rtWriteContent(editor, val) {
    editor.contentWindow.document.open();
    editor.contentWindow.document.write('<HTML><HEAD><LINK rel="stylesheet" href="../css/editor.css" type="text/css"><LINK rel="stylesheet" href="../..' + editor.css + '" type="text/css"></HEAD><BODY>');
    editor.contentWindow.document.write(val + " ");
    editor.contentWindow.document.write('</BODY></HTML>');
    editor.contentWindow.document.close();
}

function rtCopyValue(editorId, hidden) {
    var editor = document.getElementById(editorId);

    var newVal = "";

    if (editor.HTMLmode) {
        if(editor.contentWindow.document.all) {
            newVal = editor.contentWindow.document.body.innerText;
        } else {
            var htmlSrc = editor.contentWindow.document.body.ownerDocument.createRange();
            htmlSrc.selectNodeContents(editor.contentWindow.document.body);
            newVal = htmlSrc.toString();
        }
    } else {
        newVal = editor.contentWindow.document.body.innerHTML;
    }

    // IE legger inn adressen til denne siden av og til i lenker ...
    var href = "" + location.href;
    if (newVal.indexOf(href) != -1) {
        newVal = newVal.split(href).join('');
    }

    if (hidden.value != newVal) {
        setIsUpdated();
        hidden.value = newVal;
    }

    return true;
}

function rtCleanupHTML(editorId) {
    var editor = document.getElementById(editorId);

    var html;
    if (editor.HTMLmode) {
        alert("<kantega:label key="aksess.js.advarsel.htmlmodus"/>");
        return false;
    } else {
        html = editor.contentWindow.document.body.innerHTML;
    }

    var xmlhttp = getXmlHttp();

    xmlhttp.open("POST",  "<%=request.getContextPath()%>/admin/publish/CleanupHTML.action", true);
    xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    xmlhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xmlhttp.onreadystatechange=function() {
        if (xmlhttp.readyState==4) {
            rtCleanupHTMLCallback(editor, xmlhttp.responseText)
        }
    }
    xmlhttp.send("html=" + encodeURIComponent(html) + "&dummy=" + new Date());

}

function rtCleanupHTMLCallback(editor, txt) {
    rtWriteContent(editor, txt);
}

function checkEditorMode(editor) {
    editor.contentWindow.focus();

    if (editor.HTMLmode) {
        alert("<kantega:label key="aksess.js.advarsel.htmlmodus"/>");
        return false;
    }

    return true;
}

function findCell(e) {
    if (e.tagName == "TD") {
        return e;
    } else if (e.tagName == "BODY") {
        return null;
    } else {
        return findCell(e.parentNode);
    }
}

function findTableElement(e) {
    if (e.tagName == "TD" || e.tagName == "TR" || e.tagName == "TABLE") {
        return e;
    } else if (e.tagName == "BODY") {
        return null;
    } else {
        return findTableElement(e.parentNode);
    }
}

function findTable(e) {
    if (e.tagName == "TABLE") {
        return e;
    } else if (e.tagName == "BODY") {
        return null;
    } else {
        return findTable(e.parentNode);
    }
}

function findA(e) {
    if (e.tagName == "A") {
        return e;
    } else if (e.tagName == "BODY" || e.tagName == "P" || e.tagName == "TD" || e.tagName == "DIV") {
        return null;
    } else {
        return findA(e.parentNode);
    }
}


function rtToggleHTMLMode(editorId) {
    var editor = document.getElementById(editorId);

    var oldHTMLmode = false;

    if (editor.HTMLmode) {
        oldHTMLmode = true;
    }

    if (oldHTMLmode) {
        // Change to WYSIWYG mode
        var tmp = "";
        if(editor.contentWindow.document.all) {
            tmp = editor.contentWindow.document.body.innerText;
        } else {
            var htmlSrc = editor.contentWindow.document.body.ownerDocument.createRange();
            htmlSrc.selectNodeContents(editor.contentWindow.document.body);
            tmp = htmlSrc.toString();
        }

        rtWriteContent(editor, tmp);

    } else {
        // Change to HTML mode
        var fonts=editor.contentWindow.document.getElementsByTagName("FONT");
        for (var i=0;i<fonts.length;i++) {
            if (fonts[i].style.backgroundColor!="")
                fonts[i].outerHTML=fonts[i].innerHTML;
        }
        var tmp = editor.contentWindow.document.body.innerHTML;
        editor.contentWindow.document.open()
        editor.contentWindow.document.write("<BODY style=\"font:10pt courier, monospace\">")
        editor.contentWindow.document.close()

        if(editor.contentWindow.document.all) {
            editor.contentWindow.document.body.innerText = tmp;
        } else {
            var htmlSrc = editor.contentWindow.document.createTextNode(tmp);
            editor.contentWindow.document.body.innerHTML = "";
            editor.contentWindow.document.body.appendChild(htmlSrc);
        }
    }
    editor.HTMLmode = !oldHTMLmode;

    editor.contentWindow.focus()
}

function rtSpellCheck(editorId) {
    var editor = document.getElementById(editorId);
    if (editor.HTMLmode) {
        alert("Stavekontroll kan ikke brukes i HTML modus");
        return;
    }

    try {
        var spellchecker = new ActiveXObject("ieSpell.ieSpellExtension");
        var isChecked = spellchecker.CheckAllLinkedDocuments2(editor.document.body, true);
        if (isChecked) {
            alert("Stavekontrollen er ferdig");
        }
    } catch (e) {
        if(e.number==-2146827859) {
            var charwin = window.open("../popups/downloadspellchecker.jsp?refresh=" + getRefresh(), "charWindow", "dependent,toolbar=no,width=410,height=256,resizable=yes");
        } else {
            alert(e.message);
        }
    }
}


function rtSetTableStyle(editor, cssClass) {
    var range = getRange(editor);
    var selectedElement = null;
    if (range) {
        selectedElement = findTableElement(getNodeFromRange(range));
    }
    if (selectedElement) {
        if (selectedElement.tagName == "TR") {
            // Fjern farger fra celler i raden
            for (var i = 0; i < selectedElement.childNodes.length; i++) {
                var cell = selectedElement.childNodes[i];
                cell.removeAttribute("bgcolor");
                cell.style.backgroundColor = "";
                cell.className = "";
                cell.style.color = "";
            }
        }

        // Fjern farger fra dette element, legg til style
        selectedElement.removeAttribute("bgcolor");
        if (cssClass != "normal") {
            selectedElement.className = cssClass;
        }
        if (browser.isIE5up) {
            editor.document.recalc(true);
        }
    }
}


function rtSetTextStyle(editor, cssClass) {

    var range = getRange(editor);

    if (range) {
        var node = getNodeFromRange(range);

        // Fjern f�rste tag utenfor
        var tags = new Array("SPAN", "FONT", "DIV", "H1", "H2", "H3", "H4", "H5", "H6", "B", "I", "U", "STRONG", "EM");
        for (var i = 0; i < tags.length; i++) {
            if (node.tagName == tags[i]) {
                var parentToParent = node.parentNode;
                if (parentToParent) {
                    parentToParent.removeChild(parent);
                }
                break;
            }
        }

        if (node.tagName == "IMG") {
            if (cssClass != "normal") {
                // Sett CSS stil p� bilde direkte
                node.className = cssClass;
            } else {
                node.removeAttribute("className");
            }
        } else {
            // Lim inn ny HTML

            var html = getHTMLFromRange(range);

            // Slett tags i den markerte teksten
            var re = /<SPAN.*?>/gi;
            html = html.replace(re, "");

            re = /<\/SPAN>/gi;
            html = html.replace(re, "");

            re = /<H?>/gi;
            html = html.replace(re, "");

            re = /<\/H?>/gi;
            html = html.replace(re, "");

            // Slett evt font tagger
            re = /<FONT.*?>/gi;
            html = html.replace(re, "");

            re = /<\/FONT>/gi;
            html = html.replace(re, "");

            if (cssClass != "normal") {
                pasteHTML(editor,'<SPAN class="' + cssClass + '">' + html + '</SPAN>');
            } else {
                pasteHTML(editor, html);
            }
        }

        if (browser.isIE5up) {
            editor.document.recalc(true);
        }
    }
}


function rtSetStyle(editorId, list) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) {
        list.selectedIndex = 0;
        return;
    }

    var cssClass = list.options[list.selectedIndex].value;
    if (cssClass == "") {
        list.selectedIndex = 0;
        return;
    } else {
        var range = getRange(editor);
        if (range) {
            var node = getNodeFromRange(range);
            if (node) {
                if (cssClass.indexOf("tabell") != -1 || node.tagName == "TD" || node.tagName == "TR") {
                    rtSetTableStyle(editor, cssClass);
                } else {
                    rtSetTextStyle(editor, cssClass);
                }
            }
        }
    }
    list.selectedIndex = 0;
}


function rtSetTextFormat(editorId, list) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) {
        list.selectedIndex = 0;
        return;
    }

    var tag = list.options[list.selectedIndex].value;
    if (tag != "") {
        if (browser.isGecko) {
            editor.contentWindow.document.execCommand('useCSS',false, true);
        }

        editor.contentWindow.document.execCommand("formatblock", false, "<" + tag + ">");
    }
    list.selectedIndex = 0;
}


function rtFormatText(editorId, cmd) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    if (browser.isGecko) {
        editor.contentWindow.document.execCommand('useCSS',false, true);
    }

    editor.contentWindow.document.execCommand(cmd, false, '');
}


function rtSplitCell(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    var range = getRange(editor);
    var selectedCell = null;
    if (range) {
        selectedCell = findCell(getNodeFromRange(range));
    }
    if (selectedCell) {
        var selectedRow = selectedCell.parentNode;
        if (selectedCell.colSpan > 1) {
            selectedCell.colSpan--;
            var newCell = editor.contentWindow.document.createElement("TD");
            var cellContent = editor.contentWindow.document.createTextNode("\u00a0");
            newCell.insertBefore(cellContent, null);
            newCell.className = selectedCell.className;
            selectedRow.insertBefore(newCell, selectedCell);
        }
    }
}


function rtMergeCells(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    var range = getRange(editor);
    var selectedCell = null;
    if (range) {
        selectedCell = findCell(getNodeFromRange(range));
    }
    if (selectedCell) {
        var selectedRow = selectedCell.parentNode;
        var cellNo = -1;
        for (var i = 0; i < selectedRow.childNodes.length; i++) {
            if (selectedRow.childNodes[i] == selectedCell) {
                cellNo = i;
                break;
            }
        }
        if (cellNo < selectedRow.childNodes.length-1) {
            selectedCell.colSpan++;
            selectedRow.deleteCell(cellNo+1);
        }
    } else {
        alert("<kantega:label key="aksess.js.advarsel.plassericelle"/>");
    }

}

function rtEditTable(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    var range = getRange(editor);
    var selectedTable = null;
    if (range) {
        selectedTable = findTable(getNodeFromRange(range));
    }
    if (selectedTable) {
        focusField = selectedTable;
        var tablewin = window.open("<%=request.getContextPath()%>/admin/popups/inserttable.jsp?edit=true&refresh=" + getRefresh(), "tableWindow", "dependent,toolbar=no,width=480,height=116,resizable=yes");
        tablewin.focus();
    } else {
        alert("<kantega:label key="aksess.js.advarsel.plasseritabellredigere"/>");
    }
}


function rtInsertRow(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    var range = getRange(editor);
    var selectedCell = null;
    if (range) {
        selectedCell = findCell(getNodeFromRange(range));
    }
    if (selectedCell) {
        var selectedRow = selectedCell.parentNode;
        var selectedTable = selectedRow.parentNode;

        var rowPosition = -1;
        for (var i = 0; i < selectedTable.childNodes.length; i++) {
            if (selectedTable.childNodes[i] == selectedRow) {
                rowPosition = i;
            }
        }
        if (rowPosition != -1) {
            var newRow = selectedTable.insertRow(rowPosition+1);
            newRow.className = selectedRow.className;
            for (var i = 0; i < selectedRow.childNodes.length; i++) {
                for (var j = 0; j < selectedRow.childNodes[i].colSpan; j++) {
                    var newCell = editor.contentWindow.document.createElement("TD");
                    var cellContent = editor.contentWindow.document.createTextNode("\u00a0");
                    // Legger inn samme stiler som den valgte linja
                    newCell.className = selectedRow.childNodes[i].className;
                    newCell.insertBefore(cellContent, null);
                    newRow.insertBefore(newCell, null);
                }
            }
        }
    }
}


function rtInsertColumn(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    var range = getRange(editor);
    var selectedCell = null;
    if (range) {
        selectedCell = findCell(getNodeFromRange(range));
    }
    if (selectedCell) {
        var selectedRow = selectedCell.parentNode;
        var selectedTable = selectedRow.parentNode;

        var cellPosition = -1;
        for (var i = 0; i < selectedRow.childNodes.length; i++) {
            cellPosition += selectedRow.childNodes[i].colSpan;
            if (selectedRow.childNodes[i] == selectedCell) {
                break;
            }
        }

        if (cellPosition != -1) {
            for (var i = 0; i < selectedTable.childNodes.length; i++) {
                var row = selectedTable.childNodes[i];
                var currentCellPosition = -1;
                for (j = 0; j < row.childNodes.length; j++) {
                    currentCellPosition += row.childNodes[j].colSpan;
                    if (currentCellPosition == cellPosition) {
                        var newCell = row.insertCell(j+1);
                        var cellContent = editor.contentWindow.document.createTextNode("\u00a0");
                        newCell.insertBefore(cellContent, null);
                        break;
                    } else if (currentCellPosition > cellPosition) {
                        row.childNodes[j].colSpan++;
                        break;
                    }
                }
            }
        }
    }
}


function rtDeleteRow(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    var range = getRange(editor);
    var selectedCell = null;
    if (range) {
        selectedCell = findCell(getNodeFromRange(range));
    }
    if (selectedCell) {
        var selectedRow = selectedCell.parentNode;
        var selectedTable = selectedRow.parentNode;

        selectedTable.removeChild(selectedRow);
    }
}


function rtDeleteColumn(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    var range = getRange(editor);
    var selectedCell = null;
    if (range) {
        selectedCell = findCell(getNodeFromRange(range));
    }

    if (selectedCell) {
        var selectedRow = selectedCell.parentNode;
        var selectedTable = selectedRow.parentNode;

        var cellPosition = -1;
        for (var i = 0; i < selectedRow.childNodes.length; i++) {
            cellPosition += selectedRow.childNodes[i].colSpan;
            if (selectedRow.childNodes[i] == selectedCell) {
                break;
            }
        }
        if (cellPosition != -1) {
            for (var i = 0; i < selectedTable.childNodes.length; i++) {
                var row = selectedTable.childNodes[i];
                var currentCellPosition = -1;
                for (j = 0; j < row.childNodes.length; j++) {
                    currentCellPosition += row.childNodes[j].colSpan;
                    if (currentCellPosition >= cellPosition) {
                        if (row.childNodes[j].colSpan > 1) {
                            row.childNodes[j].colSpan--;
                        } else {
                            row.deleteCell(j);
                        }
                        break;
                    }
                }
            }
        }
    }
}


function rtInsertChar(editorId) {
    focusField = document.getElementById(editorId);

    var charwin = window.open("<%=request.getContextPath()%>/admin/popups/insertchar.jsp?refresh=" + getRefresh(), "charWindow", "dependent,toolbar=no,width=410,height=256,resizable=yes");
    charwin.focus();
}


function rtInsertTable(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    focusField = editor;

    var tablewin = window.open("<%=request.getContextPath()%>/admin/popups/inserttable.jsp?refresh=" + getRefresh(), "tableWindow", "dependent,toolbar=no,width=480,height=256,resizable=yes");
    tablewin.focus();
}


function rtInsertLink(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    focusField = editor;

    var url = "";
    var anchor = "";
    var openInNewWindow = false;

    var range = getRange(editor);
    if (range) {
        var node = getNodeFromRange(range);
        if (node && node.tagName == "A") {
            if (node.href) {
                url = node.href;
                if (url.indexOf('#') != -1) {
                    anchor = url.substring(url.indexOf('#') + 1, url.length);
                    url = url.substring(0, url.indexOf('#'));
                }
                url = escape(url);
                anchor = escape(anchor);
            }

            var onClick = node.onClick;

            if (onClick && onClick.indexOf("window.open") != -1) {
                openInNewWindow = true;
            }
        }
    }

    doInsertTag = true;

    var linkwin = window.open("<%=request.getContextPath()%>/admin/popups/insertlink.jsp?url=" + url + "&anchor=" + anchor + "&openInNewWindow=" + openInNewWindow + "&refresh=" + getRefresh(), "linkSelectorWindow", "dependent,toolbar=no,width=380,height=260,resizable=no,scrollbars=no");
    linkwin.focus();
}


function rtInsertAnchor(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    if (browser.isIE) {
        var sel = editor.contentWindow.document.selection.createRange();
        if (editor.contentWindow.document.selection.type!="None") {
            alert("<kantega:label key="aksess.js.advarsel.settinnbokmerke"/>");
            return;
        }
    }

    var name = prompt('<kantega:label key="aksess.js.advarsel.navnbokmerke"/>', '');
    if (name != null && name != "") {
        name = name.replace("�", "a");   name = name.replace("�", "A");
        name = name.replace("�", "o");   name = name.replace("�", "O");
        name = name.replace("�", "a");   name = name.replace("�", "A");
        name = name.replace("�", "a");   name = name.replace("�", "A");
        name = name.replace("�", "o");   name = name.replace("�", "O");

        name = name.toLowerCase();
        name = name.replace(/^\s+|\s+$/g, '');   // trim
        name = name.split(' ').join('_');

        var regexFirstCharacter = new RegExp("[A-Za-z]");   // "id" and "name" attributes must begin with a letter
        if (!name.substring(0, 1).match(regexFirstCharacter)) {
            name = "b_" + name;
        }

        for (var i = 0; i < name.length; i++) {
            // Replace illegal characters with hyphens ("-")
            var regexNameAndId = new RegExp("[A-Za-z0-9-_:.]");   // http://www.w3.org/TR/html401/types.html#h-6.2
            if (!name.substring(i,1).match(regexNameAndId)) {
                name = name.replace(name.substring(i,1), "-");
            }
        }

        pasteHTML(editor, '<img src="<%=request.getContextPath()%>/admin/bitmaps/common/placeholder/anchor.gif" name="' + name + '">');
    }
}


function rtInsertMedia(editorId) {
    var editor = document.getElementById(editorId);

    if (!checkEditorMode(editor)) return;

    focusField = editor;

    doInsertTag = true;

    var id = "";

    var range = getRange(editor);
    if (range) {
        var node = getNodeFromRange(range);
        if (node && node.tagName == "IMG" && node.src) {
            var url = node.src;
            if (url.indexOf("id=") != -1) {
                id = url.substring(url.indexOf("id=") + 3, url.length);
            }
        }
    }

    var mmwin = window.open("<%=request.getContextPath()%>/admin/multimedia/?id=" + id + "&refresh=" + getRefresh(), "mmWindow", "toolbar=no,width=780,height=450,resizable=yes,scrollbars=yes");
    mmwin.focus();
}


function rtReplace(editorId) {
    var editor = document.getElementById(editorId);

    editor.focus();

    focusField = editor;

    var replacewin = window.open("<%=request.getContextPath()%>/admin/popups/replacetext.jsp?refresh=" + getRefresh(), "replaceWindow", "dependent,toolbar=no,width=480,height=256,resizable=yes");
    replacewin.focus();
}
