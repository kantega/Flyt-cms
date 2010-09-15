<%@ page import="java.util.Map"%>
<%@ page import="no.kantega.publishing.spring.RootContext"%>
<%@ page import="no.kantega.publishing.org.OrganizationManager"%>
<%@ page import="org.springframework.context.ApplicationContext"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.io.IOException"%>
<%@ page import="no.kantega.publishing.org.OrgUnit"%>
<%@ page import="no.kantega.publishing.common.data.attributes.DateAttribute"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="no.kantega.publishing.common.ao.HearingAO"%>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page buffer="none" %>
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
    public void showAttribute(String name, Attribute attribute, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setAttribute("attribute", attribute);
        request.setAttribute("fieldName", name);
        request.getRequestDispatcher("attributes/" +attribute.getRenderer() + ".jsp").include(request, response);

    }
%>
<html>
<head>
	<title></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <%@ include file="include/calendarsetup.jsp"%>
    <script type="text/javascript" language="Javascript" src="../../aksess/js/common.js"></script>
    <script type="text/javascript" language="Javascript" src="../js/browserdetect.js"></script>
    <script type="text/javascript" language="Javascript" src="../js/edit.jsp"></script>
    <script type="text/javascript" language="Javascript" src="../js/richtext.jsp"></script>
    
    <script type="text/javascript">

        function submitHearing() {
            document.myform.submit();

        }
    </script>
</head>
<body>

<c:if test="${errors.length > 0}">
    <div id="errorMessageArea" style="display:block;">
        <table border="0" cellspacing="0" cellpadding="0" class="error">
                <tr>
                    <td>

                        <div id="errorMessage">
                            <b>Vennligst fyll ut følgende felt riktig:</b>
                            <ul>
                                <c:forEach items="${errors.errors}" var="error">
                                    <LI><c:out value="${error.message}"/><!-- [<A href="Javascript:document.myform.<c:out value="${error.field}"/>.focus()">vis meg</A>]--></LI>
                                </c:forEach>
                            </ul>
                        </div>

                    </td>
                </tr>
        </table>
    </div>

</c:if>
<form name="myform" action="SaveHearing.action" method="POST">
    <%
        RequestParameters param = new RequestParameters(request);
    %>
    <input type="hidden" name="contentId" value="<%= param.getInt("contentId")%>">

    <table border="0" cellspacing="0" cellpadding="0" width="400" style="padding: 3px">
    <%
        Content content = (Content)session.getAttribute("currentContent");
        int cvid = HearingAO.getHearingContentVersion(content.getId());
        Hearing hearing = null;
        if(cvid != -1) {
            hearing = HearingAO.getHearingByContentVersion(cvid);
        }

        DateAttribute deadline = new DateAttribute();
        deadline.setName(LocaleLabels.getLabel("aksess.hearing.deadline", Aksess.getDefaultAdminLocale()));
        if(request.getAttribute("deadline") != null) {
            deadline.setValue((String)request.getAttribute("deadline"));
        } else {
            deadline.setValue(new SimpleDateFormat(Aksess.getDefaultDateFormat()).format(new Date()));
        }
        showAttribute("deadline", deadline, request, response);
        %>

        <tr><td><div class=helpText>Frist for når kommentarer til høringen må være inne</div></td></tr>
        <tr><td><img src="../bitmaps/blank.gif" width="2" height="8"></td></tr>

        <%
        TextAttribute desc = new TextAttribute();
        desc.setName(LocaleLabels.getLabel("aksess.hearing.changedescription", Aksess.getDefaultAdminLocale()));
        if(request.getAttribute("description") != null) {
            desc.setValue((String)request.getAttribute("description"));
        } else {
            desc.setValue("");
        }
        desc.setMaxLength(300);
        showAttribute("description", desc, request, response);
        %>

        <tr><td><div class=helpText>Kort beskrivelse av endringer i versjonen som legges ut på høring</div></td></tr>
        <tr><td><img src="../bitmaps/blank.gif" width="2" height="8"></td></tr>

        <%

        OrgunitAttribute orgunits = new OrgunitAttribute();

        orgunits.setName(LocaleLabels.getLabel("aksess.hearing.orgunits", Aksess.getDefaultAdminLocale()));
        orgunits.setMultiple(true);
        orgunits.setMoveable(false);
        if(request.getAttribute("orgunits") != null) {
            orgunits.setValue((String)request.getAttribute("orgunits"));
        }
        else {
            orgunits.setValue("");
            if(hearing != null) {
                List units = HearingAO.getOrgUnitInviteesForHearing(hearing.getId());
                for (int i = 0; i < units.size(); i++) {
                    HearingInvitee invitee = (HearingInvitee) units.get(i);
                    orgunits.setValue(orgunits.getValue() + invitee.getReference() + (i != units.size() -1 ? "," : ""));
                }
            }
        }

        showAttribute("orgunits", orgunits, request, response);
        %>
        <tr><td><div class=helpText>Organisasjonsenheter som skal ha tilgang til høringen</div></td></tr>
        <tr><td><img src="../bitmaps/blank.gif" width="2" height="8"></td></tr>

        <%
        UserAttribute users = new UserAttribute();
        users.setName(LocaleLabels.getLabel("aksess.hearing.users", Aksess.getDefaultAdminLocale()));
        users.setMultiple(true);
        users.setMoveable(false);

        if(request.getAttribute("users") != null) {
            users.setValue((String)request.getAttribute("users"));
        } else {
            users.setValue("");
            if(hearing != null) {
                List persons = HearingAO.getPersonInviteesForHearing(hearing.getId());
                for (int i = 0; i < persons.size(); i++) {
                    HearingInvitee invitee = (HearingInvitee) persons.get(i);
                    users.setValue(users.getValue() + invitee.getReference() + (i != persons.size() -1 ? "," : ""));
                }
            }}

        showAttribute("users", users, request, response);
        %>
        <tr><td><div class=helpText>Brukere som skal ha tilgang til høringen</div></td></tr>
        <tr><td><img src="../bitmaps/blank.gif" width="2" height="8"></td></tr>

        <%
    %>
</table>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
