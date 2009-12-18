<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
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
<kantega:section id="title">
    <kantega:label key="aksess.insertlink.title"/>
</kantega:section>

<kantega:section id="head">
    <script language="Javascript" type="text/javascript" src="${pageContext.request.contextPath}/aksess/js/autocomplete.js"></script>
</kantega:section>

<kantega:section id="body">


    <div id="SelectLinkType">
        <div class="tabGroup">
            <a href="?linkType=external" class="tab ${externalSelected}"><span><kantega:label key="aksess.insertlink.external"/></span></a>
            <a href="?linkType=internal" class="tab ${internalSelected}"><span><kantega:label key="aksess.insertlink.internal"/></span></a>
            <a href="?linkType=anchor" class="tab ${anchorSelected}"><span><kantega:label key="aksess.insertlink.anchor"/></span></a>
            <a href="?linkType=attachment" class="tab ${attachmentSelected}"><span><kantega:label key="aksess.insertlink.attachment"/></span></a>
            <a href="?linkType=email" class="tab ${emailSelected}"><span><kantega:label key="aksess.insertlink.email"/></span></a>
            <a href="?linkType=multimedia" class="tab ${multimediaSelected}"><span><kantega:label key="aksess.insertlink.multimedia"/></span></a>
        </div>
    </div>

    <div id="InsertLinkForm">
        <form action="" name="linkform">
            <div class="fieldset">
                <fieldset>
                    <h1><kantega:label key="aksess.insertlink.title"/></h1>

                    <jsp:include page="insert-link/${linkType}.jsp"/>
                    <div class="buttonGroup">
                        <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                        <span class="button"><input type="button" class="cancel" onclick="window.close()" value="<kantega:label key="aksess.button.cancel"/>"></span>
                    </div>
                </fieldset>
            </div>
        </form>
    </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>
