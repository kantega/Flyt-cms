<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="java.io.IOException"%>
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
<%!
    int printRange(JspWriter out, int start, int end, int rowPos) throws IOException {
        int charPerRow = 24;

        for (int i = start; i <= end; i++) {
            if (rowPos == 0) {
                out.write("<TR>\n");
            }
            rowPos++;
            out.write("<TD align=\"center\" bgcolor=\"#ffffff\"><A href=\"#\" onClick=\"doInsert(" + i + ")\" class=\"symbol\" title=\"" + i +"\">&#" + i + ";</A></TD>\n");
            if (rowPos == charPerRow) {
                out.write("</TR>\n");
                rowPos = 0;
            }
        }
        return rowPos;
    }
%>
<%!
    void printEnd(JspWriter out, int rowPos) throws IOException {
        int charPerRow = 24;
        if (rowPos != charPerRow) {
            for (int i = rowPos; i < charPerRow; i++) {
                out.write("<TD bgcolor=\"#ffffff\">&nbsp;</TD>");
            }
            out.write("</TR>");
        }
    }
%>
<html>
<head>
	<title><kantega:label key="aksess.insertchar.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/browserdetect.js"></script>
<script language="Javascript" src="../js/edit.jsp"></script>
<script language="Javascript">
    function doInsert(code) {
        var char = '<SPAN class="symbol">&#' + code + ';</SPAN> ';
        var frm = document.myform;
        if (window.opener) {
            window.opener.insertValueIntoForm(char);
        }
        window.close();
    }
</script>

<body class="bodyWithMargin">
<form name="myform">
    <table border="0" width="400" cellspacing="0" cellpadding="0">
        <tr>
            <td width="400"><img src="../bitmaps/blank.gif" width="400" height="1"></td>
        </tr>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.insertchar.title"/></b></td>
            </tr>
            <tr>
                <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
            </tr>
            <tr>
                <td>
                    <table border="0" cellspacing="0" cellpadding="0" bgcolor="#000000" width="100%">
                        <tr>
                            <td>
                                <table border="0" cellspacing="1" cellpadding="0" width="100%">
                                <%
                                    int rowPos = 0;

                                    rowPos = printRange(out, 169, 169, rowPos);
                                    rowPos = printRange(out, 177, 177, rowPos);
                                    rowPos = printRange(out, 188, 190, rowPos);
                                    rowPos = printRange(out, 913, 937, rowPos);
                                    rowPos = printRange(out, 913, 937, rowPos);
                                    rowPos = printRange(out, 945, 969, rowPos);
                                    rowPos = printRange(out, 977, 978, rowPos);
                                    rowPos = printRange(out, 982, 982, rowPos);
                                    rowPos = printRange(out, 8704, 8901, rowPos);
                                    rowPos = printRange(out, 8968, 8971, rowPos);
                                    printEnd(out, rowPos);
                                %>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
         </table>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>