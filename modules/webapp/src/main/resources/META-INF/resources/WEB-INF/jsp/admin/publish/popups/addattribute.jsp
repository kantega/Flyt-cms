<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
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
<kantega:section id="title"><kantega:label key="aksess.addattribute.title"/></kantega:section>

<kantega:section id="head">
    <style type="text/css">
        #HiddenAttributes {
            overflow: auto;
            max-height: 200px;
            padding: 1em;
        }

        #HiddenAttributes .row {
            margin-bottom: 0.5em;
        }
    </style>

    <script type="text/javascript">
        var hasSubmitted = false;
        function buttonOkPressed() {
            if (!hasSubmitted) {
                hasSubmitted = true;

                $("#HiddenAttributes").find("input:checked").each(function() {
                    var id = $(this).attr("name");
                    getParent().openaksess.editcontext.showHiddenAttribute(id);
                });
            }
            return true;
        }


        function listHiddenAttributes() {
            var parent = $("#HiddenAttributes");
            parent.html();

            getParent().$(".attributeHiddenEmpty").each(function() {
                var id = $(this).attr("id");
                var title = $(this).attr("data-title");

                $("<div class='row'></div>")
                        .append("<input type='checkbox' name='" + id + "' id='add_" + id + "' value='true' class='radio'>")
                        .append("<label for='add_" + id + "' class='radio'>" + title + "</label>")
                        .append("<div class='clearing'></div>")
                        .appendTo(parent);
            });
        }

        $(document).ready(function() {
            listHiddenAttributes();
        });
    </script>
</kantega:section>

<kantega:section id="body">
    <div id="HiddenAttributes" class="inpSelectList">

    </div>

    <div class="ui-state-highlight">
        <kantega:label key="aksess.addattribute.help"/>
    </div>

    <div class="buttonGroup">
        <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
        <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
    </div>

</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>