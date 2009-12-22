<%@ page import="no.kantega.publishing.common.data.enums.ExpireAction" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<script type="text/javascript">
    function displayExpireAction() {
       var d = $("#end_date").val();
       if (DateFunctions.isDateNotEmpty(d)) {
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
            if (DateFunctions.isDateNotEmpty(document.myform.from_date.value) && DateFunctions.checkDate(document.myform.from_date.value) == -1) {
                document.myform.from_date.focus();
                return false;
            }
            if (DateFunctions.isTimeNotEmpty(document.myform.from_time.value) && DateFunctions.checkTime(document.myform.from_time.value) == -1) {
                document.myform.from_time.focus();
                return false;
            }
        }

        if (document.myform.end_date) {
            if (DateFunctions.isDateNotEmpty(document.myform.end_date.value) && DateFunctions.checkDate(document.myform.end_date.value) == -1) {
                document.myform.end_date.focus();
                return false;
            }
            if (DateFunctions.isTimeNotEmpty(document.myform.end_time.value) && DateFunctions.checkTime(document.myform.end_time.value) == -1) {
                document.myform.end_time.focus();
                return false;
            }
        }

        if (document.myform.change_date) {
            if (DateFunctions.isDateNotEmpty(document.myform.change_date.value) && DateFunctions.checkDate(document.myform.change_date.value) == -1) {
                document.myform.change_date.focus();
                return false;
            }
            if (DateFunctions.isTimeNotEmpty(document.myform.change_time.value) && DateFunctions.checkTime(document.myform.change_time.value) == -1) {
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
        debug("bindTopicButtons(): bind ChooseTopicButton");
        $("#ChooseTopicButton input").click(function(event){
            debug("bindTopicButtons(): click ChooseTopicButton");
            selectTopic(null, true);
        });

        // Load topics
        var params = new Object();
        updateTopics(params);
    });

</script>

<c:if test="${!isStartPage}">
    <div class="sidebarFieldset">
        <fieldset>
            <h1><kantega:label key="aksess.publishinfo.period"/></h1>
            <table class="noborder" id="DisplayPeriod">
                <tr>
                    <td><label for="from_date"><kantega:label key="aksess.publishinfo.period.from"/></label></td>
                    <td><input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.publishDate}"/>" tabindex="500" onFocus="setFocusField(this)" onBlur="blurField()">
                    <td><label for="from_time"><kantega:label key="aksess.publishinfo.period.time"/></label></td>
                    <td><input type="text" id="from_time" name="from_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.publishDate}"/>" tabindex="501" onFocus="setFocusField(this)" onBlur="blurField()"></td>
                </tr>
                <tr>
                    <td><label for="end_date"><kantega:label key="aksess.publishinfo.period.until"/></label></td>
                    <td><input type="text" id="end_date" name="end_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.expireDate}"/>" tabindex="502" onFocus="setFocusField(this)" onchange="displayExpireAction()" onBlur="blurField()"></td>
                    <td><label for="end_time"><kantega:label key="aksess.publishinfo.period.time"/></label></td>
                    <td><input type="text" id="end_time" name="end_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.expireDate}"/>" tabindex="503" onFocus="setFocusField(this)" onBlur="blurField()"></td>
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
<c:if test="${currentContent.id > 0 && canPublish}">
    <div class="sidebarFieldset">
        <fieldset>
            <h1><kantega:label key="aksess.publishinfo.change"/></h1>
            <table class="noborder" id="ChangeDate">
                <tr>
                    <td><!--<label for="change_date"><kantega:label key="aksess.publishinfo.change.from"/></label>--></td>
                    <td><input type="text" id="change_date" name="change_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.changeFromDate}"/>" tabindex="500" onFocus="setFocusField(this)" onBlur="blurField()"></td>
                    <td><label for="change_time"><kantega:label key="aksess.publishinfo.change.time"/></label></td>
                    <td><input type="text" id="change_time" name="change_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.changeFromDate}"/>" tabindex="501" onFocus="setFocusField(this)" onBlur="blurField()"></td>
                </tr>
            </table>
        </fieldset>
    </div>
</c:if>
<c:if test="${!isStartPage}">
    <div class="sidebarFieldset">
        <fieldset>
            <h1><kantega:label key="aksess.publishinfo.alias"/></h1>
            <input type="text" name="alias" size="30" maxlength="128" value="${currentContent.alias}" tabindex="510">
        </fieldset>
    </div>
</c:if>
<c:if test="${canChangeTemplate}">
    <div class="sidebarFieldset">
        <fieldset>
            <h1><kantega:label key="aksess.publishinfo.displaytemplate"/></h1>
            <select name="displaytemplate" tabindex="60" onchange="setIsModified()">
                <c:forEach var="template" items="${allowedTemplates}">
                    <c:set var="templateName" value="${template.name}"/>
                    <c:if test="template.contentTemplate.id == current.contentTemplateId">
                        <c:set var="templateName" value="${template.name} *"/>
                    </c:if>
                    <option value="${template.id}" <c:if test="${template.id == currentContent.displayTemplateId}"> selected</c:if>>${templateName}</option>
                </c:forEach>
                <c:if test="${isAdmin}">
                    <div class=helpText><kantega:label key="aksess.editpublishinfo.displaytemplate.hjelp"/></div>
                </c:if>
            </select>
        </fieldset>
    </div>
</c:if>
<c:if test="${topicMapsEnabled}">
    <div class="sidebarFieldset">
        <fieldset>
            <h1><kantega:label key="aksess.publishinfo.topics"/></h1>

            <div id="TopicList">...</div>
            <div id="ChooseTopicButton">
                <span class="button"><input type="button" class="select" value="<kantega:label key="aksess.publishinfo.topics.choose"/>"></span>
            </div>

        </fieldset>
    </div>
</c:if>
