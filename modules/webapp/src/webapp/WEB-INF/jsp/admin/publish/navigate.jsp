<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess"%>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%--
~ Copyright 2009 Kantega AS
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~  http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><kantega:label key="aksess.title"/></title>
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">

    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/reset.css">
    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/base.css">
    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/default.css">
    <!--[if lt IE 8]>
        <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/default_ie7.css">
    <![endif]-->

    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/jquery-<aksess:getconfig key="jquery.version" default="1.3.2"/>.min.js"></script>
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/jquery.dimensions.pack.js"></script>
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/jquery.interface.js"></script>
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/jquery.contextMenu.js"></script>
    <script type="text/javascript" src='<%=Aksess.getContextPath()%>/admin/dwr/interface/ContentStateHandler.js'></script>
    <script type="text/javascript" src='<%=Aksess.getContextPath()%>/admin/dwr/engine.js'></script>
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/admin.jjs"></script>
 
</head>
<body>






<div id="Top">
    <div id="TopMenu">
        <a class="logo" href="javascript:alert('not yet implemented');" title="<kantega:label key="aksess.title"/>">&nbsp;</a>
        <a href="#" class="menuitem"><kantega:label key="aksess.menu.mypage"/></a>
        <a href="#" class="menuitem"><kantega:label key="aksess.menu.administration"/></a>
        <a href="#" class="menuitem selected"><kantega:label key="aksess.menu.publish"/></a>
        <a href="#" class="menuitem"><kantega:label key="aksess.menu.multimedia"/></a>
        <a href="#" class="menuitem"><kantega:label key="aksess.menu.topicmaps"/></a>
        <a href="#" class="menuitem"><kantega:label key="aksess.menu.forms"/></a>
    </div>
    <div id="ModesMenu">
        <div class="buttonGroup">
            <a href="#" class="button"><span class="navigate"><kantega:label key="aksess.mode.navigate"/></span></a>
            <span class="buttonSeparator"></span>
            <a href="#" class="button last"><span class="organize"><kantega:label key="aksess.mode.organize"/></span></a>
        </div>
        <div class="buttonGroup">
            <a href="#" class="button"><span class="linkcheck"><kantega:label key="aksess.mode.linkcheck"/></span></a>
            <span class="buttonSeparator"></span>
            <a href="#" class="button"><span class="dummy">Dummy button</span></a>
            <span class="buttonSeparator"></span>
            <a href="#" class="button last"><span class="statistics"><kantega:label key="aksess.mode.statistics"/></span></a>
        </div>
        <div class="buttonGroup search">
            <form action="" method="get">
                <input type="text" class="query content" name="query content">
                <input type="submit" value="" title="<kantega:label key="aksess.search.submit"/>">
            </form>
        </div>
    </div>
    <div id="ToolsMenu">
       <div class="buttonGroup">
            <a href="#" class="button"><span class="newSubpage"><kantega:label key="aksess.tools.newSubpage"/></span></a>
            <a href="#" class="button"><span class="edit"><kantega:label key="aksess.tools.edit"/></span></a>
            <a href="#" class="button"><span class="delete"><kantega:label key="aksess.tools.delete"/></span></a>
        </div>
        <div class="buttonGroup">
            <a href="#" class="button"><span class="cut"><kantega:label key="aksess.tools.cut"/></span></a>
            <a href="#" class="button"><span class="copy"><kantega:label key="aksess.tools.copy"/></span></a>
            <a href="#" class="button"><span class="paste"><kantega:label key="aksess.tools.paste"/></span></a>
        </div>
        <div class="buttonGroup">
            <a href="#" class="button"><span class="displayPeriod"><kantega:label key="aksess.tools.displayPeriod"/></span></a>
            <a href="#" class="button"><span class="privileges"><kantega:label key="aksess.tools.privileges"/></span></a>
        </div>
    </div>
</div>

<div id="Content">
    <div id="Navigation">
        <div class="filteroptions">
            <a href="#" class="filteroption filter">Filtreringsvalg</a>
            <div class="filteroption">
                <input type="checkbox" id="filteroptionHideExpired">
                <label for="filteroptionHideExpired">Skjul utløpte</label>
            </div>
        </div>
        <div id="Navigator"></div>
        <div id="Framesplit">
            <div id="FramesplitDrag"></div>
        </div>
    </div>
    <div id="ContentFrame">
        <div class="statusbar">
            <ul class="breadcrumbs">
                <li>Forside</li>
                <li>Lorem ipsum</li>
                <li>Dolor sit amet</li>
            </ul>
        </div>
        <iframe name="contentmain" id="Contentmain" src="${showContent.url}" height="100%" width="100%"></iframe>
    </div>
    <div class="clearing"></div>

</div>


<ul id="ContextMenu-page" class="contextMenu">
    <li class="open">
        <a href="#open">Open</a>
    </li>
    <li class="openInNewWindow">
        <a href="#openInNewWindow">Open in new window</a>
    </li>
    <li class="newSubpage separator">
        <a href="#newSubpage">New subpage</a>
    </li>
    <li class="edit">
        <a href="#edit">Edit</a>
    </li>
    <li class="delete">
        <a href="#delete">Delete</a>
    </li>
    <li class="cut separator">
        <a href="#cut">Cut</a>
    </li>
    <li class="copy">
        <a href="#copy">Copy</a>
    </li>
    <li class="paste">
        <a href="#paste">Paste</a>
    </li>
    <li class="pasteAsShortcut">
        <a href="#pasteAsShortcut">Paste as shortcut</a>
    </li>
    <li class="managePrivileges separator">
        <a href="#managePrivileges">Manage privileges</a>
    </li>
</ul>

<ul id="ContextMenu-link" class="contextMenu">
    <li class="open">
        <a href="#open">Open</a>
    </li>
    <li class="openInNewWindow">
        <a href="#openInNewWindow">Open in new window</a>
    </li>
    <li class="edit separator">
        <a href="#edit">Edit</a>
    </li>
     <li class="delete">
        <a href="#delete">Delete</a>
    </li>
    <li class="cut separator">
        <a href="#cut">Cut</a>
    </li>
    <li class="copy">
        <a href="#copy">Copy</a>
    </li>
    <li class="paste">
        <a href="#paste">Paste</a>
    </li>
    <li class="pasteAsShortcut">
        <a href="#pasteAsShortcut">Paste as shortcut</a>
    </li>
    <li class="managePrivileges separator">
        <a href="#managePrivileges">Manage privileges</a>
    </li>
</ul>



</body>
</html>
