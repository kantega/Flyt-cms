<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ page import="no.kantega.publishing.common.data.enums.ExpireAction" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<script type="text/javascript">
    function displayExpireAction() {
        var d = $("#end_date").val();
        if (openaksess.dateutils.isDateNotEmpty(d)) {
            $("#EndDateAction").show("slow");
        } else {
            $("#EndDateAction").hide("slow");
        }
    }

    function validatePublishProperties() {
        if (document.myform.alias && document.myform.alias.value == "/") {
            alert("<kantega:label key="aksess.error.aliasroot"/>");
            return false;
        }

        if (document.myform.from_date) {
            if (openaksess.dateutils.isDateNotEmpty(document.myform.from_date.value) && openaksess.dateutils.checkDate(document.myform.from_date.value) == -1) {
                document.myform.from_date.focus();
                return false;
            }
            if (openaksess.dateutils.isTimeNotEmpty(document.myform.from_time.value) && openaksess.dateutils.checkTime(document.myform.from_time.value) == -1) {
                document.myform.from_time.focus();
                return false;
            }
        }

        if (document.myform.end_date) {
            if (openaksess.dateutils.isDateNotEmpty(document.myform.end_date.value) && openaksess.dateutils.checkDate(document.myform.end_date.value) == -1) {
                document.myform.end_date.focus();
                return false;
            }
            if (openaksess.dateutils.isTimeNotEmpty(document.myform.end_time.value) && openaksess.dateutils.checkTime(document.myform.end_time.value) == -1) {
                document.myform.end_time.focus();
                return false;
            }
        }

        if (document.myform.change_date) {
            if (openaksess.dateutils.isDateNotEmpty(document.myform.change_date.value) && openaksess.dateutils.checkDate(document.myform.change_date.value) == -1) {
                document.myform.change_date.focus();
                return false;
            }
            if (openaksess.dateutils.isTimeNotEmpty(document.myform.change_time.value) && openaksess.dateutils.checkTime(document.myform.change_time.value) == -1) {
                document.myform.change_time.focus();
                return false;
            }
        }
        return true;
    }

    $(function() {
        $("#from_date").datepicker();
        $("#end_date").datepicker();
        $("#change_date").datepicker();
    });

    $(document).ready(function() {
        openaksess.common.debug("bindTopicButtons(): bind ChooseTopicButton");
        $("#ChooseTopicButton input").click(function(event){
            openaksess.common.debug("bindTopicButtons(): click ChooseTopicButton");
            openaksess.editcontext.selectTopic(null, true);
        });
        $("#Locked").click(function(event){
            var locked = document.getElementById("Locked");
            if (locked.checked) {
                $("#Alias").readOnly = true;
                document.myform.alias.readOnly = true;
                $("#LockedHelp").show();
            } else {
                $("#Alias").readOnly = false;
                $("#LockedHelp").hide();
            }
        });
        // Load topics
        var params = new Object();
        openaksess.editcontext.updateTopics(params);
        openaksess.editcontext.addTopicAutocomplete();
    });

</script>

<c:if test="${!isStartPage}">
    <div class="sidebarFieldset">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.period"/></legend>
            <table class="noborder" id="DisplayPeriod">
                <tr>
                    <td><label for="from_date"><kantega:label key="aksess.publishinfo.period.from"/></label></td>
                    <td><input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.publishDate}"/>" tabindex="500" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField()">
                    <td><label for="from_time"><kantega:label key="aksess.publishinfo.period.time"/></label></td>
                    <td><input type="text" id="from_time" name="from_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.publishDate}"/>" tabindex="501" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField()"></td>
                </tr>
                <tr>
                    <td><label for="end_date"><kantega:label key="aksess.publishinfo.period.until"/></label></td>
                    <td><input type="text" id="end_date" name="end_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.expireDate}"/>" tabindex="502" onFocus="openaksess.editcontext.setFocusField(this)" onchange="displayExpireAction()" onBlur="openaksess.editcontext.blurField()"></td>
                    <td><label for="end_time"><kantega:label key="aksess.publishinfo.period.time"/></label></td>
                    <td><input type="text" id="end_time" name="end_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.expireDate}"/>" tabindex="503" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField()"></td>
                </tr>
            </table>
            <div id="EndDateAction" <c:if test="${currentContent.expireDate == null}">style="display:none;"</c:if> >
                <label><kantega:label key="aksess.publishinfo.period.action"/></label>
                <table class="noborder" id="EndDateActionChoice">
                    <%
                        int expireAction = ((Content)session.getAttribute("currentContent")).getExpireAction();
                    %>
                    <tr>
                        <td><input name="expireaction" type="radio" id="ExpireActionHide" value="<%=ExpireAction.HIDE%>" <%if (expireAction == ExpireAction.HIDE) out.write(" checked");%>></td>
                        <td><label for="ExpireActionHide"><kantega:label key="aksess.publishinfo.period.action.hide"/></label></td>
                    </tr>
                    <tr>
                        <td><input name="expireaction" type="radio" id="ExpireActionArchive" value="<%=ExpireAction.ARCHIVE%>" <%if (expireAction == ExpireAction.ARCHIVE) out.write(" checked");%>></td>
                        <td><label for="ExpireActionArchive"><kantega:label key="aksess.publishinfo.period.action.archive"/></label></td>
                    </tr>
                    <tr>
                        <td><input name="expireaction" type="radio" id="ExpireActionRemind" value="<%=ExpireAction.REMIND%>" <%if (expireAction == ExpireAction.REMIND) out.write(" checked");%>></td>
                        <td><label for="ExpireActionRemind"><kantega:label key="aksess.publishinfo.period.action.remind"/></label></td>
                    </tr>
                    <tr>
                        <td><input name="expireaction" type="radio" id="ExpireActionDelete" value="<%=ExpireAction.DELETE%>" <%if (expireAction == ExpireAction.DELETE) out.write(" checked");%>></td>
                        <td><label for="ExpireActionDelete"><kantega:label key="aksess.publishinfo.period.action.delete"/></label></td>
                    </tr>
                </table>
            </div>
        </fieldset>
    </div>
