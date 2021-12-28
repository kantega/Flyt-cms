<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
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
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.enums.ExpireAction" %>
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
        $("#from_date").datepicker({
            dateFormat: "<%=Aksess.getDefaultDateFormatJS()%>"
        });
        $("#end_date").datepicker({
            dateFormat: "<%=Aksess.getDefaultDateFormatJS()%>"
        });
        $("#change_date").datepicker({
            dateFormat: "<%=Aksess.getDefaultDateFormatJS()%>"
        });
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
        $("#EndDateAction input[name=expireaction]").change(function() {
            var expireAction = $(this).val();
            if (expireAction == <%=ExpireAction.DELETE%>) {
                $("#ExpireActionDeleteWarning").show();
            } else {
                $("#ExpireActionDeleteWarning").hide();
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
                    <td><input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.publishDate}"/>" tabindex="${maxTabindex+10}" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField(this)">
                    <td><label for="from_time"><kantega:label key="aksess.publishinfo.period.time"/></label></td>
                    <td><input type="text" id="from_time" name="from_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.publishDate}"/>" tabindex="${maxTabindex+11}" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField(this)"></td>
                </tr>
                <tr>
                    <td><label for="end_date"><kantega:label key="aksess.publishinfo.period.until"/></label></td>
                    <td><input type="text" id="end_date" name="end_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.expireDate}"/>" tabindex="${maxTabindex+12}" onFocus="openaksess.editcontext.setFocusField(this)" onchange="displayExpireAction()" onBlur="openaksess.editcontext.blurField(this)"></td>
                    <td><label for="end_time"><kantega:label key="aksess.publishinfo.period.time"/></label></td>
                    <td><input type="text" id="end_time" name="end_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.expireDate}"/>" tabindex="${maxTabindex+13}" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField(this)"></td>
                </tr>
            </table>
            <div id="EndDateAction" <c:if test="${currentContent.expireDate == null}">style="display:none;"</c:if> >
                <label><kantega:label key="aksess.publishinfo.period.action"/></label>
                <table class="noborder" id="EndDateActionChoice">
                    <%
                        ExpireAction expireAction = ((Content)session.getAttribute("currentContent")).getExpireAction();
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
                <div class="ui-state-error" id="ExpireActionDeleteWarning" <%if (expireAction != ExpireAction.DELETE) out.write(" style=\"display:none\"");%>><kantega:label key="aksess.publishinfo.period.action.delete.warning"/></div>
            </div>
        </fieldset>
    </div>
</c:if>
<c:if test="${!currentContent['new']  && canPublish}">
    <div class="sidebarFieldset">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.change"/></legend>
            <table class="noborder" id="ChangeDate">
                <tr>
                    <td><input type="text" id="change_date" name="change_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.changeFromDate}"/>" tabindex="${maxTabindex+20}" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField(this)"></td>
                    <td><label for="change_time"><kantega:label key="aksess.publishinfo.change.time"/></label></td>
                    <td><input type="text" id="change_time" name="change_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.changeFromDate}"/>" tabindex="${maxTabindex+21}" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField(this)"></td>
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
            <input type="text" name="alias" id="Alias" size="30" maxlength="128" value="${currentContent.alias}" tabindex="${maxTabindex+30}" <c:if test="${currentContent.locked || !canEditContentAlias}">readonly</c:if>>
            <div id="LockedHelp" class="ui-state-highlight" style="clear:both; <c:if test="${!currentContent.locked && canEditContentAlias}">display:none</c:if>">
                <ul>
                    <c:if test="${currentContent.locked}">
                        <li style="list-style: none"><kantega:label key="aksess.publishinfo.locked.help"/></li>
                    </c:if>
                    <c:if test="${!canEditContentAlias}">
                        <li style="list-style: none"><kantega:label key="aksess.restricted.alias"/></li>
                    </c:if>
                </ul>
            </div>
        </fieldset>
    </div>
</c:if>
<c:if test="${topicMapsEnabled}">
    <div class="sidebarFieldset">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.topics"/></legend>
            <div id="Topics">
                <div id="TopicList">...</div>
                <c:if test="${canEditContentTopics}">
                    <div id="SelectTopics">
                        <div id="TopicInputContainer">
                            <input type="text" id="TopicInput" size="20" maxlength="128" tabindex="${maxTabindex+40}" accesskey="E">
                        </div>
                        <div id="ChooseTopicButton">
                            <span class="button"><input type="button" class="select" tabindex="${maxTabindex+41}" value="<kantega:label key="aksess.publishinfo.topics.choose"/>"></span>
                        </div>
                    </div>
                </c:if>
                <c:if test="${!canEditContentTopics}">
                    <div class="ui-state-highlight" style="clear:both">
                        <ul>
                            <li style="list-style: none"><kantega:label key="aksess.restricted.topics"/></li>

                        </ul>
                    </div>
                </c:if>

            </div>

    </fieldset>
    </div>
</c:if>
<c:if test="${canChangeTemplate}">
    <div class="sidebarFieldset">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.displaytemplate"/></legend>
            <select name="displaytemplate" tabindex="${maxTabindex+50}" onchange="openaksess.editcontext.setIsModified()">
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
        <c:choose>
            <c:when test="${toggleSearchableEnabled}">
                <div class="row">
                    <input type="checkbox" class="checkbox" name="searchable" id="Searchable" value="true"<c:if test="${currentContent.searchable}"> checked="checked"</c:if> tabindex="${maxTabindex+60}"><label for="Searchable" class="checkbox"><kantega:label key="aksess.publishinfo.searchable"/></label>
                    <div class="clearing"></div>
                </div>
            </c:when>
            <c:otherwise>
                <input type="hidden" name="searchable" value="false">
            </c:otherwise>
        </c:choose>
        <c:if test="${isDeveloper}">
            <div class="row">
                <input type="checkbox" class="checkbox" name="locked" value="true" <c:if test="${currentContent.locked}">checked</c:if> id="Locked" tabindex="${maxTabindex+61}"><label for="Locked" class="checkbox"><kantega:label key="aksess.publishinfo.locked"/></label>
                <div class="clearing"></div>
            </div>
        </c:if>

    </fieldset>
</div>
