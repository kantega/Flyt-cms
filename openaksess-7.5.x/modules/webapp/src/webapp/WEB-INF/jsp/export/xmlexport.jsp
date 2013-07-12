<?xml version="1.0" encoding="UTF-8"?>
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
<%@ page contentType="text/xml;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>

<page id="<aksess:getattribute name="id" obj="${page}"/>" displaytemplateid="<aksess:getattribute name="displaytemplateid" obj="${page}"/>">
    <title><aksess:getattribute name="title" obj="${page}"/></title>
    <c:set var="alias"><aksess:getattribute name="alias" obj="${page}"/></c:set>
    <url <c:if test="${not empty alias}">alias="${alias}"</c:if> absolute="/content/<aksess:getattribute name="id"/>/"></url>
    <publishdate><aksess:getattribute name="publishdate" format="yyyy-MM-dd'T'HH:mm:ssZ" obj="${page}"/></publishdate>
    <lastmajorchange><aksess:getattribute name="lastmajorchange" format="yyyy-MM-dd'T'HH:mm:ssZ" obj="${page}"/></lastmajorchange>
    <expiredate><aksess:getattribute name="expiredate" format="yyyy-MM-dd'T'HH:mm:ssZ" obj="${page}"/></expiredate>
    <c:out value="${xml}" escapeXml="false"/>
    <children>
        <aksess:getcollection name="undersider" associatedid="${page.association.id}" orderby="priority">
            <child id="<aksess:getattribute name="id" collection="undersider"/>" absoluteurl="/content/<aksess:getattribute name="id" collection="undersider"/>/"/>
        </aksess:getcollection>
    </children>
</page>