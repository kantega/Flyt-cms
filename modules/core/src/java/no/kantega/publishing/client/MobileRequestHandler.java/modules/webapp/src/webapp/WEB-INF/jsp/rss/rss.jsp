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

<%--
  User: Kristian Lier Selnæs, Kantega AS
  Date: 20.des.2006
  Time: 15:54:46
--%>
<%@ page contentType="text/xml;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns="http://purl.org/rss/1.0/" xmlns:taxo="http://purl.org/rss/1.0/modules/taxonomy/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:syn="http://purl.org/rss/1.0/modules/syndication/" xmlns:admin="http://webns.net/mvcb/">
  <channel rdf:about="<c:out value="${baseUrl}"/>/">
    <title><aksess:getattribute name="title"/></title>
    <link><c:out value="${baseUrl}"/>/</link>
    <description><aksess:getattribute name="title"/></description>
    <items>
      <rdf:Seq>
        <aksess:getcollection name="undersider" contentquery="${contentQuery}" orderby="publishdate" descending="true" max="${max}">

        <rdf:li rdf:resource="<c:out value="${baseUrl}"/><aksess:getattribute name="url" collection="undersider"/>"/>
        </aksess:getcollection>
     </rdf:Seq>
        </items>
      </channel>

        <aksess:getcollection name="undersider" contentquery="${contentQuery}" orderby="publishdate" descending="true" max="${max}">
            <item rdf:about="<c:out value="${baseUrl}"/><aksess:getattribute name="url" collection="undersider"/>">
                <title><aksess:getattribute name="title" collection="undersider"/></title>
                <link><c:out value="${baseUrl}"/><aksess:getattribute name="url" collection="undersider"/></link>
                <description><![CDATA[<aksess:getattribute name="description" collection="undersider"/>]]></description>
                <dc:date><aksess:getattribute name="publishdate" format="yyyy-MM-dd'T'hh:mm:ss+00:00" collection="undersider"/></dc:date>
              </item>
        </aksess:getcollection>
</rdf:RDF>