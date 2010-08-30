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

var activeId = null;
var menuEnabled = false;

function treeNodeId(id, uniqueId, type) {
    this.id = id;
    this.uniqueId = uniqueId;
    this.type = type;
}


function toogleSubTree(id) {
    var tree = document.getElementById("tree_" + id);
    var icon = document.getElementById("img_" + id);
    if (id != null) {
        if (tree.style.display == 'none') {
            tree.style.display = 'block';
            if (icon) {
                var src = "" + icon.src;
                src = src.replace('closed', 'open');
                icon.src = src;
            }
            setFolderOpen(id);
        } else {
            setFolderClosed(id);
            tree.style.display = 'none';
            if (icon) {
                var src = "" + icon.src;
                src = src.replace('open', 'closed');
                icon.src = src;
            }
        }
    }
}


function updateTree() {
    // Angir ikke focus, dette blir da bestemt av sesjon
    document.tree.focusId.value = -1;
    document.tree.submit();
}


function setFolderOpen(id) {
    var newList = document.tree.openFolders.value;
    var openList = newList.split(",");

    for (var i = 0; i < openList.length; i++) {
        var current = openList[i]
        if (id == current) {
            break;
        }
    }

    if (newList.length == 0) {
        newList = "" + id;
    } else {
        newList = newList + "," + id;
    }
    document.tree.openFolders.value = newList;
    if (window.parent && typeof window.parent.onFolderOpen == "function") {
        window.parent.onFolderOpen(id, newList);
    }
}


function setFolderClosed(id) {
    var openList = document.tree.openFolders.value.split(",");
    var newOpenList  = "";
    for (var i = 0; i < openList.length; i++) {
       var current = openList[i]
       if (id == current) {
          // Skip
       } else {
          if (newOpenList.length > 0) newOpenList += ",";
          newOpenList += current;
       }
    }
    document.tree.openFolders.value = newOpenList;
    if (window.parent && typeof window.parent.onFolderClose == "function") {
        window.parent.onFolderClose(id, newOpenList);
    }
}


function loadSubTree(id) {
    // Legg til i liste over åpne trær...
    setFolderOpen(id);
    document.tree.expand.value = "false";
    document.tree.focusId.value = id;
    document.tree.submit();
}


function setSort(sort) {
    document.tree.sort.value = sort;
    document.tree.submit();
}


function initTree() {
    var focusId = document.tree.focusId.value;
    if (focusId != -1) {
        var elm = document.getElementById("item_" + focusId);
        if (elm) {
            elm.scrollIntoView();
        }
    }
}

function mouseContextMenu(e)  {
    if (!e) {
        event.cancelBubble = true;
        event.returnValue = false;
    } else {
        e.cancelBubble = true
    }
    return false;
}

function mouseClick(e) {
	if (!e) var e = window.event;

    if (e.which) {
        if (e.which == 2 || e.which == 3) {
            e.cancelBubble = true;
            toggleContextMenu(e);
            return false;
        } else {
            if (isOutside(e)) hideContextMenu();
        }

    } else {
        if (e.button == 2 || e.button == 3) {
            e.cancelBubble = true;
            e.returnValue = false;
            toggleContextMenu(e);
            return false;
        } else {
            if (isOutside(e)) hideContextMenu();
        }
    }
    return true;
}


function isOutside(e) {
    var layer = document.getElementById("contextMenu");


    var x = 0;
    var y = 0;
    if (e.pageX || e.pageY) {
        x = e.pageX;
        y = e.pageY;
    } else if (e.clientX || e.clientY) {
        x = e.clientX + document.body.scrollLeft;
        y = e.clientY + document.body.scrollTop;
    }

    var layerX = parseInt(layer.style.left, 10);
    var layerY = parseInt(layer.style.top, 10);
    if ((x > layerX && x < layerX + layer.offsetWidth) &&
        (y > layerY && y < layerY + layer.offsetHeight)) {
        return false;
    }

    return true;
}


