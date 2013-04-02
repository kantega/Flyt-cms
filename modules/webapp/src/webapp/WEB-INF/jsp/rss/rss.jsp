<%--
  Created by IntelliJ IDEA.
  User: larlon
  Date: 02.04.13
  Time: 11:23
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
                    <c:out value="${baseUrl}"/><aksess:getattribute name="url" collection="undersider"/>
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