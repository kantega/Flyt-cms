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
<kantega:section id="title"><kantega:label key="aksess.publishinfo.period"/></kantega:section>

<kantega:section id="head">
    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { };
        }
        if (typeof properties.date == 'undefined') {
            properties.date = { };
        }
        properties.date['labels'] = {
            feilformat : "<kantega:label key="aksess.js.advarsel.dato.feilformat"/>",
            skilletegn : "<kantega:label key="aksess.js.advarsel.dato.skilletegn"/>",
            feildag : "<kantega:label key="aksess.js.advarsel.dato.feildag"/>",
            feilmaned : "<kantega:label key="aksess.js.advarsel.dato.feilmaned"/>",
            feilar : "<kantega:label key="aksess.js.advarsel.dato.feilar"/>",
            feildagtall : "<kantega:label key="aksess.js.advarsel.dato.feildagtall"/>",
            feilmanedtall : "<kantega:label key="aksess.js.advarsel.dato.feilmanedtall"/>",
            feilartall : "<kantega:label key="aksess.js.advarsel.dato.feilartall"/>",
            feilskuddarmaned : "<kantega:label key="aksess.js.advarsel.dato.feilskuddarmaned"/>",
            feiltidsformatKolon : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.kolon"/>",
            feiltidsformat : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat"/>",
            feiltidsformatMinuttermindre : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.minuttermindre"/>",
            feiltidsformatMinutterstorre : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.minutterstorre"/>",
            feiltidsformatTimermindre : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.timermindre"/>",
            feiltidsformatTimerstorre : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.timerstorre"/>"
        };
    </script>
    <script type="text/javascript" src="../js/date.js"></script>
    <script type="text/javascript">
        var hasSubmitted = false;
        function buttonOkPressed() {
            if (!hasSubmitted) {
                hasSubmitted = true;
                var fromDate = $("#from_date").val();
                var fromTime = $("#from_time").val();
                var endDate = $("#end_date").val();
                var endTime = $("#end_time").val();
                var updateChild = $("#update_Children").is(":checked");

                if (updateChild && !confirm("<kantega:label key="aksess.publishinfo.period.updatechildren.confirm"/>")) {
                    return false;
                }

                if (validatePublishProperties()) {
                    openaksess.common.debug("showdisplayperiod - set new date:" + fromDate + "-" + endDate);
                    $.post("../publish/UpdateDisplayPeriod.action", {associationId:${content.association.id}, from_date: fromDate, from_time:fromTime, end_date:endDate, end_time:endTime, updateChildren: updateChild}, function(data) {
                        if (data.error) {
                            alert('<kantega:label key="aksess.error.generic" escapeJavascript="true"/>');
                        } else {
                            getParent().openaksess.navigate.updateNavigator(getParent().openaksess.navigate.getCurrentItemIdentifier(), true);
                            setTimeout("closeWindow()", 10);
                        }

                    }, "json");
                }
            }
            return false;
        }

        $(function() {
            $("#from_date").datepicker({
                dateFormat: "<%=Aksess.getDefaultDateFormatJS()%>"
            });
            $("#end_date").datepicker({
                dateFormat: "<%=Aksess.getDefaultDateFormatJS()%>"
            });
        });

        function validatePublishProperties() {
            var $fromDate = $("#from_date");
            if (openaksess.dateutils.isDateNotEmpty($fromDate.val()) && openaksess.dateutils.checkDate($fromDate.val()) == -1) {
                $fromDate.focus();
                return false;
            }
            var $fromTime = $("#from_time");
            if (openaksess.dateutils.isTimeNotEmpty($fromTime.val()) && openaksess.dateutils.checkTime($fromTime.val()) == -1) {
                $fromTime.focus();
                return false;
            }

            var $endDate = $("#end_date");
            if (openaksess.dateutils.isDateNotEmpty($endDate.val()) && openaksess.dateutils.checkDate($endDate.val()) == -1) {
                $endDate.focus();
                return false;
            }
            var $endTime = $("#end_time");
            if (openaksess.dateutils.isTimeNotEmpty($endTime.val()) && openaksess.dateutils.checkTime($endTime.val()) == -1) {
                $endTime.focus();
                return false;
            }
            return true;
        }
    </script>
</kantega:section>

<kantega:section id="body">
    <table class="noborder" style="margin-top:20px; margin-bottom:20px">
        <tr>
            <td><label for="from_date"><kantega:label key="aksess.publishinfo.period.from"/></label></td>
            <td>
                <input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="<admin:formatdate date="${content.publishDate}"/>">
            </td>
            <td><label for="from_time"><kantega:label key="aksess.publishinfo.period.time"/></label></td>
            <td><input type="text" id="from_time" name="from_time" size="5" maxlength="5" value="<admin:formattime date="${content.publishDate}"/>"></td>
        </tr>
        <tr>
            <td><label for="end_date"><kantega:label key="aksess.publishinfo.period.until"/></label></td>
            <td>
                <input type="text" id="end_date" name="end_date" size="10" maxlength="10" value="<admin:formatdate date="${content.expireDate}"/>">
            </td>
            <td>
                <label for="end_time"><kantega:label key="aksess.publishinfo.period.time"/></label>
            </td>
            <td>
                <input type="text" id="end_time" name="end_time" size="5" maxlength="5" value="<admin:formattime date="${content.expireDate}"/>">
            </td>
        </tr>
    </table>

    <c:if test="${canUpdateSubpages}">
        <div id="UpdateChildren">
            <div class="row">
                <input type="checkbox" name="updateChildren" value="true" id="update_Children" class="radio">
                <label for="update_Children" class="radio"><kantega:label key="aksess.publishinfo.period.updatechildren"/></label>
                <div class="clearing"></div>
            </div>
        </div>
    </c:if>

    <div class="buttonGroup">
        <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
        <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
    </div>

</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>
