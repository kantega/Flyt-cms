<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters,
                 no.kantega.publishing.common.data.Multimedia"%>
<%@ page import="no.kantega.publishing.common.data.enums.MultimediaType" %>
<%@ page import="no.kantega.publishing.common.util.MultimediaHelper" %>
<%@ page import="java.util.List" %>
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

<%
    RequestParameters param = new RequestParameters(request, "utf-8");

    Multimedia m = (Multimedia)session.getAttribute("currentMultimedia");
    int parentId = 0;
    if (m != null) {
        parentId = m.getId();
        if (parentId == -1) {
            parentId = 0;
        }
    }
    
    int cOffset = param.getInt("offset");
    if (cOffset < 0) {
        cOffset = 0;
    }

    int maxPerRow  = 3;
    int maxPerPage = 60;

    int noObjs = 0;
    List mmlist = mediaService.getMultimediaList(parentId);
    if (mmlist != null) {
        noObjs = mmlist.size();
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>index.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/multimedia.jsp"></script>
<script language="Javascript">
    function gotoOffset(offset) {
        offset = <%=cOffset%> + offset;
        location = "viewfolder.jsp?offset=" + offset;
    }
</script>
<body class="bodyWithMargin">
<table width="100%">
<%@ include file="navfolder.jsf" %>
<%
    if (noObjs > 0) {
        int colNo = 0;

        int max = Math.min(cOffset + maxPerPage, noObjs);
        for (int i = cOffset; i < max; i++) {
            Multimedia mm = (Multimedia)mmlist.get(i);
            if (colNo == 0) {
                out.write("<tr>");
            }

            String name = mm.getName();
            if (name.length() > 20) {
                name = name.substring(0, 17) + "...";
            }

            MultimediaType type = mm.getType();
            String prefix = "media";
            if (type == MultimediaType.FOLDER) {
                prefix = "folder";
            }

            out.write("<td>");

            out.write("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"174\">");
            out.write("<tr height=\"5\">");
            out.write("<td rowspan=\"4\"><img src=\"../bitmaps/common/mm/" + prefix + "_left.gif\" width=\"7\" height=\"154\"></td>");
            out.write("<td><img src=\"../bitmaps/common/mm/media_top.gif\" width=\"160\" height=\"5\"></td>");
            out.write("<td rowspan=\"4\"><img src=\"../bitmaps/common/mm/" + prefix + "_right.gif\" width=\"7\" height=\"154\"></td>");
            out.write("</tr>");
            out.write("<tr height=\"120\">");
            if (type == MultimediaType.FOLDER) {
                out.write("<td align=\"center\"  class=\"mmbackground\" onClick=\"gotoMMObject(" + mm.getId() + ", 'folder')\">" + name + "</td>");
            } else {
                out.write("<td align=\"center\"  class=\"mmbackground\" onClick=\"gotoMMObject(" + mm.getId() + ", 'multimedia')\">");
                String mimeType = mm.getMimeType().getType();
                if (mimeType.indexOf("image") != -1) {
                    out.write(MultimediaHelper.mm2HtmlTag(mm, 156, 120));
                } else if (mimeType.indexOf("flash") != -1) {
                    out.write("<img src=\"../bitmaps/common/mm/icon_flash.gif\" alt=\"Flash fil\">");
                } else if (mimeType.indexOf("x-ms-wmv") != -1 || mimeType.indexOf("x-msvideo") != -1) {
                    out.write("<img src=\"../bitmaps/common/mm/icon_wm.gif\" alt=\"Windows Media fil\">");
                } else {
                    out.write(mm.getFileType());
                }
                out.write("</td>");
            }
            out.write("</tr>");
            out.write("<tr height=\"22\" class=\"mmbackground\">");
            out.write("<td align=\"center\">");
            if (type == MultimediaType.MEDIA) {
                out.write(name);
            }
            out.write("</td>");
            out.write("</tr>\n");
            out.write("<tr height=\"7\">");
            out.write("<td><img src=\"../bitmaps/common/mm/" + prefix + "_bottom.gif\" width=\"160\" height=\"7\"></td>");
            out.write("</tr>");
            out.write("</table>");
            out.write("</td>");

            colNo++;
            if (colNo == maxPerRow) {
                out.write("</tr>");
                colNo = 0;
            }
        }
        if (colNo != 0) {
            for (int i = colNo; i < maxPerRow; i++) {
                out.write("<td>&nbsp;</td>");
            }
            out.write("</tr>");
        }
    }
%>
<%@ include file="navfolder.jsf" %>
</table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>