/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var oPopup = null;

if (window.createPopup) {
    // IE popup
    oPopup  = window.createPopup();
}

var autocompleteTimerId = -1;

var acBackgroundColor = '#ffffff';
var acItemColor = 'black';
var acItemBackgroundColor = 'white';
var acItemOverColor = 'white';
var acItemOverBackgroundColor = 'black';

var Autocomplete = function() {
    this.height = 150;
    this.minChars = 3;
}


function acInsertAfter(target, newNode) {
    var parent   = target.parentNode;
    var refChild = target.nextSibling;

    if(refChild != null) {
       parent.insertBefore(newNode, refChild);
    } else {
       parent.appendChild(newNode);
    }
}

function acNavigate(id, row, scroll) {
    var ac = acList[id];
    if (row < 0) {
        ac.selectedRow = 0;
    } else if (row >= ac.numberOfElements) {
        ac.selectedRow = ac.numberOfElements - 1;
    } else {
        ac.selectedRow = row;
    }

    var d = document;
    if (oPopup) {
        d = oPopup.document;
    }


    for (var i = 0; i < ac.numberOfElements; i++) {
        var item = d.getElementById(ac.id + "_value_" + i);
        if (i == ac.selectedRow) {
            item.style.backgroundColor = acItemOverBackgroundColor;
            item.style.color = acItemOverColor;
            if (scroll) item.scrollIntoView();
        } else {
            item.style.backgroundColor = acItemBackgroundColor;
            item.style.color = acItemColor;
        }
    }
    return d.getElementById(ac.id + "_value_" + ac.selectedRow);
}

function acHidePopup(id) {
    if (!oPopup) {
        var list = document.getElementById(id + 'divlist');
        if (list) {
            list.style.display = 'none';
        }
    }
}


function acSelectValue(id, row) {
    var ac = acList[id];

    var d = document;
    if (oPopup) {
        d = oPopup.document;
    }

    var key = d.getElementById(ac.id + "_value_" + row + "_key").value;
    var txt = d.getElementById(ac.id + "_value_" + row + "_txt").value;

    if (ac.callback) {
        eval(ac.callback + "('" + id + "', '" + key + "','" + txt + "')");
    } else {
        ac.inputField.value = txt;
        if (ac.hiddenField) {
            ac.hiddenField.value = key;
        }
    }

    if (oPopup) {
        oPopup.hide();
    } else {
        acHidePopup(ac.id);
    }
}


function acCreatePopup(ac) {
    var width = 200;
    var offsetY = 25;
    if (ac.inputField.style) {
        if (ac.inputField.style.width) width = parseInt(ac.inputField.style.width, 10);
        if (ac.inputField.style.height) offsetY = parseInt(ac.inputField.style.height, 10);
    }

    if (oPopup) {
        var html = "<div id=\"" + ac.id + "divlist\" style=\"";
        html += "border: 1px solid black;";
        html += "height: 150px; ";
        html += "padding: 4px;";
        html += "overflow:auto;";
        html += "overflow-x:hidden;";
        html += "width:" + width + "px;";
        html += "font-family: Verdana, Arial;";
        html += "font-size: 11px;";
        html += "height:" + ac.height + "px;";
        html += "background-color:" + acBackgroundColor + ";";
        html += "\"></div>";
        oPopup.document.body.innerHTML = html;
        oPopup.show(0, offsetY, width, ac.height, ac.inputField);
        return oPopup.document.getElementById(ac.id + 'divlist');
    } else {
        var list = document.getElementById(ac.id + 'divlist');
        if (list == null) {
            // Linjeskift etter element
            var newBR = document.createElement('BR');
            acInsertAfter(ac.inputField, newBR);

            // DIV for innholdet
            list = document.createElement('DIV');
            list.setAttribute('id', ac.id  + 'divlist');
            list.style.height = '150px';
            list.style.position = 'absolute';
            list.style.overflow = 'auto';
            list.style.borderStyle = 'solid';
            list.style.borderWidth = '1px';
            list.style.padding = '4px';
            list.style.backgroundColor = acBackgroundColor;
            if (ac.inputField.style && ac.inputField.style.width) {
                list.style.width = ac.inputField.style.width;
            }
            list.style.zindex = '100';
            list.style.visible = 'false';
            acInsertAfter(newBR, list);
        } else {
             list.style.display = 'block';
        }
    }

    return list;
}



