<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess"%>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%--
~ Copyright 2009 Kantega AS
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~  http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
--%>

<c:set var="notesActive" value="true"/>
<kantega:section id="title">
    <kantega:label key="aksess.statistics.title"/>
</kantega:section>

<kantega:section id="contentclass">statistics</kantega:section>

<kantega:section id="head extras">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/notes.jjs"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            openaksess.notes.currentUrl = "${currentNavigateContent.url}";
            openaksess.common.debug("notes.$(document).ready()");
            openaksess.notes.listNotes();
            $("#NoteSubmit").live('click', function(event) {
                event.preventDefault();
                openaksess.notes.addNote($("#NoteText").val());
                // Clear input form
                $("#NoteText").val("");
            });
            $("#Notes .delete").live('click', function(event) {
                event.preventDefault();
                openaksess.notes.deleteNote($(this).attr("href"));
            });
        });        
    </script>
</kantega:section>

<kantega:section id="content">
    <div id="MainPaneContent">
        <div class="fieldset">
            <fieldset>
                <h1><kantega:label key="aksess.notes.title"/></h1>

                <div id="NoteArea">
                    <form name="myform" action="SaveNote.action" method="post">
                        <textarea name="note" id="NoteText" cols="40" rows="6"></textarea>
                        <div class="buttonGroup">
                            <span class="button"><input type="submit" class="ok" id="NoteSubmit" value="<kantega:label key="aksess.notes.submit"/>"></span>
                        </div>
                    </form>

                    <div id="Notes">
                        <%-- The content is loaded with ajax by the ListNotes.action --%>
                    </div>
                </div>
            </fieldset>
        </div>

    </div>
</kantega:section>

<%@include file="../layout/contentNavigateLayout.jsp"%>
