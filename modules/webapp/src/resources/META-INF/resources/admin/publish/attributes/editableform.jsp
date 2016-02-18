<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.common.data.attributes.Attribute"%>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<%
    Attribute attribute = (Attribute)request.getAttribute("attribute");
    Content   content   = (Content)request.getAttribute("content");
    String    fieldName = (String)request.getAttribute("fieldName");

    String value = attribute.getValue();
%>
<div class="inputs">
    <admin:box>
        <div id="form_EditableForm">
            <div id="form_FormElements">
                <%=value%>
            </div>
        </div>
        <div>
            <div id="form_PlaceHolder" style="display:none;">
                <div id="EditFormElement">
                    <input type="hidden" id="form_ChildNo" value="">
                    <table>
                        <tr>
                            <td><label for="form_FieldName"><kantega:label key="aksess.formeditor.fieldname"/></label></td>
                            <td><input type="text" id="form_FieldName" name="form_fieldName" size="30" maxlength="250"></td>
                        </tr>
                        <tr valign="top">
                            <td><label for="form_HelpText"><kantega:label key="aksess.formeditor.helptext"/></label></td>
                            <td><textarea type="text" id="form_HelpText" name="form_helpText" wrap="soft" cols="30" rows="3"></textarea></td>
                        </tr>
                        <tr>
                            <td><label for="form_FieldType"><kantega:label key="aksess.formeditor.fieldtype"/></label></td>
                            <td>
                                <select name="type" id="form_FieldType">
                                </select>
                            </td>
                        </tr>
                        <tr class="form_params_text" style="display:none;">
                            <td><label for="form_Length"><kantega:label key="aksess.formeditor.fieldsize"/></label></td>
                            <td>
                                <input type="text" id="form_Length" size="2" maxlength="2" name="form_Length"> <kantega:label key="aksess.formeditor.characters"/>
                            </td>
                        </tr>
                        <tr class="form_params_text" style="display:none;">
                            <td><label for="form_MaxLength"><kantega:label key="aksess.formeditor.fieldmaxlength"/></label></td>
                            <td>
                                <input type="text" id="form_MaxLength" size="2" maxlength="2" name="form_MaxLength"> <kantega:label key="aksess.formeditor.characters"/>
                            </td>
                        </tr>
                        <tr class="form_params_text" style="display:none;">
                            <td><label for="form_Validator"><kantega:label key="aksess.formeditor.validator"/></label></td>
                            <td>
                                <select id="form_Validator">
                                    <option value=""><kantega:label key="aksess.formeditor.validator.text"/></option>
                                    <option value="date"><kantega:label key="aksess.formeditor.validator.date"/></option>
                                    <option value="email"><kantega:label key="aksess.formeditor.validator.email"/></option>
                                    <option value="number"><kantega:label key="aksess.formeditor.validator.number"/></option>
                                    <option value="norwegianssn"><kantega:label key="aksess.formeditor.validator.norwegianssn"/></option>
                                    <option value="regularexpression"><kantega:label key="aksess.formeditor.validator.regularexpression"/></option>
                                    <option value="norwegianphonenumber"><kantega:label key="aksess.formeditor.validator.norwegianphonenumber"/></option>
                                </select>
                            </td>
                        </tr>
                        <tr class="form_validatorparams_regularexpression" style="display:none;">
                            <td><label for="form_RegEx"><kantega:label key="aksess.formeditor.fieldregex"/></label></td>
                            <td>
                                <input type="text" id="form_RegEx" name="form_RegEx">
                            </td>
                        </tr>
                        <tr class="form_validatorparams_date" style="display:none;">
                            <td><label for="form_DateFormat"><kantega:label key="aksess.formeditor.dateformat"/></label></td>
                            <td>
                                <select id="form_DateFormat">
                                    <option value="dd.MM.yyyy"><kantega:label key="aksess.formeditor.dateformat.dd"/>.<kantega:label key="aksess.formeditor.dateformat.mm"/>.<kantega:label key="aksess.formeditor.dateformat.yy"/><kantega:label key="aksess.formeditor.dateformat.yy"/></option>
                                    <option value="dd.MM.yy"><kantega:label key="aksess.formeditor.dateformat.dd"/>.<kantega:label key="aksess.formeditor.dateformat.mm"/>.<kantega:label key="aksess.formeditor.dateformat.yy"/></option>
                                    <option value="dd/MM/yyyy"><kantega:label key="aksess.formeditor.dateformat.dd"/>/<kantega:label key="aksess.formeditor.dateformat.mm"/>/<kantega:label key="aksess.formeditor.dateformat.yy"/><kantega:label key="aksess.formeditor.dateformat.yy"/></option>
                                    <option value="MM/dd/yyyy"><kantega:label key="aksess.formeditor.dateformat.mm"/>/<kantega:label key="aksess.formeditor.dateformat.dd"/>/<kantega:label key="aksess.formeditor.dateformat.yy"/><kantega:label key="aksess.formeditor.dateformat.yy"/></option>
                                </select>
                            </td>
                        </tr>
                        <tr class="form_params_textarea" style="display:none;">
                            <td><label for="form_Rows"><kantega:label key="aksess.formeditor.fieldrows"/></label></td>
                            <td>
                                <input type="text" id="form_Rows" size="2" maxlength="2" name="form_Rows"> <kantega:label key="aksess.formeditor.rows"/>
                            </td>
                        </tr>
                        <tr class="form_params_textarea" style="display:none;">
                            <td><label for="form_Cols"><kantega:label key="aksess.formeditor.fieldcols"/></label></td>
                            <td>
                                <input type="text" id="form_Cols" size="2" maxlength="2" name="form_Cols"> <kantega:label key="aksess.formeditor.cols"/>
                            </td>
                        </tr>
                        <tr class="form_params_list" style="display:none;" valign="top">
                            <td><label><kantega:label key="aksess.formeditor.choices"/></label></td>
                            <td>
                                <div id="form_Values"></div>
                                <a href="form_AddElement" id="form_AddElement" class="button"><kantega:label key="aksess.formeditor.addchoice"/></a>
                            </td>
                        </tr>
                        <tr class="form_params_hidden" style="display:none;">
                            <td><label for="form_HiddenValue"><kantega:label key="aksess.formeditor.hiddenvalue"/></label></td>
                            <td>
                                <input type="text" id="form_HiddenValue" size="30" maxlength="128" name="form_HiddenValue">
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <c:set var="mailconfirmationEnabled"><aksess:getconfig key="formengine.mailconfirmation.enabled" default="false"/></c:set>
                                <c:if test="${mailconfirmationEnabled}">
                                    <div class="form_validatorparams_email" style="display:none;">
                                        <input type="checkbox" name="form_IsRecipientEmail" id="form_IsRecipientEmail"><label for="form_IsRecipientEmail"><kantega:label key="aksess.formeditor.isrecipientemail"/></label><br>
                                    </div>
                                </c:if>
                                <div class="form_params_select" style="display:none;">
                                    <input type="checkbox" name="form_FirstValueBlank" id="form_FirstValueBlank"><label for="form_FirstValueBlank"><kantega:label key="aksess.formeditor.firstvalueblank"/></label><br>
                                </div>
                                <input type="checkbox" name="form_FieldMandatory" id="form_FieldMandatory"><label for="form_FieldMandatory"><kantega:label key="aksess.formeditor.mandatory"/></label><br>
                                <input type="checkbox" name="form_NoBreak" id="form_NoBreak"><label for="form_NoBreak"><kantega:label key="aksess.formeditor.nobreak"/></label><br>
                                <input type="checkbox" name="form_FieldReadonly" id="form_FieldReadonly" checked="checked"><label for="form_FieldReadonly"><kantega:label key="aksess.formeditor.readonly"/></label>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" align="right">
                                <input type="button" id="form_SaveFormElement" value="<kantega:label key="aksess.button.save"/>">
                                <input type="button" id="form_CancelFormElement" value="<kantega:label key="aksess.button.cancel"/>">
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <div id="form_TextPlaceHolder" style="display:none;">
                <div id="EditFormText">
                    <input type="hidden" id="form_TextChildNo" value="">
                    <table>
                        <tr valign="top">
                            <td><label for="form_Text" ><kantega:label key="aksess.formeditor.content"/></label></td>
                            <td>
                                <textarea id="form_Text" cols="50" rows="6"></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" align="right">
                                <input type="button" id="form_SaveFormText" value="<kantega:label key="aksess.button.save"/>">
                                <input type="button" id="form_CancelFormText" value="<kantega:label key="aksess.button.cancel"/>">
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <div id="form_SectionPlaceHolder" style="display: none;">
                <div id="EditFormSection">
                    <input type="hidden" id="form_SectionChildNo" value="">
                    <table>
                        <tr valign="top">
                            <td><label for="form_SectionTitle" ><kantega:label key="aksess.formeditor.section.title"/></label></td>
                            <td>
                                <input type="text" id="form_SectionTitle" size="30" maxlength="128" name="form_SectionTitle">
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" align="right">
                                <input type="button" id="form_SaveFormSection" value="<kantega:label key="aksess.button.save"/>">
                                <input type="button" id="form_CancelFormSection" value="<kantega:label key="aksess.button.cancel"/>">
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
        <div>
            <textarea name="<%=fieldName%>" rows="4" cols="20" style="display:none;" id="form_Value"></textarea>
        </div>
    </admin:box>
</div>
<div class="buttonGroup">
    <a href="form_NewElement" id="form_NewElement" class="button"><span class="add"><kantega:label key="aksess.formeditor.newfield"/></span></a>
    <a href="form_NewText" id="form_NewText" class="button"><span class="add"><kantega:label key="aksess.formeditor.newtext"/></span></a>
    <a href="form_NewSection" id="form_NewSection" class="button"><span class="add"><kantega:label key="aksess.formeditor.newsection"/></span></a>
</div>
<div class="buttonGroup">
    <%
        if (!content.isNew()) {
    %>
    <a href="FormSubmissionsExportExcel.action?formId=<%=content.getId()%>" target="_new" class="button"><span class=""><kantega:label key="aksess.formeditor.exportformdata"/></span></a>
    <%
        if (SecuritySession.getInstance(request).isAuthorized(content, Privilege.APPROVE_CONTENT)) {
    %>
    <a href="Javascript:formDeleteSubmissions(<%=content.getId()%>)" class="button" tabindex="<%=attribute.getTabIndex()%>"><span class="delete"><kantega:label key="aksess.formeditor.deleteformdata"/></span></a>
    <%
            }
        }
    %>
</div>
