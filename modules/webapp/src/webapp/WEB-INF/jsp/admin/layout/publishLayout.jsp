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
        $(document).ready(function(){
            bindPublishButtons();
        });

        function bindPublishButtons() {
            $("#ModesMenu .button .preview").click(function(){
                gotoMode("ViewContentPreview");
            });
            $("#ModesMenu .button .edit").click(function(){
                gotoMode("SaveContent");
            });
            $("#EditContentTabs .tab .content").click(function(){
                gotoMode("SaveContent");
            });
            $("#EditContentTabs .tab metadata").click(function(){
                gotoMode("SaveMetadata");
            });
            $("#EditContentTabs .tab .versions").click(function(){
                gotoMode("SaveVersion");
            });
            $("#EditContentTabs .tab .attachments").click(function(){
                gotoMode("SaveAttachments");
            });

            $("#EditContentButtons input.publish").click(function(){
                saveContent(<%=ContentStatus.PUBLISHED%>);
            });
            $("#EditContentButtons input.save").click(function(){
                saveContent(<%=ContentStatus.WAITING_FOR_APPROVAL%>);
            });
            $("#EditContentButtons input.savedraft").click(function(){
                saveContent(<%=ContentStatus.DRAFT%>);
            });
            $("#EditContentButtons input.hearing").click(function(){
                saveContent(<%=ContentStatus.HEARING%>);
            });
            $("#EditContentButtons input.cancel").click(function(){
                location.href = 'CancelEdit.action';
            });
        }


        function setLayoutSpecificSizes( ) {
            var maxHeight = $("#MainPane").height() - $("#EditContentTabs").height() - $("#EditContentButtons").height();
            var width = $("#MainPane").width();

            $('#MainPane iframe').css('height', (maxHeight-20) + 'px').css('width', (width-20) + 'px'); 

        }
        
        function gotoMode(action) {
            action = action + ".action";
            var href = "" + window.location.href;
            if (href.indexOf(action) != -1) {
                // Tried to click current tab
                return;
            }

            document.myform.elements['action'].value = action;
            saveContent("");
        }
    </script>
</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">
    <div class="buttonGroup">
        <a href="#" class="button"><span class="preview"><kantega:label key="aksess.mode.preview"/></span></a>
        <span class="buttonSeparator"></span>
        <a href="#" class="button last"><span class="edit"><kantega:label key="aksess.mode.edit"/></span></a>
    </div>

</kantega:section>

<kantega:section id="body">
    <form name="myform" action="" method="post" enctype="multipart/form-data">

        <div id="Content" class="publish">
            <div id="MainPane">
                <div id="EditContentTabs" class="tabGroup">
                    <a href="#" class="tab"><span class="content"><kantega:label key="aksess.tools.content"/></span></a>
                    <a href="#" class="tab"><span class="metadata"><kantega:label key="aksess.tools.metadata"/></span></a>
                    <a href="#" class="tab"><span class="attachments"><kantega:label key="aksess.tools.attachments"/></span></a>
                    <a href="#" class="tab"><span class="versions"><kantega:label key="aksess.tools.versions"/></span></a>
                </div>
                <div id="EditContentButtons">
                    <div class="buttonGroup">
                        <c:choose>
                            <c:when test="${canPublish}">
                                <input type="button" class="button publish" value="<kantega:label key="aksess.button.publish"/>">
                            </c:when>
                            <c:otherwise>
                                <input type="button" class="button save" value="<kantega:label key="aksess.button.save"/>">
                            </c:otherwise>
                        </c:choose>
                        <input type="button" class="button savedraft" value="<kantega:label key="aksess.button.save"/>">
                        <c:if test="${hearingEnabled}">
                            <input type="button" class="button hearing" value="<kantega:label key="aksess.button.hoering"/>">
                        </c:if>
                        <input type="button" class="button cancel" value="<kantega:label key="aksess.button.cancel"/>">
                    </div>
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