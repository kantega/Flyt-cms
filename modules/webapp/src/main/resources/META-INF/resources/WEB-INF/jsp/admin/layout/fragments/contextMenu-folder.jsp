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

<ul id="ContextMenu-folder" class="contextMenu">
    <li class="newFolder">
        <a href="#newFolder"><kantega:label key="aksess.navigator.newfolder"/></a>
    </li>
    <li class="newFile">
        <a href="#newFile"><kantega:label key="aksess.navigator.newfile"/></a>
    </li>
    <li class="delete">
        <a href="#delete"><kantega:label key="aksess.navigator.delete"/></a>
    </li>
    <li class="cut separator">
        <a href="#cut"><kantega:label key="aksess.navigator.cut"/></a>
    </li>
    <li class="paste">
        <a href="#paste"><kantega:label key="aksess.navigator.paste"/></a>
    </li>
    <li class="managePrivileges separator">
        <a href="#managePrivileges"><kantega:label key="aksess.navigator.permissions"/></a>
    </li>    
</ul>

<%-- Identical to ContextMenu-folder. Hack to get different elements disabled for the root folder. --%>
<ul id="ContextMenu-root" class="contextMenu">
    <li class="newFolder">
        <a href="#newFolder"><kantega:label key="aksess.navigator.newfolder"/></a>
    </li>
    <li class="newFile">
        <a href="#newFile"><kantega:label key="aksess.navigator.newfile"/></a>
    </li>
    <li class="delete">
        <a href="#delete"><kantega:label key="aksess.navigator.delete"/></a>
    </li>
    <li class="cut separator">
        <a href="#cut"><kantega:label key="aksess.navigator.cut"/></a>
    </li>
    <li class="paste">
        <a href="#paste"><kantega:label key="aksess.navigator.paste"/></a>
    </li>
    <li class="managePrivileges separator">
        <a href="#managePrivileges"><kantega:label key="aksess.navigator.permissions"/></a>
    </li>
</ul>