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


<%@ page contentType="text/xml;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<rss version="2.0">
    <channel>
        <title><aksess:getattribute name="title"/></title>
        <link><c:out value="${baseUrl}"/></link>
        <description><aksess:getattribute name="title"/></description>

        <!-- Items -->
        <aksess:getcollection name="undersider" contentquery="${contentQuery}" orderby="publishdate" descending="true"
                              max="${max}">
            <item>
                <title><aksess:getattribute name="title" collection="undersider"/></title>
                <link>
                    <c:out value="${baseUrl}"/>
                    <aksess:getattribute name="url" collection="undersider"/>
                </link>
                <description>
                    <![CDATA[<aksess:getattribute name="description" collection="undersider"/>]]>
                </description>
                <pubDate>
                    <aksess:getattribute name="publishdate" format="yyyy-MM-dd'T'hh:mm:ss+00:00"
                                         collection="undersider"/>
                </pubDate>
            </item>
        </aksess:getcollection>
    </channel>
</rss>