<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
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
    <script type="text/javascript" language="Javascript" src="../js/date.jsp"></script>
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
                if (validatePublishProperties()) {
                    debug("showdisplayperiod - set new date:" + fromDate + "-" + endDate);
                    $.post("../publish/UpdateDisplayPeriod.action", {associationId:${content.association.id}, from_date: fromDate, from_time:fromTime, end_date:endDate, end_time:endTime, updateChildren: updateChild}, function(data) {
                        if (data.error) {
                            alert('<kantega:label key="aksess.error.generic"/>');
                        } else {
                            setTimeout("closeWindow()", 10);
                        }

                    }, "json");
                }
            }
            return false;
        }

    	$(function() {
	    	$("#from_date").datepicker();
            $("#end_date").datepicker();
	    });               

        function validatePublishProperties() {
            if (openaksess.dateutils.isDateNotEmpty($("#from_date").val()) && openaksess.dateutils.checkDate($("#from_date").val()) == -1) {
                $("#from_date").focus();
                return false;
            }
            if (openaksess.dateutils.isTimeNotEmpty($("#from_time").val()) && openaksess.dateutils.checkTime($("#from_time").val()) == -1) {
                $("#from_time").focus();
                return false;
            }

            if (openaksess.dateutils.isDateNotEmpty($("#end_date").val()) && openaksess.dateutils.checkDate($("#end_date").val()) == -1) {
                $("#end_date").focus();
                return false;
            }
            if (openaksess.dateutils.isTimeNotEmpty($("#end_time").val()) && openaksess.dateutils.checkTime($("#end_time").val()) == -1) {
                $("#end_time").focus();
                return false;
            }
            return true;
        }
    </script>
</kantega:section>

<kantega:section id="body">
        <div class="fieldset">
            <fieldset>
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
                <div id="UpdateChildren">
                    <div class="row">
                        <input type="checkbox" name="updateChildren" value="true" id="update_Children" class="radio">
                        <label for="update_Children" class="radio"><kantega:label key="aksess.publishinfo.period.updatechildren"/></label>
                        <div class="clearing"></div>
                    </div>

                </div>
                <div class="buttonGroup">
                    <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                    <span class="button"><input type="button" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
                </div>

            </fieldset>
        </div>
</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>