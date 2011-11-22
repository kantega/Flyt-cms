<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
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
    String fieldName = (String)request.getAttribute("fieldName");

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
    request.setAttribute("value", value);
%>

<c:choose>
    <c:when test="${(miniAksessMediaArchive != null && !miniAksessMediaArchive) || !attribute.useMediaArchive}">
        <div class="inputs">
            <!-- For users without access to mediaarchive - simple file upload -->
            <c:if test="${fn:length(value) != 0}">
                <div id="${fieldName}_media">
                    <aksess:getattribute name="${attribute.nameIncludingPath}" obj="${content}" width="270" cssclass="contentMedia"/>
                </div>
            </c:if>        
            <input type="hidden" name="${fieldName}" value="${value}">
            <input type="file" class="inp" name="${fieldName}_upload" value="" tabindex="<%=attribute.getTabIndex()%>">
            <c:if test="${fn:length(value) != 0}">
                <a id="${fieldName}_remove" href="#" onclick="openaksess.editcontext.removeValueAndNameFromForm(document.myform.${fieldName})" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><span class="remove"><kantega:label key="aksess.button.remove"/></span></a>
                <script type="text/javascript">
                    var button = $("#${fieldName}_remove");
                    var media = $("#${fieldName}_media");
                    button.click(function() {
                        button.remove();
                        media.remove();
                    });
                </script>
            </c:if>
        </div>
    </c:when>
    <c:otherwise>
        <div class="inputs">
            <input type="hidden" name="${fieldName}" value="${value}" id="${fieldName}">
            <input type="text" name="${fieldName}text" id="${fieldName}text" class="fullWidth" value="<%=mmname%>" onFocus="this.select()">
            <script type="text/javascript">
                $(document).ready(function() {
                    $("#${fieldName}text").oaAutocompleteMultimedia({
                        defaultValue: '<kantega:label key="aksess.insertlink.multimedia.hint"/>',
                        source: "${pageContext.request.contextPath}/ajax/AutocompleteMultimedia.action",
                        select: openaksess.editcontext.autocompleteInsertIntoFormCallback
                    });
                });
            </script>
        </div>
        <div class="buttonGroup">
            <a href="#" onclick="openaksess.editcontext.selectMultimedia(document.myform.${fieldName}, '<%=filter%>')" class="button" tabindex="<%=attribute.getTabIndex()%>"><span class="choose"><kantega:label key="aksess.button.choose"/></span></a>
            <a href="#" onclick="openaksess.editcontext.removeValueAndNameFromForm(document.myform.${fieldName})" class="button" tabindex="<%=(attribute.getTabIndex()+1)%>"><span class="remove"><kantega:label key="aksess.button.remove"/></span></a>
        </div>
    </c:otherwise>
</c:choose>
