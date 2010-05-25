<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page buffer="none" %>
<%@ page import="no.kantega.publishing.admin.content.InputScreenRenderer"%>
<%@ page import="no.kantega.publishing.spring.RootContext"%>
<%@ page import="no.kantega.publishing.org.OrganizationManager"%>
<%@ page import="java.util.Map"%>
<%@ page import="no.kantega.publishing.forum.ForumProvider"%>
<%@ include file="../include/jsp_header.jsf" %>
<%@ include file="../include/edit_header.jsf" %>
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
    InputScreenRenderer screen = new InputScreenRenderer(pageContext, current, AttributeDataType.META_DATA);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>editmetadata.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script type="text/javascript" language="Javascript" src="../js/browserdetect.js"></script>
<script type="text/javascript" language="Javascript" src="../js/common.js"></script>
<script type="text/javascript" language="Javascript" src="../js/edit.jsp"></script>
<script type="text/javascript" language="Javascript" src="../js/richtext.jsp"></script>
<script type="text/javascript" language="Javascript" src="../../aksess/js/common.js"></script>
<script type="text/javascript" language="Javascript" src="../../aksess/js/autocomplete.js"></script>
<script type="text/javascript" src="../js/calendar/calendar.js"></script>
<script type="text/javascript" src="../js/calendar/calendar-en.js"></script>
<script type="text/javascript" src="../js/calendar/calendar-no.js" charset="utf-8"></script>
<script type="text/javascript" src="../js/calendar/calendar-setup.js"></script>

<script type="text/javascript" language="Javascript">
    var hasSubmitted = false;

    function removeTopic(topicMap, topicId) {
        window.parent.location = "../publish/DeleteContentTopic.action?topicMapId=" + topicMap + "&topicId=" + topicId;
    }

    function saveContent(status) {
    <%
        screen.generatePostJavascript();
    %>
    if (!hasSubmitted) {
        hasSubmitted = true;
        document.myform.status.value = status;
        document.myform.submit();
    }
}