function acGetValues(id) {
    var ac = acList[id];

    var xmlhttp=false;

    try {
        xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
        try {
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        } catch (E) {
            xmlhttp = false;
        }
    }

    if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
        xmlhttp = new XMLHttpRequest();
    }

    xmlhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");

    if (ac.inputField) {
        var val = ac.inputField.value;
        var q = "";
        if (ac.url.indexOf("?") != -1) {
            q = ac.url.substring(ac.url.indexOf("?") + 1, ac.url.length) + "&";
            ac.url = ac.url.substring(0, ac.url.indexOf("?"));
        }
        xmlhttp.open("POST",  ac.url, true);
        xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
        xmlhttp.onreadystatechange=function() {
            if (xmlhttp.readyState==4) {
                acFillList(ac, xmlhttp.responseXML)
            }
        }
        xmlhttp.send(q + "key=" + ac.id + "&value=" + val);
    }
}

function acMouseOut(item) {
    item.style.backgroundColor = acItemBackgroundColor;
    item.style.color = acItemColor;
}

function acFillList(ac, xml) {
    var values = xml.getElementsByTagName("value");

    if (values) {
        var popup = acCreatePopup(ac);
        var html = "";
        var style = "padding-left: 0px; padding-right: 0px; cursor: pointer; color:" + acItemColor + ";background-color:" + acItemBackgroundColor;
        for (var i = 0; i < values.length; i++) {
            var v = values[i];
            var key = v.getAttribute("key");
            var txt = v.firstChild.nodeValue;
            if (!key) {
                key = txt;
            }
            txt = txt.split("'").join("");
            var id = ac.id + "_value_" + i;
            html += '<input type=hidden id="' + id + '_key" name="' + id + '_key" value="' + key + '">';
            html += '<input type=hidden id="' + id + '_txt" name="' + id + '_txt" value="' + txt + '">';

            if (oPopup) {
                html += "<div id=\"" + id + "\" onclick=\"parent.acSelectValue('" + ac.id + "', '" + i + "')\" onmouseover=\"parent.acNavigate('" + ac.id + "', '" + i + "', false)\" onmouseout=\"parent.acMouseOut(this)\" style=\"" + style + "\">" + txt + "</div>";
            } else {
                html += "<div id=\"" + id + "\" onclick=\"acSelectValue('" + ac.id + "', '" + i + "')\" onmouseover=\"acNavigate('" + ac.id + "', '" + i + "', false)\" onmouseout=\"acMouseOut(this)\" style=\"" + style + "\">" + txt + "</div>";
            }
        }
        popup.innerHTML = html;
        ac.numberOfElements = values.length;
    }
}


Autocomplete.setup = function(params) {
    var ac = new Autocomplete();
    if (params.inputField) {
        ac.id = params.inputField;
        if (document.getElementById(ac.id + "text")) {
            ac.inputField =  document.getElementById(ac.id + "text");
            ac.hiddenField =  document.getElementById(ac.id);
        } else {
            ac.inputField =  document.getElementById(ac.id);
            ac.hiddenField = null;
        }

    }
    ac.selectedRow = -1;

    if (params.url) {
        ac.url = params.url;
    }
    if (params.minChars) {
        ac.minChars = params.minChars;
    }

    if (params.callback) {
        ac.callback = params.callback;
    }
    if (params.height) {
        ac.height = params.height;
    }

    if (ac.inputField != null) {
        acList[ac.id] = ac;
        ac.inputField.setAttribute("autocomplete", "off");

        ac.inputField.onkeyup = function(e) {
            if(!e) {
               e = window.event;
            }

            var keyPressed = 0;
            if(e.which) {
               keyPressed = e.which;
            } else if(e.keyCode) {
               keyPressed = e.keyCode;
            } else if(e.charCode) {
               keyPressed = e.charCode
            }

            switch (keyPressed) {
                case 27:
                    // Esc
                    acHidePopup(ac.id);
                    break;
                case 13:
                    // Enter
                    if (ac.selectedRow > 0) {
                        acSelectValue(ac.id, ac.selectedRow);
                    }
                    break;
                case 38:
                    // Up
                    acNavigate(ac.id, ac.selectedRow - 1, true);
                    break;
                case 40:
                    // Down
                    acNavigate(ac.id, ac.selectedRow + 1, true);
                    break;
                default:
                    if (autocompleteTimerId != -1) {
                        clearInterval(autocompleteTimerId);
                    }
                    if (ac.hiddenField) {
                        // Nullstill kodefelt hvis brukeren har endret / visket ut
                        ac.hiddenField.value = "";
                    }
                    if (ac.inputField.value.length >= ac.minChars) {
                        autocompleteTimerId = setTimeout("acGetValues('" + ac.id + "')", 500);
                    } else {
                        acHidePopup(ac.id);
                    }
            }

        }

        ac.inputField.onblur = function (e) {
            setTimeout("acHidePopup('" + ac.id + "')", 3000);
        }
    }
}


var acList = new Array();