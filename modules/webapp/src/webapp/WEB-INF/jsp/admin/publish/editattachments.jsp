<%@ page import="no.kantega.publishing.common.data.enums.ExpireAction" %>
<%@ page import="no.kantega.publishing.admin.content.InputScreenRenderer" %>
<%@ page import="no.kantega.publishing.common.data.enums.AttributeDataType" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.Attachment" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.commons.util.FormatHelper" %>
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
<kantega:section id="content">
<script language="Javascript" type="text/javascript">
    var hasSubmitted = false;

    function initialize() {
        // Do nothing
    }

    function saveContent(status) {
        if (validatePublishProperties()) {
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.status.value = status;
                document.myform.submit();
            }
        }
    }

    function addAttachment() {
        updateAttachment(-1);
    }

    function updateAttachment(id) {
        var attwin = window.open("../popups/attachment.jsp?attachmentId=" + id, "attachmentWindow", "dependent,toolbar=no,width=310,height=130,resizable=no,scrollbars=no");
        attwin.focus();
    }

    function deleteAttachment(id) {
        if (confirm("<kantega:label key="aksess.attachments.confirmdelete"/>")) {
            location = "DeleteAttachment.action?attachmentId=" + id;
        }
    }

</script>
<form name="myform" action="SaveAttachments.action" method="post" enctype="multipart/form-data">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr class="tableHeading">
            <td><strong><kantega:label key="aksess.attachments.attachment"/></strong></td>
            <td><strong><kantega:label key="aksess.attachments.size"/></strong></td>
            <td><strong><kantega:label key="aksess.attachments.lastmodified"/></strong></td>
            <td>&nbsp;</td>
        </tr>
    <%
        List attachments = (List)request.getAttribute("attachments");

        for (int i = 0; i < attachments.size(); i++) {
            Attachment a = (Attachment)attachments.get(i);

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
    %>
            <tr class="tableRow<%=(i%2)%>">
                <td><a href="<%=Aksess.getContextPath()%>/attachment.ap?id=<%=a.getId()%>"><%=filename%></a></td>
                <td><%=FormatHelper.formatSize(a.getSize())%></td>
                <td><%=modifiedDate%></td>
                <td align="right">
                    <a href="Javascript:updateAttachment(<%=a.getId()%>)" class="buttonEdit"><kantega:label key="aksess.button.erstattfil"/></a>
                    <a href="Javascript:deleteAttachment(<%=a.getId()%>)" class="buttonDelete"><kantega:label key="aksess.button.slettvedlegg"/></a>
                </td>
            </tr>
    <%
        }
    %>
            <tr>
                <td colspan="4" align="right">
                     <a href="Javascript:addAttachment()" class="buttonNew"><kantega:label key="aksess.button.nyttvedlegg"/></a>
                </td>
            </tr>
    </table>

    <%
        
        if (attachments.size() == 0) {
    %>
            <div class=helpText><kantega:label key="aksess.attachments.hjelp"/></div>
    <%
        }
    %>

    <input type="hidden" name="status" value="">
    <input type="hidden" name="action" value="">
    <input type="hidden" name="currentId" value="${currentContent.id}">
    <input type="hidden" name="isModified" value="${currentContent.modified}">
</form>
</kantega:section>
<%@ include file="../design/publish.jsp" %>


