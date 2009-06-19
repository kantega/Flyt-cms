<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1"%>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
	<title>Can't create role</title>
    <link rel="stylesheet" type="text/css" href="<aksess:geturl/>/login/login.css">
<body>
<table border="0" cellspacing="0" cellpadding="0" width="400" align="center">
    <tr>
        <td width="1" rowspan="3" class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" height="1"></td>
        <td width="396" class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" height="1"></td>
        <td width="1" rowspan="3" class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" heigth="1"></td>
        <td width="2" rowspan="3" class="shadow" valign="top"><img src="<aksess:geturl/>/login/bitmaps/corner.gif" width="2" heigth="2"></td>
     </tr>
     <tr>
        <td class="box">

            <h1>Can't create role</h1>
            <p>
                The system has been setup so that roles are managed outside OpenAksess, eg LDAP or Active Directory.
            </p>

            <p>
                Setup your LDAP configuration correctly.
            </p>

        </td>
     </tr>
    <tr>
        <td class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" height="1"></td>
     </tr>
     <tr>
        <td colspan="4" class="shadow"><img src="<aksess:geturl/>/login/bitmaps/corner.gif" width="2" height="2"></td>
    </tr>
</table>

</body>
</html>
