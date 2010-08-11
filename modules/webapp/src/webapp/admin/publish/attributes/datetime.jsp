<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.data.attributes.DateAttribute"%>
<%@ page import="no.kantega.publishing.common.data.attributes.DatetimeAttribute" %>
<%@ page import="no.kantega.publishing.admin.util.DateUtil" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
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
    String df = Aksess.getDefaultDateFormat();
    int dateLen = df.length();

    String tf = Aksess.getDefaultTimeFormat();
    int timeLen = tf.length();

    DatetimeAttribute attribute = (DatetimeAttribute)request.getAttribute("attribute");
    String fieldName = (String)request.getAttribute("fieldName");

    attribute.setFormat(Aksess.getDefaultDatetimeFormat());
    String dateValue = (attribute.getDateValue() != null && attribute.getDateValue().trim().length() > 0)? attribute.getDateValue() : DateUtil.format(Aksess.getDefaultDateFormat(), Aksess.getDefaultAdminLocale());
    String timeValue = (attribute.getTimeValue() != null && attribute.getTimeValue().trim().length() > 0)? attribute.getTimeValue() : DateUtil.format(Aksess.getDefaultTimeFormat(), Aksess.getDefaultAdminLocale());
%>
<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
                <td><img src="../bitmaps/common/textseparator.gif" alt=""></td>
                <td><a href="#" id="velgdato<%=fieldName%>0"><img src="../bitmaps/common/buttons/mini_velg.gif" border="0" alt=""></a></td>
                <td><a href="#" id="velgdato<%=fieldName%>1" class="button"><kantega:label key="aksess.button.velg.dato"/></a></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td class="inpHeadingSpacer"><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
</tr>
<tr>
    <td>
        <kantega:label key="aksess.attribute.datetime.date"/>&nbsp;<input type="text" class="inp" id="date_<%=fieldName%>" size="<%=dateLen%>" maxlength="<%=dateLen%>" name="date_<%=fieldName%>" value="<%=dateValue%>" tabindex="<%=attribute.getTabIndex()%>" onfocus="clearDefaultValue(this,'<%=DateUtil.format(Aksess.getDefaultDateFormat(), Aksess.getDefaultAdminLocale())%>')" onblur="setDefaultValue(this,'<%=DateUtil.format(Aksess.getDefaultDateFormat(), Aksess.getDefaultAdminLocale())%>')">
        <kantega:label key="aksess.attribute.datetime.time"/>&nbsp;<input type="text" class="inp" id="time_<%=fieldName%>" size="<%=timeLen%>" maxlength="<%=timeLen%>" name="time_<%=fieldName%>" value="<%=timeValue%>" tabindex="<%=attribute.getTabIndex()+1%>" onfocus="clearDefaultValue(this,'<%=DateUtil.format(Aksess.getDefaultTimeFormat(), Aksess.getDefaultAdminLocale())%>')" onblur="setDefaultValue(this,'<%=DateUtil.format(Aksess.getDefaultTimeFormat(), Aksess.getDefaultAdminLocale())%>')"><br>

        <script type="text/javascript">
            Calendar.setup( { inputField  : "date_<%=fieldName%>", ifFormat : "%d.%m.%Y", button : "velgdato<%=fieldName%>0", firstDay: 1 } );
            Calendar.setup( { inputField  : "date_<%=fieldName%>", ifFormat : "%d.%m.%Y", button : "velgdato<%=fieldName%>1", firstDay: 1 } );
        </script>
    </td>
</tr>