function toggleContextMenu(e) {
    var layer = document.getElementById("contextMenu");

    var x = 0;
    var y = 0;
	if (e.pageX || e.pageY) {
		x = e.pageX;
		y = e.pageY;
	} else if (e.clientX || e.clientY) {
		x = e.clientX + document.body.scrollLeft;
		y = e.clientY + document.body.scrollTop;

        if (y > document.body.scrollTop + document.body.clientHeight - layer.offsetHeight) {
            y = document.body.scrollTop + document.body.clientHeight - layer.offsetHeight;
        }
        if (x > document.body.scrollLeft + document.body.clientWidth - layer.offsetWidth) {
            x = document.body.scrollLeft + document.body.clientWidth - layer.offsetWidth;
        }
	}

    if (layer.style.visibility == "hidden") {
        if ((menuEnabled) && (document.tree.select.value != "true")) {

            layer.style.left = x;
            layer.style.top = y;

            layer.style.visibility = "visible";
        }

    } else {
        layer.style.visibility = "hidden";
    }
}

function hideContextMenu() {
    var layer = document.getElementById("contextMenu");
    layer.style.visibility = "hidden";
}

function menuItemOver(obj) {
    obj.prevClassName = obj.className;
    obj.className = "cMenuOver";
}

function menuItemOut(obj) {
    obj.className = obj.prevClassName;
}

function enableMenu(id, parent, type) {
    if (!menuEnabled) {
        menuEnabled = true;
        // Sjekk om menyen allerede vises
        var layer = document.getElementById("contextMenu");

        if (layer != null && layer.style.visibility == "hidden") {
            activeId = new treeNodeId(id, parent, type);

            // Vis riktig kontekstmeny
            var menus = document.getElementsByTagName("DIV");
            for (var i = 0; i < menus.length; i++) {
                var menu = menus[i];
                if (menu.id.indexOf("contextMenu_") != -1) {
                    menu.style.display = "none";
                }
            }

            var activeMenu = document.getElementById("contextMenu_" + type);
            if (activeMenu != null) {
                activeMenu.style.display = "block";
            }
        }
    }
}

function disableMenu() {
    menuEnabled = false;
}

function copyCutObject(isCopy) {
    hideContextMenu();

    if (activeId != null) {
        document.tree.isCopy.value = isCopy;
        document.tree.clipboard.value = activeId.id;

        setCssClass("cMenu_paste", "cMenu");
        setCssClass("cMenu_pasteshortcut", "cMenu");
    }
}



function pasteObject(isshortcut) {

    var shortCutStr = "";
    if (isshortcut != null && isshortcut) {
        shortCutStr = "&pasteShortCut=true";
    }


    hideContextMenu();

    if (activeId != null) {
        if (document.tree.clipboard.value == "") {
            alert("<kantega:label key="aksess.js.advarsel.copybeforepaste"/>");
        } else if (document.tree.clipboard.value == activeId.id) {
            alert("<kantega:label key="aksess.js.advarsel.copytoself"/>");
        } else {
            var clipboard = document.tree.clipboard.value;
            var isCopy    = document.tree.isCopy.value;

            // Reset clipboard
            document.tree.clipboard.value = "";
            setCssClass("cMenu_paste", "cMenuDisabled");
            setCssClass("cMenu_pasteshortcut", "cMenuDisabled");


            // Apne vindu for klipp og lim
            var copywin = window.open("ConfirmCopyPaste.action?clipboard=" + clipboard + "&newParentId=" + activeId.id + "&isCopy=" + isCopy + shortCutStr, "copyPaste", "toolbar=no,width=350,height=245,resizable=no,scrollbars=no");
            copywin.focus();

        }
    }
}


function setCssClass(id, clz) {
    var i = 0;
    var elem;
    while ((elem = document.getElementById(id + i))) {
        elem.className = clz;
        i++;
        elem = document.getElementById(id + i);
    }
}

if (window.Event) {
    document.captureEvents(Event.MOUSEDOWN);
}

document.oncontextmenu = mouseContextMenu;
document.onmousedown = mouseClick;
