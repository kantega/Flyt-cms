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

function newMMObject(parent, type) {
    window.parent.location = "multimedia.jsp?activetab=editmultimedia&parentId=" + parent + "&type=" + type;
}

function editMMObject() {
    window.parent.location = "multimedia.jsp?activetab=editmultimedia";
}

function manipulateMMObject() {
    window.parent.location = "multimedia.jsp?activetab=imagemanipulation";
}

function imagemapMMObject(){
    window.parent.location = "multimedia.jsp?activetab=imagemap";
}

function deleteMMObject(id, name) {
    if (confirm("<kantega:label key="aksess.js.advarsel.onskerduaslette"/> " + name + "?")) {
        window.parent.location = "DeleteMultimedia.action?id=" + id;
    }
}

function gotoMMObject(id, type) {
    window.parent.location = "multimedia.jsp?activetab=view" + type + "&id=" + id;
}

