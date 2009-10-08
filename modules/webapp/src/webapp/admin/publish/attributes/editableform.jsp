<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.data.attributes.Attribute"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege" %>
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
<tr>
    <td class="inpHeading">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><b><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></b></td>
                <%
                    if (content.getId() != -1) {
                %>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="FormSubmissionsExportExcel.action?formId=<%=content.getId()%>" target="_new"><img src="../bitmaps/common/buttons/mini_vis.gif" border="0"></a></td>
                <td><a href="FormSubmissionsExportExcel.action?formId=<%=content.getId()%>" target="_new" class="button" tabindex="<%=attribute.getTabIndex()%>"><kantega:label key="aksess.formeditor.exportformdata"/></a></td>
                <%
                        if (SecuritySession.getInstance(request).isAuthorized(content, Privilege.APPROVE_CONTENT)) {
                %>
                <td><img src="../bitmaps/common/textseparator.gif"></td>
                <td><a href="Javascript:formDeleteSubmissions(<%=content.getId()%>)"><img src="../bitmaps/common/buttons/mini_slett.gif" border="0"></a></td>
                <td><a href="Javascript:formDeleteSubmissions(<%=content.getId()%>)" class="button" tabindex="<%=attribute.getTabIndex()%>"><kantega:label key="aksess.formeditor.deleteformdata"/></a></td>
                <%

                        }
                    }
                %>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
</tr>
<tr>
    <td>
        <div id="form_EditableForm">
            <div id="form_FormElements">
                <%=value%>
            </div>
            <div id="form_NewElement"><a href="form_NewElement" class="button"><kantega:label key="aksess.formeditor.newfield"/></a></div>
        </div>
        <div>
            <div id="form_PlaceHolder" style="display:none;">
                <div id="EditFormElement">
                    <input type="hidden" id="form_ChildNo" value="">
                    <table>
                        <tr>
                            <td><label for="form_FieldName"><kantega:label key="aksess.formeditor.fieldname"/></label></td>
                            <td><input type="text" id="form_FieldName" name="form_fieldName" size="30" maxlength="40"></td>
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
                            <td><label for="form_Validator"><kantega:label key="aksess.formeditor.validator"/></label></td>
                            <td>
                                <select id="form_Validator">
                                    <option value=""><kantega:label key="aksess.formeditor.validator.text"/></option>
                                    <option value="email"><kantega:label key="aksess.formeditor.validator.email"/></option>
                                    <option value="number"><kantega:label key="aksess.formeditor.validator.number"/></option>
                                </select>
                            </td>
                        </tr>
                        <tr class="form_params_textarea" style="display:none;">
                            <td><label for="form_Rows"><kantega:label key="aksess.formeditor.fieldrows"/></label></td>
                            <td>
                                <input type="text" id="form_Rows" size="2" maxlength="2" name="form_Rows"> <kantega:label key="aksess.formeditor.rows"/>
                            </td>
                        </tr>
                        <tr class="form_params_list" style="display:none;" valign="top">
                            <td><label><kantega:label key="aksess.formeditor.choices"/></label></td>
                            <td>
                                <div id="form_Values"></div>
                                <a href="form_AddElement" id="form_AddElement" class="button"><kantega:label key="aksess.formeditor.addchoice"/></a>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <input type="checkbox" name="form_FieldMandatory" id="form_FieldMandatory"><label for="form_FieldMandatory"><kantega:label key="aksess.formeditor.mandatory"/></label><br>
                                <input type="checkbox" name="form_NoBreak" id="form_NoBreak"><label for="form_NoBreak"><kantega:label key="aksess.formeditor.nobreak"/></label>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" align="right">
                                <input type="button" id="form_SaveFormElement" value="<kantega:label key="aksess.button.lagre"/>">
                                <input type="button" id="form_Cancel" value="<kantega:label key="aksess.button.avbryt"/>">
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
        <div>
            <textarea name="<%=fieldName%>" rows="4" cols="20" style="display:none;" id="form_Value"></textarea>
        </div>
    </td>
</tr>