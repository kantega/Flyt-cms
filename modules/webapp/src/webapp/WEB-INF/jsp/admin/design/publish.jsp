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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title><kantega:label key="aksess.edit.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/default.css">
    <%@ include file="../../../../admin/publish/include/calendarsetup.jsp"%>
</head>
<script type="text/javascript" language="Javascript" src="../js/browserdetect.js"></script>
<script type="text/javascript" language="Javascript" src="../js/common.js"></script>
<script type="text/javascript" language="Javascript" src="../js/date.jsp"></script>
<script type="text/javascript" language="Javascript" src="../js/edit.jsp"></script>
<script type="text/javascript" language="Javascript" src="../js/richtext.jsp"></script>
<script type="text/javascript" language="Javascript" src="../../aksess/js/common.js"></script>
<script type="text/javascript" language="Javascript" src="../../aksess/js/autocomplete.js"></script>

<style type="text/css">
    #MainContent {
        width: 1000px;
        float: left;
    }

    #MainPane {
        width: 600px;
        float: left;
    }

    #PaneSeperator {
        width: 10px;
        float: left;
    }

    #PropertiesPane {
        float: right;
    }

    #PropertiesPane label {
        display: inline;
    }

    #PublishTabs {
        height: 21px;
        float:left;
    }

    #PublishTabs .tab {
        height: 21px;
        float:left;
    }

    #PublishButtons {
        height: 40px;
        clear: both;
    }
</style>

<script type="text/javascript">
function gotoTab(action) {
    document.myform.elements['action'].value = action;
    saveContent("");
}
</script>

<body onLoad="initialize()" class="bodyWithMargin">
    <div id="MainContent">

        <div id="MainPane">
            <div id="PublishTabs">
                <div class="tab" id="PublishPreview">
                    <a href="Javascript:gotoTab('Preview.action')">Preview</a>
                </div>
                <div class="tab" id="PublishContent">
                    <a href="Javascript:gotoTab('SaveContent.action')">Content</a>
                </div>
                <div class="tab" id="PublishMetadata">
                    <a href="Javascript:gotoTab('SaveMetadata.action')">Metadata</a>
                </div>
                <div class="tab" id="PublishAttachments">
                    <a href="Javascript:gotoTab('SaveAttachments.action')">Attachments</a>
                </div>
            </div>
            <div id="PublishButtons">
                <c:choose>
                    <c:when test="${canPublish}">
                        <a href="Javascript:saveContent(<%=ContentStatus.PUBLISHED%>)" class="button publish"><kantega:label key="aksess.button.publiser"/></a>
                    </c:when>
                    <c:otherwise>
                        <a href="Javascript:saveContent(<%=ContentStatus.WAITING%>)" class="button save"><kantega:label key="aksess.button.lagre"/></a>
                    </c:otherwise>
                </c:choose>
                <a href="Javascript:saveContent(<%=ContentStatus.DRAFT%>)" class="button draft"><kantega:label key="aksess.button.kladd"/></a>
                <c:if test="${hearingEnabled}">
                    <a href="Javascript:saveContent(<%=ContentStatus.HEARING%>)" class="button hearing"><kantega:label key="aksess.button.hoering"/></a>
                </c:if>
                <a href="CancelEdit.action" class="button-cancel"><kantega:label key="aksess.button.avbryt"/></a>
            </div>
            <div id="ContentPane">
                <kantega:getsection id="content"/>
            </div>
        </div>

        <div id="PaneSeperator"></div>
        
        <div id="PropertiesPane">
            <%@ include file="../publish/include/publishproperties.jsp" %>
        </div>
    </div>
</body>
</html>
