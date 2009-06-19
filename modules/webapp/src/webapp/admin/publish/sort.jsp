<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>Untitled</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">
function setSort(sort) {
    if (window.parent) {
        window.parent.navtree.setSort(sort);
    }
}
</script>
<body class="framework">
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="2"></td>
            <td>
            <kantega:label key="aksess.navigator.sort"/>:
            <a href="Javascript:setSort('<%=ContentProperty.PRIORITY%>')" class="button"><kantega:label key="aksess.navigator.sort.priority"/></a>
            <a href="Javascript:setSort('<%=ContentProperty.LAST_MODIFIED%>')" class="button"><kantega:label key="aksess.navigator.sort.date"/></a>
            <a href="Javascript:setSort('<%=ContentProperty.TITLE%>')" class="button"><kantega:label key="aksess.navigator.sort.title"/></a>
            </td>
        </tr>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
