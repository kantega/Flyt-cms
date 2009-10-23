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

var focusField = null;

var doInsertTag  = false;

document.onkeypress = editKeyPress;

/*
 *  Update status set that page has been edited
 */
function setIsUpdated() {
    try {
        document.myform.isModified.value = "true";
    } catch (e) {
    }
}


function editKeyPress(e) {
    if (focusField != null) {
        setIsUpdated();
    }
    return true;
}


/*
 *  Sets field as focused element
 */
function setFocusField(field) {
   focusField = field;
}


function blurField() {
   focusField = null;
}


/*
 *  Used in URLs to force refresh
 */
function getRefresh() {
   var dt = new Date();
   return "" + dt.getTime();
}


/*
 *  Popup window for forms
 */
function editForm(formElement) {
    var id = formElement.value;
    if (id) {
        var formwin = window.open("../forms/index.jsp?showform=" + id, "formWindow", "toolbar=no,width=800,height=450,resizable=yes,scrollbars=yes");
    }
    formwin.focus();
}

/*
 *  Popup window for selecting a page url
 */
function selectContentUrl(formElement) {
   focusField = formElement;
   doInsertTag = true;
   var contentwin = window.open("../popups/selectcontent.jsp?refresh=" + getRefresh(), "contentWindow", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
   contentwin.focus();
}


/*
 *  Popup vindu for selecting a page id
 */
function selectContent(formElement, maxItems) {

    var items = 0;

    if (arguments.length < 2) {
        maxItems = 1;
    } else {
        var list = eval("document.myform." + formElement.name + "list");
        if(list) {
            items = list.length;
        }
    }

    if (items >= maxItems) {
        alert("<kantega:label key="aksess.js.advarsel.dukanmaksimaltvelge"/> " + maxItems + " <kantega:label key="aksess.js.advarsel.elementer"/>");
    } else {
        focusField = formElement;
        doInsertTag = false;
        var contentwin = window.open("../popups/selectcontent.jsp?refresh=" + getRefresh(), "contentWindow", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
        contentwin.focus();
    }
}


/*
 *  Popup window for date
 */
function selectDate(formElement) {
    focusField = formElement;
    doInsertTag = false;
    var calwin = window.open("../popups/calendar.jsp?refresh=" + getRefresh(), "calWindow", "toolbar=no,width=300,height=200,resizable=yes,scrollbars=yes");
    calwin.focus();
}

/*
 *  Popup window for selecting a user
 */
function selectUser(formElement) {
    focusField = formElement;
    doInsertTag = false;
    var userwin = window.open("../security/addroleoruser.jsp?select=true&roletype=user&refresh=" + getRefresh(), "usrWindow", "toolbar=no,width=400,height=300,resizable=yes,scrollbars=no");
    userwin.focus();
}

/*
 *  Popup window for selecting a organizational unit
 */
function selectOrgunit(formElement) {
    focusField = formElement;
    doInsertTag = false;
    var orgwin = window.open("../popups/selectorgunit.jsp?refresh=" + getRefresh(), "usrWindow", "toolbar=no,width=300,height=300,resizable=yes,scrollbars=no");
    orgwin.focus();
}

/*
*  Popup window for selecting a category
*/
function selectCategory(formElement) {
    focusField = formElement;
    doInsertTag = false;
    var catwin = window.open("../popups/selectcategory.jsp?refresh=" + getRefresh(), "usrWindow", "toolbar=no,width=300,height=300,resizable=yes,scrollbars=no");
    catwin.focus();
}

/*
 *  Popup window for selecting media object
 */
function selectMultimedia(formElement, filter) {
    focusField = formElement;
    var id = -1;
    if (focusField.value != "") {
        id = focusField.value;
    }

    doInsertTag = false;
    var mmwin = window.open("../multimedia/?id=" + id + "&filter=" + filter + "refresh=" + getRefresh(), "mmWindow", "toolbar=no,width=880,height=620,resizable=yes,scrollbars=yes");
    mmwin.focus();
}


/*
 * Popup window for selecting media folder
 */
function selectMediaFolder(formElement) {
    focusField = eval(formElement);
    doInsertTag = false;
    var mmwin = window.open("../popups/selectmediafolder.jsp?refresh=" + getRefresh(), "mmWindow", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
    mmwin.focus();
}

/*
 * Popup window for selecting a topic
 */

function selectTopic(formElement) {
   focusField = formElement;
   var topicwin = window.open("../topicmaps/index.jsp?refresh=" + getRefresh(), "topicwin",  "toolbar=no,width=880,height=500,resizable=yes,scrollbars=yes");
   topicwin.focus();
}

/*
 * Popup window for selecting a role
 */
function selectRole(formElement) {
    focusField = formElement;
    var rolewin = window.open("../security/addroleoruser.jsp?select=true&roletype=Role&refresh=" + getRefresh(), "roleWindow", "toolbar=no,width=400,height=300,resizable=yes,scrollbars=no");
    rolewin.focus();
}


/*
 * Callback for adding a topic
 */
function addTopic(topicMap, topicId, topicName) {
    setIsUpdated();

    if (focusField == null) {
        window.parent.location = "../publish/AddContentTopic.action?topicMapId=" + topicMap + "&topicId=" + topicId;
    } else {
        // Topic should be inserted into a input field / list
        insertIdAndValueIntoForm(topicMap + ":" + topicId, topicName);
    }
}


/*
 * Popup for adding a list option used for editablelists
 */
function addListOption(formElement, attributeKey, language) {
    var optionwin = window.open("../popups/addlistoption.jsp?attributeKey=" + escape(attributeKey) + "&language=" + language, "optionwin", "toolbar=no,width=280,height=120,resizable=no,scrollbars=no");
    optionwin.focus();
    focusField = formElement;
}


/*
 * Adds option to select list
 */
function insertOptionIntoList(value) {
    var option = document.createElement("option");
    option.value = value; option.text = value;

    for(var i = 0; i < focusField.options.length; i++) {
        var o = focusField.options[i];
        if(value < o.value) {
            try {
                focusField.add(option, focusField.options[i]);
            } catch(ex) { // IE
                focusField.add(option, i);

            }
           break;
        } else if(i == focusField.options.length -1) {
            focusField.add(option);
            break;
        }

    }
    option.selected = true;
}

/*
 * Remove list option from list
 */
function removeOptionFromList(formElement, attributeKey, language) {
    var xmlhttp = getXmlHttp();
    xmlhttp.open("POST",  "../publish/RemoveListOption.action", true);
    xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    xmlhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xmlhttp.onreadystatechange=function() {
        if (xmlhttp.readyState==4) {
            if(xmlhttp.responseText == "success") {
                for (var i=0; i < formElement.options.length; i++) {
                    if (formElement.options[i].selected) {
                        formElement.options[i] = null;
                        setIsUpdated();
                    }
                }
            }
            else {
                alert("<kantega:label key="aksess.js.advarsel.fjernvalgfraliste"/>");
            }
        }
    }
    xmlhttp.send("value=" + formElement.value + "&attributeKey=" + attributeKey + "&language=" + language);
}


/*
 *   Insert tag into text
 */
function insertTag(tag) {
    setIsUpdated();

    if (focusField != null) {
        var fname = "" + focusField.name;
        if (fname.indexOf("editor_") != -1) {
            // Rich texteditor
            pasteHTML(focusField, tag);
        } else {
            // Normal form field
            if (focusField.storedRange) {
                focusField.storedRange.text = tag;
            } else {
                focusField.value += tag;
            }
        }
    }
}


/*
 * Create link using Rich Text editor
 */
function createLink(url) {
    setIsUpdated();
    if (focusField != null) {
        focusField.contentWindow.document.execCommand("CreateLink", false, url);
    }
}


/*
 * Insert id and value into form field
 */
function insertIdAndValueIntoForm(id, text) {
    setIsUpdated();

    if (focusField != null) {
        var name = focusField.name;
        var type = focusField.type;


        var textField = document.myform.elements[name + 'text'];
        var listField = document.myform.elements[name + 'list'];
        if (listField) {
            // Field is a list
            var found = false;
            // Check if not added before
            for(var i = 0; i < listField.options.length; i++) {
                var val = listField.options[i].value ;
                if(val == id) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                listField.options[listField.options.length] = new Option(text, id, 0, 0);
                focusField.value = getEntriesFromList(listField);
            }
        } else if (textField) {
            // Is textfield
            focusField.value = "" + id;
            textField.value = text;
        } else {
            focusField.value = "" + id;
        }
    }
}


/*
 * Remove id and value from form
 */
function removeIdAndValueFromForm(field) {
    var text = document.myform.elements[field.name + 'text'];
    var list = document.myform.elements[field.name + 'list'];

    if (text) {
        setIsUpdated();
        field.value = "";
        text.value = "";
    } else if (list) {
        for (var i=0; i < list.options.length; i++) {
            if (list.options[i].selected) {
                list.options[i] = null;
                setIsUpdated();
            }
        }

        // Oppdaterer hidden felt med riktig verdi
        field.value = getEntriesFromList(list);
    } else {
        field.value = "";
    }
}



/*
 *  Move element in select list up or down
 */
function moveId(field, dir) {
    var list = document.myform.elements[field.name + 'list'];

    for (var i=0; i < list.options.length; i++) {
        if (list.options[i].selected) {
            if (dir < 0) {
                if (i == 0) {
                    return;
                }
            } else if (dir > 0){
                if (i == list.options.length - 1) {
                    return;
                }
            }

            var tmpText  = list.options[i+dir].text;
            var tmpValue = list.options[i+dir].value;

            list.options[i+dir].text  = list.options[i].text;
            list.options[i+dir].value = list.options[i].value;

            list.options[i].text  = tmpText;
            list.options[i].value = tmpValue;

            list.options[i+dir].selected = true;

            setIsUpdated();
            field.value = getEntriesFromList(list);

            return;
        }
    }
}

/*
 * Get elements from select list as string
 */
function getEntriesFromList(list) {
    var entries = "";

    for (var i=0; i < list.options.length; i++) {
        if (i > 0) {
            entries += ",";
        }
        entries += list.options[i].value;
   }
   return entries;
}


/*
 * Insert value into form
 */
function insertValueIntoForm(val) {
    setIsUpdated();
    if (focusField != null) {
        var fname = "" + focusField.name;
        if (fname.indexOf("editor_") != -1) {
            var range = getRange(focusField);
            if (!hasSelection(focusField)) {
                pasteHTML(focusField, val);
            } else {
                focusField.contentWindow.document.execCommand("CreateLink", false, url);
            }
        } else {
            // Normal form field
            focusField.value = "" + val;
        }
    }
}

/*
 * Remove value from form
 */
function removeValueFromForm(field)
{
    setIsUpdated();
    field.value = "";
}


/*
 * Set flag indicating attachment should be deleted
 */
function removeAttachment(element) {
    setIsUpdated();
    var name = element.name;
    var field = eval('document.myform.delete_' + name);
    field.value = "1";
}


/*
 * Replaces text in HTML editor
 */
function replaceString(search, replace) {
    setIsUpdated();
    if (focusField != null) {
        if (focusField.HTMLmode) {
            var tmp = "" + focusField.contentWindow.document.body.innerText;
            tmp = tmp.split(search).join(replace);
            focusField.contentWindow.document.body.innerText = tmp;
        } else {
            var tmp = focusField.contentWindow.document.body.innerHTML;

            tmp = tmp.split(search).join(replace);
            focusField.contentWindow.document.body.innerHTML = tmp;
        }
    }
}


/*
 * Get range from text selection in editor
 */
function getRange(editor) {
    var range = null;
    if (browser.isIE) {
        range = editor.contentWindow.document.selection.createRange();
    } else {
        var sel = editor.contentWindow.getSelection();
        if (sel.rangeCount > 0) {
            range = sel.getRangeAt(0);
        }
    }

    return range;
}


/*
 * Check if editor has selection
 */
function hasSelection(editor) {
    if (browser.isIE) {
        var range = editor.contentWindow.document.selection.createRange();
        if (editor.document.selection.type != "None") {
            return true;
        }
    } else {
        var sel = editor.contentWindow.getSelection();
        if (sel.rangeCount > 0) {
            return true;
        }
    }

    return false;
}

/*
 * Gets HTML in range
 */
function getHTMLFromRange(range) {
    if (browser.isIE) {
        return range.htmlText;
    } else {
        return new XMLSerializer().serializeToString(range.cloneContents());
    }
}

/*
 * Paste HTML in HTML text field
 */
function pasteHTML(field, tag) {
    if (browser.isIE) {
        var range = field.contentWindow.document.selection.createRange();
        var node = getNodeFromRange(range);
        try {
            // If user has selected image/link, must delete old image/link before inserting new
            if (node.tagName == "IMG" || node.tagName == "A" || node.tagName == "OBJECT") {
                var parent = node.parentNode;
                parent.removeChild(node);
                range = field.contentWindow.document.selection.createRange();
            }

            range.pasteHTML(tag);
        } catch (e) {

        }
    } else {
        var selection = field.contentWindow.window.getSelection();
        field.contentWindow.focus();

        var range;
        if (selection) {
            range = selection.getRangeAt(0);
        } else {
            range = field.contentWindow.document.createRange();
        }

        var fragment = field.contentWindow.document.createDocumentFragment();
        var div = field.contentWindow.document.createElement("div");
        div.innerHTML = tag;

        while (div.firstChild) {
            fragment.appendChild(div.firstChild);
        }

        selection.removeAllRanges();
        range.deleteContents();

        var node = range.startContainer;
        var pos = range.startOffset;

        if (node.nodeType == 3) {
            // Text node
            if (fragment.nodeType == 3) {
                // Text node
                node.insertData(pos, fragment.data);
                range.setEnd(node, pos + fragment.length);
                range.setStart(node, pos + fragment.length);
            } else {
                node = node.splitText(pos);
                node.parentNode.insertBefore(fragment, node);
                range.setEnd(node, pos + fragment.length);
                range.setStart(node, pos + fragment.length);
            }
        } else if (node.nodeType == 1) {
            // Element node
            node = node.childNodes[pos];
            node.parentNode.insertBefore(fragment, node);
            range.setEnd(node, pos + fragment.length);
            range.setStart(node, pos + fragment.length);
        }
        selection.addRange(range);
    }
}


/*
 *
 */
function getNodeFromRange(range) {
    var node;
    if (browser.isIE) {
        if (range.item) {
            node = range.item(0);
        } else {
            node = range.parentElement();
        }
    } else {
        node = range.commonAncestorContainer;
        if (node.tagName == "TR") {
            return node;
        }

        if (!range.collapsed && range.startContainer == range.endContainer) {
            if (range.startOffset - range.endOffset < 1 && range.startContainer.hasChildNodes()) {
                // When image etc is selected
                node = range.startContainer.childNodes[range.startOffset];
            }
        }

        if (node.nodeType == 3) {
            // Text node, return parent
            node = node.parentNode;
        }
    }

    return node;
}

function clearDefaultValue(field, defaultValue) {
    if (field.value == defaultValue) {
        field.value = '';
    }
}
function setDefaultValue(field, defaultValue) {
    if (field.value == '') {
        field.value = defaultValue;
    }
}

function toggleCheckbox(cb, value) {
    for (var i=0; i < cb.length; i++) {
        if (cb[i].value == value) {
            cb[i].checked = !cb[i].checked;
            break;
        }
    }
}