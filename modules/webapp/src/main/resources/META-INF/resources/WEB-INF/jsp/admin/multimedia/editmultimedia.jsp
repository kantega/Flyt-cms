<%@ page import="no.kantega.publishing.common.data.Multimedia" %>
<%@ page import="no.kantega.publishing.common.util.MultimediaTagCreator" %>
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
    <script type="text/javascript">
        function getParent() {
            if (window.opener) {
                return window.opener;
            } else {
                return window.parent;
            }
        }

        function closeWindow() {
            if (window.opener) {
                window.close();
            } else {
                try {
                    var tinymce = getParent().tinymce;
                    var ed = tinymce.editors[0];
                    ed.windowManager.windows[0].close();
                } catch (e) {
                    openaksess.common.debug("Failed to close tiny modal " + e);
                }
                window.setTimeout(parent.openaksess.common.modalWindow.close,300);
            }
        }

        var hasSubmitted = false;

        function saveForm() {
            if ("${media.id}" != "-1" && $("#MultimediaName").val() == "") {
                alert('<kantega:label key="aksess.multimedia.name.missing" escapeJavascript="true"/>');
                return;
            }

            if (${altNameRequired} && $("#MultimediaAltName").val() == "") {
                alert('<kantega:label key="aksess.multimedia.altname.missing" escapeJavascript="true"/>');
                return;
            }

            if (${descriptionRequired} && $("#MultimediaDescription").val() == "") {
                alert('<kantega:label key="aksess.multimedia.description.missing" escapeJavascript="true"/>');
                return;
            }

            if (!hasSubmitted) {
                hasSubmitted = true;
                var prmstr = window.location.search.substr(1);
                var prmarr = prmstr.split ("&");

                var form = $(document.editmediaform);
                for ( var i = 0; i < prmarr.length; i++) {
                    var tmparr = prmarr[i].split("=");
                    var name = tmparr[0];
                    if(name == 'ids'){
                        form.append('<input type="hidden" name="ids" value="' + tmparr[1] +'" />');
                    }
                }
                var w = getParent();
                if (w && w.openaksess.editcontext.insertMultimediaLink) {
                    var id = form[0].id.value;
                    var title = form[0].name.value;
                    w.openaksess.editcontext.insertMultimediaLink({
                        url : '/multimedia/' + id + '/' + w.openaksess.common.uglifyTitle(title),
                        name: title,
                        mimeType: '',
                        fileExtension : ''
                    });
                    closeWindow();
                } else {
                    document.editmediaform.submit();
                }

            }
        }
        $(document).ready(function() {
            $("#MultimediaName").focus();
            if (openaksess.common.isPopup()) {
                var $EditMultimediaButtons = $("#EditMultimediaButtons .insert");
                $EditMultimediaButtons.removeClass("hidden");

               $EditMultimediaButtons.click(function (){
                    document.editmediaform.insert.value = true;
                    saveForm();
                }).parent().show();

                var p;
                if (window.opener) {
                    p = window.opener;
                } else {
                    p = window.parent;
                }

                if(!p.tinyMCE){
                    p = p.parent;
                }

                if (p.tinyMCE) {
                    // Get max width of editor field, image should be resized to fit
                    var editor = p.tinyMCE.editors[0];
                    var editorwidth = editor.dom.getSize(editor.dom.getRoot()).w;

                    // Subtract 10 pixels to avoid scrolling
                    $("#MaxWidth").val(editorwidth - 10);
                }
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
