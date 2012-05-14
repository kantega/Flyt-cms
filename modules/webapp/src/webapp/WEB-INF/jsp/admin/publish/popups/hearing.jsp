<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page import="no.kantega.publishing.common.data.HearingInvitee" %>
<%@ page import="no.kantega.publishing.common.data.attributes.UserAttribute" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.Hearing" %>
<%@ page import="no.kantega.publishing.common.data.attributes.OrgunitAttribute" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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

<kantega:section id="title"><kantega:label key="aksess.hearing.title"/></kantega:section>

<kantega:section id="head">
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/js/aksess-i18n.jjs"></script>
    <script language="Javascript" type="text/javascript"
            src="${pageContext.request.contextPath}/admin/js/editcontext.js"></script>
    <script type="text/javascript">
        function buttonOkPressed() {
            // Prevent popup from closing
            return false;
        }
    </script>
</kantega:section>

<kantega:section id="body">


    <form action="" name="myform" method="POST">
        <%@ include file="../../layout/fragments/infobox.jsp" %>
        <div id="HearingForm">

            <div class="contentAttribute">
                <fmt:formatDate var="deadline" value="${currentContent.hearing.deadLine}" pattern="dd.MM.yyyy"/>
                <admin:renderattribute type="date" name="hearing_deadline" titlekey="aksess.hearing.deadline"
                                       value="${deadline}"/>
                <admin:renderattribute type="text" name="hearing_changedescription"
                                       titlekey="aksess.hearing.changedescription"
                                       value="${currentContent.changeDescription}" maxlength="300"/>
                <%
                      Hearing hearing = ((Content) session.getAttribute("currentContent")).getHearing();
                    {

                        UserAttribute users = new UserAttribute();

                        users.setMultiple(true);
                        users.setMoveable(false);
                        users.setValue("");
                        if (hearing != null) {
                            for (HearingInvitee invitee : hearing.getInvitees()) {
                                if (invitee.getType() == HearingInvitee.TYPE_PERSON) {
                                    if (users.getValue().length() > 0) {
                                        users.setValue(users.getValue() + ",");
                                    }
                                    users.setValue(users.getValue() + invitee.getReference());
                                }
                            }
                        }
                        request.setAttribute("users", users);
                    }
                %>
                <admin:renderattribute attribute="${users}" name="hearing_users" titlekey="aksess.hearing.users"/>

            </div>

            <div class="buttonGroup">
                <span class="button"><input type="submit" class="insert"
                                            value="<kantega:label key="aksess.button.ok"/>"></span>
                <span class="button"><input type="button" class="cancel"
                                            value="<kantega:label key="aksess.button.cancel"/>"></span>
            </div>
        </div>
    </form>

</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>
