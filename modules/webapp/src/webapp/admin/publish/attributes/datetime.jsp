<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<%@ page import="no.kantega.publishing.common.data.attributes.DatetimeAttribute" %>
<%@ page import="no.kantega.publishing.admin.util.DateUtil" %>
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

    String dateValue = (attribute.getDateValue() != null && attribute.getDateValue().trim().length() > 0)? attribute.getDateValue() : DateUtil.format(Aksess.getDefaultDateFormat(), Aksess.getDefaultAdminLocale());
    String timeValue = (attribute.getTimeValue() != null && attribute.getTimeValue().trim().length() > 0)? attribute.getTimeValue() : DateUtil.format(Aksess.getDefaultTimeFormat(), Aksess.getDefaultAdminLocale());
%>
<script type="text/javascript">
    $(function() {
        $("#date_${fieldName}").datepicker();
    });
</script>
<div class="inputs">
    <kantega:label key="aksess.attribute.datetime.date"/>&nbsp;<input type="text" id="date_${fieldName}" size="<%=dateLen%>" maxlength="<%=dateLen%>" name="date_${fieldName}" value="<%=dateValue%>" tabindex="<%=attribute.getTabIndex()%>" onfocus="openaksess.editcontext.clearDefaultValue(this,'<%=DateUtil.format(Aksess.getDefaultDateFormat(), Aksess.getDefaultAdminLocale())%>')" onblur="openaksess.editcontext.setDefaultValue(this,'<%=DateUtil.format(Aksess.getDefaultDateFormat(), Aksess.getDefaultAdminLocale())%>')">
    <kantega:label key="aksess.attribute.datetime.time"/>&nbsp;<input type="text" id="time_${fieldName}" size="<%=timeLen%>" maxlength="<%=timeLen%>" name="time_${fieldName}" value="<%=timeValue%>" tabindex="<%=attribute.getTabIndex()+1%>" onfocus="openaksess.editcontext.clearDefaultValue(this,'<%=DateUtil.format(Aksess.getDefaultTimeFormat(), Aksess.getDefaultAdminLocale())%>')" onblur="openaksess.editcontext.setDefaultValue(this,'<%=DateUtil.format(Aksess.getDefaultTimeFormat(), Aksess.getDefaultAdminLocale())%>')"><br>
</div>
