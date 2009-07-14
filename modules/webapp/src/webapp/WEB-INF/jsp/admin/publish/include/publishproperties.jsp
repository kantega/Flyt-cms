<%@ page import="no.kantega.publishing.common.data.enums.ExpireAction" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<script type="text/javascript">
    function validatePublishProperties() {
        if (document.myform.alias && document.myform.alias.value == "/") {
            alert("<kantega:label key="aksess.error.aliasroot"/>");
            return false;
        }

        if (document.myform.from_date) {
            if (isDateNotEmpty(document.myform.from_date.value) && checkDate(document.myform.from_date.value) == -1) {
                document.myform.from_date.focus();
                return false;
            }
            if (isTimeNotEmpty(document.myform.from_time.value) && checkTime(document.myform.from_time.value) == -1) {
                document.myform.from_time.focus();
                return false;
            }
        }

        if (document.myform.end_date) {
            if (isDateNotEmpty(document.myform.end_date.value) && checkDate(document.myform.end_date.value) == -1) {
                document.myform.end_date.focus();
                return false;
            }
            if (isTimeNotEmpty(document.myform.end_time.value) && checkTime(document.myform.end_time.value) == -1) {
                document.myform.end_time.focus();
                return false;
            }
        }

        if (document.myform.change_date) {
            if (isDateNotEmpty(document.myform.change_date.value) && checkDate(document.myform.change_date.value) == -1) {
                document.myform.change_date.focus();
                return false;
            }
            if (isTimeNotEmpty(document.myform.change_time.value) && checkTime(document.myform.change_time.value) == -1) {
                document.myform.change_time.focus();
                return false;
            }
        }
        return true;
    }

    $(document).ready(function() {
        $("#ChooseTopicButton > a.button").click(function(event){
            debug("bindTopicButtons(): click ChooseTopicButton");
            event.preventDefault();
            selectTopic(null);
        });

        // Load topics
        var params = new Object();
        updateTopics(params);
    });

</script>

<c:if test="${!isStartPage}">
    <div class="PropertyPane">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.period"/></legend>
            <div id="FromDate">
                <label for="from_date"><kantega:label key="aksess.publishinfo.period.from"/></label>
                <input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.publishDate}"/>" tabindex="500" onFocus="setFocusField(this)" onBlur="blurField()">
                <a href="#" id="chooseFromDate" class="calendar"></a>
                <label for="from_time"><kantega:label key="aksess.publishinfo.period.time"/></label>
                <input type="text" id="from_time" name="from_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.publishDate}"/>" tabindex="501" onFocus="setFocusField(this)" onBlur="blurField()">
            </div>
            <script type="text/javascript">
                Calendar.setup( { inputField  : "from_date", ifFormat : "%d.%m.%Y", button : "chooseFromDate", firstDay: 1 } );
            </script>
            <div id="EndDate">
                <label for="end_date"><kantega:label key="aksess.publishinfo.period.until"/></label>
                <input type="text" id="end_date" name="end_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.expireDate}"/>" tabindex="502" onFocus="setFocusField(this)" onchange="displayExpireAction()" onBlur="blurField()">
                <a href="#" id="chooseEndDate" class="calendar"></a>
                <label for="end_time"><kantega:label key="aksess.publishinfo.period.time"/></label>
                <input type="text" id="end_time" name="end_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.expireDate}"/>" tabindex="503" onFocus="setFocusField(this)" onBlur="blurField()">
            </div>
            <script type="text/javascript">
                Calendar.setup( { inputField  : "end_date", ifFormat : "%d.%m.%Y", button : "chooseEndDate", firstDay: 1 } );
            </script>
            <div id="EndDateAction" <c:if test="${currentContent.expireDate == null}">style="display:none;"</c:if> >
                <label><kantega:label key="aksess.publishinfo.period.action"/></label>
                <div id="EndDateActionChoice">
                    <%
                        int expireAction = ((Content)session.getAttribute("currentContent")).getExpireAction();
                    %>
                    <input name="expireaction" type="radio" id="ExpireActionHide" value="<%=ExpireAction.HIDE%>" <%if (expireAction == ExpireAction.HIDE) out.write(" checked");%>><label for="ExpireActionHide"><kantega:label key="aksess.publishinfo.period.action.hide"/></label></label><br>
                    <input name="expireaction" type="radio" id="ExpireActionArchive" value="<%=ExpireAction.ARCHIVE%>" <%if (expireAction == ExpireAction.ARCHIVE) out.write(" checked");%>><label for="ExpireActionArchive"><kantega:label key="aksess.publishinfo.period.action.archive"/></label><br>
                    <input name="expireaction" type="radio" id="ExpireActionRemind" value="<%=ExpireAction.REMIND%>" <%if (expireAction == ExpireAction.REMIND) out.write(" checked");%>><label for="ExpireActionRemind"><kantega:label key="aksess.publishinfo.period.action.remind"/></label><br>
                    <input name="expireaction" type="radio" id="ExpireActionDelete" value="<%=ExpireAction.DELETE%>" <%if (expireAction == ExpireAction.DELETE) out.write(" checked");%>><label for="ExpireActionDelete"><kantega:label key="aksess.publishinfo.period.action.delete"/></label>
                </div>
            </div>
        </fieldset>
    </div>
</c:if>
<c:if test="${currentContent.id > 0}">
    <div class="PropertyPane">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.change"/></legend>
            <div id="ChangeDate">
                <!--<label for="change_date"><kantega:label key="aksess.publishinfo.change.from"/></label>-->
                <input type="text" id="change_date" name="change_date" size="10" maxlength="10" value="<admin:formatdate date="${currentContent.changeFromDate}"/>" tabindex="500" onFocus="setFocusField(this)" onBlur="blurField()">
                <a href="#" id="chooseChangeDate" class="calendar"></a>
                <label for="change_time"><kantega:label key="aksess.publishinfo.change.time"/></label>
                <input type="text" id="change_time" name="change_time" size="5" maxlength="5" value="<admin:formattime date="${currentContent.changeFromDate}"/>" tabindex="501" onFocus="setFocusField(this)" onBlur="blurField()">
            </div>
            <script type="text/javascript">
                Calendar.setup( { inputField  : "change_date", ifFormat : "%d.%m.%Y", button : "changeFromDate", firstDay: 1 } );
            </script>
        </fieldset>
    </div>
</c:if>
<c:if test="${!isStartPage}">
    <div class="PropertyPane">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.alias"/></legend>
            <input type="text" name="alias" size="30" maxlength="128" value="${currentContent.alias}" tabindex="510">
        </fieldset>
    </div>
</c:if>
<c:if test="${canChangeTemplate}">
    <div class="PropertyPane">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.displaytemplate"/></legend>
            <select name="displaytemplate" tabindex="60" onchange="setIsUpdated()">
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
    <div class="PropertyPane">
        <fieldset>
            <legend><kantega:label key="aksess.publishinfo.topics"/></legend>


            <div id="TopicList">...</div>
            <div id="ChooseTopicButton">
                <a href="#" class="button topics"><span><kantega:label key="aksess.publishinfo.topics.choose"/></span></a>
            </div>

        </fieldset>
    </div>
</c:if>
