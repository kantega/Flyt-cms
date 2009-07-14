<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
<%@ page import="java.io.IOException" %>
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

<kantega:section id="title"><kantega:label key="aksess.insertchar.title"/></kantega:section>

<kantega:section id="head">
    <script language="Javascript" type="text/javascript">
        function doInsert(code) {
            var char = '<SPAN class="symbol">&#' + code + ';</SPAN> ';
            if (window.opener) {
                window.opener.insertValueIntoForm(char);
            }
            window.close();
        }
    </script>
</kantega:section>

<kantega:section id="body">
    <div id="InsertCharForm">
        <fieldset>
            <legend><kantega:label key="aksess.insertchar.title"/></legend>
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

        </fieldset>

        <div class="buttonGroup">
            <a href="Javascript:window.close()" class="button cancel"><span><kantega:label key="aksess.button.avbryt"/></span></a>
        </div>
    </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>
