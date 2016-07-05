<%@ page import="no.kantega.commons.util.FormatHelper" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.common.data.Attachment" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
<html>
<head>
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-popuplayout.css"/>">
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-commonlayout.css"/>">

    <script type="text/javascript" src='<kantega:expireurl url="/wro-oa/admin-popuplayout.js"/>'></script>

</head>
<body id="Content">
<script language="Javascript" type="text/javascript">
    <%-- function updateAttachment(id) {
         var title;
         if (id == -1) {
             title = '<kantega:label key="aksess.attachment.add" escapeJavascript="true"/>';
         } else {
             title = '<kantega:label key="aksess.attachment.update" escapeJavascript="true"/>';
         }
         openaksess.common.modalWindow.open({title:title, iframe:true, href: "${pageContext.request.contextPath}/publish/popups/AddAttachment.action?attachmentId=" + id,width: 380, height:250});
     }

     function deleteAttachment(id) {
         if (confirm("<kantega:label key="aksess.attachments.confirmdelete"/>")) {
             window.onbeforeunload = null;
             window.location.href = "DeleteAttachment.action?attachmentId=" + id;
         }
     }
   --%>
    function toggleSeachable(id, checked) {
        $.post('${pageContext.request.contextPath}/admin/attachment/' + id + '/togglesearchable', function(response) {
            /*openaksess.common.debug(id + ' set to searchable: ' + checked);*/
        }, 'json');
    }

    function checkUnusedAttachments(contentId) {
        $('.spinner').addClass('ajaxloading');
        $.get('${pageContext.request.contextPath}/admin/attachment/content/' + contentId + '/unused',
                function (response) {
                    var unusedIds = response;
                    $('.unusedAttachment').each(function(i, elem){
                        var attachment = $(elem);
                        var attachmentId = parseInt(attachment.attr('data-attachmentid'), 10);
                        if (unusedIds.indexOf(attachmentId) != -1) {
                            attachment.addClass('isunused');
                            attachment.attr('title', '<kantega:label key="aksess.attachments.isunused"/>')
                        } else {
                            attachment.addClass('isused');
                            attachment.attr('title', '<kantega:label key="aksess.attachments.isused"/>')
                        }
                    });
                    $('.spinner').removeClass('ajaxloading');

                });
    }
</script>
<%
    List<Attachment> attachments = (List<Attachment>)request.getAttribute("attachments");

    if (attachments.size() > 0) {
%>

<table border="0" cellspacing="0" cellpadding="0">
    <tr class="tableHeading">
        <td><strong><kantega:label key="aksess.attachments.attachment"/></strong></td>
        <td><strong><kantega:label key="aksess.attachments.size"/></strong></td>
        <td><strong><kantega:label key="aksess.attachments.lastmodified"/></strong></td>
        <td>&nbsp;</td>
        <td><strong><kantega:label key="aksess.attachments.searchable"/></strong></td>
    </tr>
    <%
        for (int i = 0; i < attachments.size(); i++) {
            Attachment a = attachments.get(i);

            String modifiedDate = "";
            Date d = a.getLastModified();
            DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());

            try {
                modifiedDate = df.format(d);
            } catch (NumberFormatException e) {

            }
            String filename = a.getFilename();
            if (filename.length() > 50) {
                filename = filename.substring(0, 47) + "...";
            }
            request.setAttribute("attachment", a);
    %>
    <tr class="tableRow<%=(i%2)%>">
        <td>
            <a href="${pageContext.request.contextPath}/attachment.ap?id=<%=a.getId()%>"><%=filename%></a>
            <span class="unusedAttachment" data-attachmentid="<%=a.getId()%>"></span>
        </td>
        <td><%=FormatHelper.formatSize(a.getSize())%></td>
        <td><%=modifiedDate%></td>
        <td align="right">
            <%-- <a href="Javascript:updateAttachment(<%=a.getId()%>)" class="button"><span class="edit"><kantega:label key="aksess.button.replaceattachment"/></span></a>
             <a href="Javascript:deleteAttachment(<%=a.getId()%>)" class="button"><span class="delete"><kantega:label key="aksess.button.deleteattachment"/></span></a>--%>
        </td>
        <td><input type="checkbox"
                   <c:if test="${attachment.searchable and currentContent.searchable}">checked</c:if>
                   <c:if test="${not currentContent.searchable}">disabled</c:if>
                   onchange="toggleSeachable(<%=a.getId()%>, this.checked)" /></td>
    </tr>
    <%
        }
    %>
</table>
<span class="button"><input type="button" class="search" value="<kantega:label key="aksess.button.markunusedattachment"/>" onclick="checkUnusedAttachments(${currentContent.id})"></span>
<span class="spinner"></span>
<%
} else {
%>
    <p><kantega:label key="aksess.attachments.empty"/></p>
<%
}
%>

</body>
</html>
