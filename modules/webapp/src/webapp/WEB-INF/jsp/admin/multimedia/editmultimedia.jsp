<%@ page import="no.kantega.publishing.common.util.MultimediaTagCreator" %>
<%@ page import="no.kantega.publishing.common.data.Multimedia" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
    <kantega:label key="aksess.edit.title"/>
</kantega:section>

<kantega:section id="editbuttons">
    <span class="barButton hidden"><input type="submit" class="insert" value="<kantega:label key="aksess.button.insert"/>"></span>
    <span class="barButton"><input type="submit" class="save" value="<kantega:label key="aksess.button.save"/>"></span>
    <span class="barButton"><input type="submit" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
</kantega:section>

<kantega:section id="content">
    <script language="Javascript" type="text/javascript">
        var hasSubmitted = false;

        function saveForm() {
            if ("${media.id}" != "-1" && $("#MultimediaName").val() == "") {
                alert('<kantega:label key="aksess.multimedia.name.missing"/>');
                return;
            }

            if (${altNameRequired} && $("#MultimediaAltName").val() == "") {
                alert('<kantega:label key="aksess.multimedia.altname.missing"/>');
                return;
            }

            if (${descriptionRequired} && $("#MultimediaDescription").val() == "") {
                alert('<kantega:label key="aksess.multimedia.description.missing"/>');
                return;
            }

            if (!hasSubmitted) {
                hasSubmitted = true;
                document.editmediaform.submit();
            }
        }

        $(document).ready(function() {
            $("#MultimediaName").focus();
            var p = window.parent;

            if (p != window || window.opener) {
               $("#EditMultimediaButtons .insert").click(function (){
                    document.editmediaform.insert.value = true;
                    saveForm();
                }).parent().show();

                $("#MaxWidth").val(p.focusFieldMaxWidth);
            }


            <c:if test="${isPropertyPaneEditable}">
            $("#EditMultimediaButtons .save").click(function () {
                document.editmediaform.insert.value = false;
                document.editmediaform.changed.value = true;
                saveForm();
            });
            </c:if>

            $(".sidebarFieldset input").keypress(function(e) {
                document.editmediaform.changed.value = true;
            });

            $("#EditMultimediaButtons .cancel").click(function (){
                location.href = "Navigate.action";
            });
        });
    </script>

    <div id="MediaObject">
        <%=MultimediaTagCreator.mm2HtmlTag(Aksess.getContextPath(), (Multimedia)request.getAttribute("media"), null, -1, -1, null, true)%>
    </div>
</kantega:section>

<%@ include file="../layout/editMultimediaLayout.jsp" %>