</script>
<body class="bodyWithMargin">
<%@ include file="../include/infobox.jsp" %>
<form name="myform" action="SaveMetadata.action" target="content" method="post" accept-charset="utf-8">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <%
            if (Aksess.isTopicMapsEnabled()) {
        %>
            <tr>
                <td class="tableHeading"><b><kantega:label key="aksess.editmetadata.topics"/></b></td>
            </tr>
            <tr>
                <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
            </tr>
            <tr>
                <td>
                    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <%
                List topics = current.getTopics();
                for (int i = 0; i < topics.size(); i++) {
                    Topic topic = (Topic)topics.get(i);
        %>
                    <tr class="tableRow<%=(i%2)%>">
                        <td width="80%"><%=topic.getBaseName()%></td>
                        <td width="20%" align="right">
                            <table border="0">
                                <tr>
                                    <td><a href="Javascript:removeTopic(<%=topic.getTopicMapId()%>, '<%=topic.getId()%>')"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></a></td>
                                    <td><a href="Javascript:removeTopic(<%=topic.getTopicMapId()%>, '<%=topic.getId()%>')" class="button"><kantega:label key="aksess.button.fjern"/></a></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
        <%
                }
        %>
                    </table>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td><a href="Javascript:selectTopic(null)"><img src="../bitmaps/common/buttons/innhold_legg_til_emner.gif" border="0"></a></td>
                            <td><a href="Javascript:selectTopic(null)" class="button"><kantega:label key="aksess.button.leggtilemner"/></a></td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
            </tr>
        <%
            }
        %>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.editmetadata.metadata"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <%
            List documentTypes = aksessService.getDocumentTypes();
            if (documentTypes != null && documentTypes.size() > 0) {
        %>
        <!-- Dokumenttype -->
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.editmetadata.doctype"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <tr>
            <td>
                <select name="documenttype" style="width:600px;" tabindex="10">
                    <option value="-1"><kantega:label key="aksess.list.ingen"/></option>
                <%
                    for (int i=0; i < documentTypes.size(); i++) {
                        DocumentType dt = (DocumentType)documentTypes.get(i);
                        if (dt.getId() == current.getDocumentTypeId()) {
                            out.write("<option value=\"" + dt.getId() + "\" selected>" + dt.getName() + "</option>");
                        } else {
                            out.write("<option value=\"" + dt.getId() + "\">" + dt.getName() + "</option>");
                        }
                    }
                %>
                </select>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.editmetadata.doctypeforchildren"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <tr>
            <td>
                <select name="documenttypeforchildren" style="width:600px;" tabindex="10">
                    <option value="-1"><kantega:label key="aksess.list.ingen"/></option>
                <%
                    for (int i=0; i < documentTypes.size(); i++) {
                        DocumentType dt = (DocumentType)documentTypes.get(i);
                        if (dt.getId() == current.getDocumentTypeIdForChildren()) {
                            out.write("<option value=\"" + dt.getId() + "\" selected>" + dt.getName() + "</option>");
                        } else {
                            out.write("<option value=\"" + dt.getId() + "\">" + dt.getName() + "</option>");
                        }
                    }
                %>
                </select>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>
        <%
            }
        %>
        <!-- Alt title -->
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.editmetadata.alttitle"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <tr>
            <td><input type="text" name="alttitle" size="72" style="width:600px;" value="<%=current.getAltTitle()%>" maxlength="255" tabindex="20"></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>
        <!-- Publisher -->
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.editmetadata.publisher"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <tr>
            <td><input type="text" name="publisher" size="64" style="width:600px;" value="<%=current.getPublisher()%>" maxlength="64" tabindex="30"></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>
        <!-- Owner -->
        <%
            OrgunitAttribute orgunit = new OrgunitAttribute();

            orgunit.setName(LocaleLabels.getLabel("aksess.editmetadata.owner", Aksess.getDefaultAdminLocale()));
            orgunit.setValue(current.getOwner());
            request.setAttribute("attribute", orgunit);
            request.setAttribute("fieldName", "owner");
            request.getRequestDispatcher("attributes/" +orgunit.getRenderer() + ".jsp").include(request, response);
        %>
        <!-- OwnerPerson-->
        <%
            UserAttribute user = new UserAttribute();

            user.setName(LocaleLabels.getLabel("aksess.editmetadata.ownerperson", Aksess.getDefaultAdminLocale()));
            user.setValue(current.getOwnerPerson());
            request.setAttribute("attribute", user);
            request.setAttribute("fieldName", "ownerperson");
            request.getRequestDispatcher("attributes/" +user.getRenderer() + ".jsp").include(request, response);
        %>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>

        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>

        <!-- Keywords -->
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.editmetadata.keywords"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <tr>
            <td><textarea name="keywords" cols="72" rows="5" style="width:600px;" wrap="soft" tabindex="50"><%=current.getKeywords()%></textarea></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>
        <!-- Language -->
        <tr>
            <td class="inpHeading"><b><kantega:label key="aksess.editmetadata.language"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <tr>
            <td>
            <select name="language" style="width:600px;" tabindex="60">
            <%
                int[] languages = Language.getLanguages();
                for (int i = 0; i < languages.length; i++) {
                    int id = languages[i];
                    String code = Language.getLanguageAsISOCode(id);
                    String label = LocaleLabels.getLabel("aksess.editmetadata.language." + code, Aksess.getDefaultAdminLocale());
                    if (current.getLanguage() == id) {
                        out.write("<option value=\"" + id + "\" selected>" + label + "</option>");
                    } else {
                        out.write("<option value=\"" + id + "\">" + label + "</option>");
                    }
                }
            %>
            </select>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>
        <%
            Map forumProviders = RootContext.getInstance().getBeansOfType(ForumProvider.class);

            ContentManagementService cms = new ContentManagementService(request);
            if (current.getDisplayTemplateId() > 0) {
                DisplayTemplate dt = cms.getDisplayTemplate(current.getDisplayTemplateId());
                if(forumProviders.size() > 0 && dt.getDefaultForumId() != null) {

                    ForumProvider forumProvider = (ForumProvider) forumProviders.values().iterator().next();

                    boolean started = forumProvider.getThreadAboutContent(current) > 0;

                %>
                    <tr>
                        <td class="inpHeading"><b><kantega:label key="aksess.editmetadata.forum"/>:</b></td>
                    </tr>
                    <tr>
                        <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
                    </tr>
                    <tr>
                        <td>
                            <% if(!started) { %>
                            <select name="forumid" style="width:600px;">
                                <option value="-1" <%= current.getForumId() < 1 ? "selected" : ""%>><kantega:label key="aksess.editmetadata.forum.dontuse"/></option>
                                <%=forumProvider.getForumsAsOptionList(current.getForumId())%>
                            </select>
                            <% } else  {%>
                                <kantega:label key="aksess.editmetadata.forum.threadcreated"/>
                            <% } %>
                        </td>
                    </tr>
                    <tr>
                        <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
                    </tr>
                <%
                    }
                }
                %>

        <!-- Dynamic -->
        <%
            screen.generateInputScreen();
        %>
    </table>
    <input type="hidden" name="status" value="">
    <input type="hidden" name="action" value="">
    <input type="hidden" name="currentId" value="<%=current.getId()%>">
    <input type="hidden" name="isModified" value="<%=current.isModified()%>">
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>