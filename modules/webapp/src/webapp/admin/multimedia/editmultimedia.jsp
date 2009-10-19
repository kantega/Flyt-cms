<%@ page import="no.kantega.commons.media.MimeType"%>
<%@ page import="no.kantega.publishing.common.data.enums.MultimediaType" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
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

<%
    Multimedia mm = (Multimedia)session.getAttribute("currentMultimedia");

    MimeType mimeType = mm.getMimeType();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
                                                                                                
<html>
<head>
	<title>editmultimedia.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" type="text/javascript">
var hasSubmitted = false;
function saveForm() {
   if ("<%=mm.getId()%>" != "-1" && document.myform.elements['name'].value == "") {
      alert('<kantega:label key="aksess.multimedia.name.missing"/>');
      return;
   }
   <%
   if (mm.getType() == MultimediaType.MEDIA) {
   %>
        if (("<%=mm.getId()%>" == "-1") && (document.myform.elements['file'].value == "")) {
            alert('<kantega:label key="aksess.multimedia.file.missing"/>');
            return;
        }
        if (<%=Aksess.getConfiguration().getBoolean("multimedia.altname.required", false)%> && document.myform.elements['altname'].value == "") {
            alert('<kantega:label key="aksess.multimedia.altname.missing"/>');
            return;
        }

        if (<%=Aksess.getConfiguration().getBoolean("multimedia.description.required", false)%> && document.myform.elements['description'].value == "") {
            alert('<kantega:label key="aksess.multimedia.description.missing"/>');
            return;
        }
    <%
    }
    %>

    if (!hasSubmitted) {
        hasSubmitted = true;
        document.myform.submit();
    }
}
function initialize() {
    try {
        document.myform.elements[0].focus()
    } catch (e) {
        // Usynlig element som ikke kan få fokus
    }
}
</script>
<body class="bodyWithMargin" onLoad="initialize()">
<%@ include file="../include/infobox.jsf" %>
<form name="myform" action="SaveMultimedia.action" method="post" target="content" enctype="multipart/form-data">
    <table border="0" cellspacing="0" cellpadding="0" width="500">
    <%
        if (mm.getType() == MultimediaType.FOLDER) {
    %>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.multimedia.foldername"/></b></td>
        </tr>
    <%
        } else {
    %>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.multimedia.medianame"/></b></td>
        </tr>
    <%
        }
    %>

        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td><input type="text" name="name" value="<%=mm.getName()%>" size="80" maxlength="255" style="width:500px;"></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
    <%
        if (mm.getType() != MultimediaType.FOLDER) {
            String titleFormat = Aksess.getMultimediaAltFormat();
            if (titleFormat.indexOf("$TITLE") != -1) {
    %>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" class="info">
                    <tr>
                        <td>
                            <kantega:label key="aksess.multimedia.medianameinfo"/>
                        </td>
                    </tr>
                </table>

            </td>
        </tr>
    <%
            }
    %>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.multimedia.altname"/></b></td>
        </tr>
        <tr>
            <td><input type="text" name="altname" value="<%=mm.getAltname()%>" size="80" maxlength="255" style="width:500px;"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" class="info">
                    <tr>
                        <td>
                            <kantega:label key="aksess.multimedia.altinfo"/>
                        </td>
                    </tr>
                </table>

            </td>
        </tr>

        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.multimedia.file"/></b> <kantega:label key="aksess.multimedia.file.help"/></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td><input type="file" name="file" size="55" style="width:500px;">
                <%
                    if (securitySession.isUserInRole(Aksess.getPhotographerRoles())) {
                %>
                    <input type="checkbox" name="preserveImageSize" value="true"><kantega:label key="aksess.multimedia.preserveimagesize"/>
                <%
                    }
                %>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2">
            </td>
        </tr>    
        <%
            if (mm.getId() == -1) {
        %>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" class="info">
                    <tr>
                        <td>
                            <kantega:label key="aksess.multimedia.zipinfo"/>
                        </td>
                    </tr>
                </table>

            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
        <%
            }
        %>
      <%
          if(mm.getId() != -1 && mimeType.userMustInputDimension()) {
      %>
        <tr>
            <td class="inpHeading">
                <b><kantega:label key="aksess.multimedia.size"/></b>
            </td>
        </tr>
        <%
            if(mm.getWidth() <= 0 || mm.getHeight() <= 0) {
        %>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" class="info">
                    <tr>
                        <td>
                            <kantega:label key="aksess.multimedia.sizeinfo"/>
                        </td>
                    </tr>
                </table>

            </td>
        </tr>
        <%
            }
        %>
        <tr>
            <td>
                <table>
                    <tr>
                        <td><kantega:label key="aksess.multimedia.width"/>:</td>
                        <td><input type="text" size="5" name="width" value="<%=mm.getWidth() <= 0 ? "" : mm.getWidth() +""%>"></td>
                    </tr>
                    <tr>
                        <td><kantega:label key="aksess.multimedia.height"/>:</td>
                        <td><input type="text" size="5" name="height" value="<%=mm.getHeight() <= 0 ? "" : mm.getHeight() +""%>"></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>
    <%
        }
    %>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.multimedia.author"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td><input type="text" name="author" value="<%=mm.getAuthor()%>" size="80" maxlength="255" style="width:500px;"></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.multimedia.usage"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td><textarea name="usage" wrap="soft" cols="80" rows="3" style="width:500px;"><%=mm.getUsage()%></textarea></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>

        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.multimedia.description"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td><textarea name="description" wrap="soft" cols="80" rows="5" style="width:500px;"><%=mm.getDescription()%></textarea></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
    <%
        }
    %>
    </table>
    <br>

    <%
        String urlparam = "";
        if (mm.getId() != -1) {
            urlparam = "?activetab=viewmultimedia&id=" + mm.getId();
        } else {
            urlparam = "?activetab=viewfolder&id=" + mm.getParentId();
        }
    %>
    <a href="Javascript:saveForm()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a>&nbsp;&nbsp;<a href="Javascript:window.parent.location = 'multimedia.jsp<%=urlparam%>'"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
    <input type="hidden" name="id" value="<%=mm.getId()%>">
    <input type="hidden" name="parentId" value="<%=mm.getParentId()%>">
    <input type="hidden" name="type" value="<%=mm.getType().getTypeAsInt()%>">
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>