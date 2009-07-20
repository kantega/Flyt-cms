<%@ page import="no.kantega.publishing.common.data.enums.ContentStatus" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/admin" prefix="admin" %>
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



<kantega:section id="head">
    <%@ include file="../../../../admin/publish/include/calendarsetup.jsp"%>
    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/publish.css">
    <script type="text/javascript" language="Javascript" src="../js/sidebar.jjs"></script>
    <script type="text/javascript" language="Javascript" src="../js/browserdetect.js"></script>
    <script type="text/javascript" language="Javascript" src="../js/date.jsp"></script>
    <script type="text/javascript" language="Javascript" src="../js/edit.jjs"></script>
    <script type="text/javascript" language="Javascript" src="../js/richtext.jjs"></script>
    <script type="text/javascript" language="Javascript" src="../../aksess/js/common.js"></script>

    <script type="text/javascript" language="Javascript" src="../../aksess/js/autocomplete.js"></script>

    <script type="text/javascript">
        function gotoTab(action) {
            document.myform.elements['action'].value = action;
            saveContent("");
        }
    </script>
</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">
    <%@include file="fragments/publishModesMenu.jsp"%>
</kantega:section>

<kantega:section id="toolsMenu">

</kantega:section>

<kantega:section id="body">
    <form name="myform" action="" method="post" enctype="multipart/form-data">

    <div id="TwoPaneContent">
        <div id="TwoPaneMainPane">
            <div id="EditContentTabs" class="tabGroup">
                <div class="tab" id="PublishPreview">
                    <a href="Javascript:gotoTab('ViewContentPreview.action')">Preview</a>
                </div>
                <div class="tab" id="PublishContent">
                    <a href="Javascript:gotoTab('SaveContent.action')">Content</a>
                </div>
                <div class="tab" id="PublishMetadata">
                    <a href="Javascript:gotoTab('SaveMetadata.action')">Metadata</a>
                </div>
                <div class="tab" id="PublishVersions">
                    <a href="Javascript:gotoTab('SaveVersion.action')">Historikk</a>
                </div>
                <div class="tab" id="PublishAttachments">
                    <a href="Javascript:gotoTab('SaveAttachments.action')">Attachments</a>
                </div>
            </div>
            <div id="EditContentButtons">
                <c:choose>
                    <c:when test="${canPublish}">
                        <input type="button" class="button publish" onclick="saveContent(<%=ContentStatus.PUBLISHED%>)" value="<kantega:label key="aksess.button.publish"/>">
                    </c:when>
                    <c:otherwise>
                        <input type="button" class="button save" onclick="saveContent(<%=ContentStatus.WAITING_FOR_APPROVAL%>)" value="<kantega:label key="aksess.button.save"/>">
                    </c:otherwise>
                </c:choose>
                <input type="button" class="button save" onclick="saveContent(<%=ContentStatus.WAITING_FOR_APPROVAL%>)" value="<kantega:label key="aksess.button.save"/>">
                <c:if test="${hearingEnabled}">
                    <input type="button" class="button hearing" onclick="saveContent(<%=ContentStatus.HEARING%>)" value="<kantega:label key="aksess.button.hoering"/>">
                </c:if>
                <input type="button" class="button cancel" onclick="location.href='CancelEdit.action'" value="<kantega:label key="aksess.button.cancel"/>">
            </div>
            <div id="EditContentPane">
                <kantega:getsection id="content"/>
            </div>
        </div>
        <div id="SideBarSplit"></div>
        <div id="SideBar">
            <%@ include file="../publish/include/publishproperties.jsp" %>
        </div>
    </div>
    </form>
</kantega:section>

<%@include file="commonLayout.jsp"%>