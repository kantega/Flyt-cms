<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
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
</head>
<script language="Javascript" type="text/javascript">
function doSearch() {
   var phrase = document.search.phrase;
   if (phrase.value.length < 3) {
      alert("Skriv inn minst 3 tegn i søkefeltet!");
      phrase.focus();
   } else {
      document.search.submit();
   }
}

</script>
<%
    Locale lang = (Locale)request.getAttribute("aksess_locale");
    String locale_bildesti_framework = "../bitmaps/"+skin+"/"+ lang.getLanguage().toLowerCase() + "/framework/";
    String locale_bildesti_buttons = "../bitmaps/"+skin+"/"+ lang.getLanguage().toLowerCase() + "/buttons/";
%>
<body>
<form name="search" action="searchresult.jsp" target="navtree" method="post">
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td background="<%=locale_bildesti_framework%>search_top.gif"><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="3">
                    <tr>
                        <td><input type="text" name="phrase" size="8" style="width:130px;"></td>
                        <td><a href="Javascript:doSearch()"><img src="<%=locale_bildesti_buttons%>sok.gif" border="0"></a></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td background="<%=locale_bildesti_framework%>search_bottom.gif"><img src="../bitmaps/blank.gif" width="2" height="5"></td>
        </tr>
    </table>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
