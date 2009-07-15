<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.publishing.common.Aksess,
                 no.kantega.publishing.common.data.Multimedia,
                 no.kantega.publishing.common.data.attributes.MediaAttribute"%>
<%@ page import="no.kantega.publishing.common.service.MultimediaService"%>
<%@ page import="no.kantega.commons.util.LocaleLabels"%>
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
    MediaAttribute attribute = (MediaAttribute)request.getAttribute("attribute");
    String    fieldName = (String)request.getAttribute("fieldName");

    String value = attribute.getValue();
    String mmname = "";

    String filter = attribute.getFilter();
    if (filter == null) filter = "";

    if (value != null && value.length() > 0) {
        try {
            MultimediaService mms = new MultimediaService(request);
            Multimedia mm = mms.getMultimedia(Integer.parseInt(value));
            if (mm != null) {
                mmname = mm.getName();
            } else {
                value = "";
            }
        } catch (NumberFormatException e) {
            value = "";
        }
    }

    if (mmname.length() == 0) {
        mmname = LocaleLabels.getLabel("aksess.insertlink.multimedia.hint", Aksess.getDefaultAdminLocale());
    }

%>
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<div class="buttonGroup">
    <a href="Javascript:selectMultimedia(document.myform.<%=fieldName%>, '<%=filter%>')" class="button choose" tabindex="<%=attribute.getTabIndex()%>"><span><kantega:label key="aksess.button.choose"/></span></a>
    <a href="Javascript:removeIdAndValueFromForm(document.myform.<%=fieldName%>)" class="button delete" tabindex="<%=(attribute.getTabIndex()+1)%>"><span><kantega:label key="aksess.button.delete"/></span></a>
</div>
<div class="inputs">
    <input type="hidden" name="<%=fieldName%>" value="<%=value%>" id="<%=fieldName%>">
    <input type="text" name="<%=fieldName%>text" id="<%=fieldName%>text" class="fullWidth" value="<%=mmname%>" onFocus="this.select()">
    <script type="text/javascript">
        Autocomplete.setup({'inputField' :'<%=fieldName%>', url:'../../ajax/SearchMultimediaAsXML.action', 'minChars' :3 });
    </script>
</div>