</c:if>
<c:if test="${!currentContent.new  && canPublish}">
    <div class="sidebarFieldset">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.change"/></legend>
            <table class="noborder" id="ChangeDate">
                <tr>
                    <td><input type="text" id="change_date" name="change_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.changeFromDate}"/>" tabindex="500" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField()"></td>
                    <td><label for="change_time"><kantega:label key="aksess.publishinfo.change.time"/></label></td>
                    <td><input type="text" id="change_time" name="change_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.changeFromDate}"/>" tabindex="501" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField()"></td>
                </tr>
            </table>
            <div class="ui-state-highlight">
                <kantega:label key="aksess.publishinfo.change.help"/>
            </div>
        </fieldset>
    </div>
</c:if>
<c:if test="${!isStartPage}">
    <div class="sidebarFieldset">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.alias"/></legend>
            <input type="text" name="alias" id="Alias" size="30" maxlength="128" value="${currentContent.alias}" tabindex="510" <c:if test="${currentContent.locked}">readonly</c:if>>
            <div id="LockedHelp" class="ui-state-highlight" <c:if test="${!currentContent.locked}">style="display:none"</c:if>><kantega:label key="aksess.publishinfo.locked.help"/></div>
        </fieldset>
    </div>
</c:if>
<c:if test="${topicMapsEnabled}">
    <div class="sidebarFieldset">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.topics"/></legend>
            <div id="Topics">
                <div id="TopicList">...</div>
                <div id="SelectTopics">
                    <div id="TopicInputContainer">
                        <input type="text" id="TopicInput" size="20" maxlength="128" accesskey="E">
                    </div>
                    <div id="ChooseTopicButton">
                        <span class="button"><input type="button" class="select" value="<kantega:label key="aksess.publishinfo.topics.choose"/>"></span>
                    </div>
                </div>

            </div>

        </fieldset>
    </div>
</c:if>
<c:if test="${canChangeTemplate}">
    <div class="sidebarFieldset">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.displaytemplate"/></legend>
            <select name="displaytemplate" tabindex="60" onchange="openaksess.editcontext.setIsModified()">
                <c:forEach var="template" items="${allowedTemplates}">
                    <c:set var="templateName" value="${template.name}"/>
                    <c:if test="template.contentTemplate.id == current.contentTemplateId">
                        <c:set var="templateName" value="${template.name} *"/>
                    </c:if>
                    <option value="${template.id}" <c:if test="${template.id == currentContent.displayTemplateId}"> selected</c:if>>${templateName}</option>
                </c:forEach>

            </select>
            <c:if test="${isAdmin}">
                <div class="ui-state-highlight"><kantega:label key="aksess.publishinfo.displaytemplate.hjelp"/></div>
            </c:if>
        </fieldset>
    </div>
</c:if>
<div class="sidebarFieldset">
    <fieldset>
        <legend><kantega:label key="aksess.publishinfo.otherproperties"/></legend>
        <div class="row">
            <input type="checkbox" class="checkbox" name="searchable" id="Searchable" value="true"<c:if test="${currentContent.searchable}"> checked="checked"</c:if> tabindex="520"><label for="Searchable" class="checkbox"><kantega:label key="aksess.publishinfo.searchable"/></label>
            <div class="clearing"></div>
        </div>
        <c:if test="${isDeveloper}">
            <div class="row">
                <input type="checkbox" class="checkbox" name="locked" value="true" <c:if test="${currentContent.locked}">checked</c:if> id="Locked"><label for="Locked" class="checkbox"><kantega:label key="aksess.publishinfo.locked"/></label>
                <div class="clearing"></div>
            </div>
        </c:if>

    </fieldset>
</div>
