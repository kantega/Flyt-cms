<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
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

<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<html>
<head>
    <title>SQL tool</title>
</head>
<body>
<form action="<aksess:geturl/>/admin/tools/sqltool" method="post">
    <textarea rows="8" cols="60" name="query">${query}</textarea><input accesskey="E" type="submit" value="Execute query">
</form>
<table border="1">
    ${lines}
</table>
</body>
</html>