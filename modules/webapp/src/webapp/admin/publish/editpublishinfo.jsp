<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.data.*" %>
<%@ page import="no.kantega.publishing.common.data.enums.ContentType" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.data.enums.ExpireAction" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.common.cache.AssociationCategoryCache" %>
<%@ include file="../include/jsp_header.jsf" %>
<%@ include file="../include/edit_header.jsf" %>
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
    boolean isStartPage = false;

    String alias = current.getAlias();
    if (alias == null) alias = "";
    if (current.getAssociation().getParentAssociationId() == 0) {
        isStartPage = true;
    }

    int selectedAssociationCategory = param.getInt("associationCategory");

    // Get possible association categories
    ContentTemplate template = aksessService.getContentTemplate(current.getContentTemplateId());
    List<AssociationCategory> allowedAssociationsIds = template.getAssociationCategories();
    List<AssociationCategory> allowedAssociations  = new ArrayList<AssociationCategory>();
    for (AssociationCategory aId : allowedAssociationsIds) {
        allowedAssociations.add(AssociationCategoryCache.getAssociationCategoryById(aId.getId()));
    }
    List allowedTemplates = null;

    if (current.getType() == ContentType.PAGE) {
        allowedTemplates = aksessService.getAllowedDisplayTemplates(current);
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>Redigering av publiseringsinfo</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <link rel="stylesheet" type="text/css" href="../css/calendar-<%=skin%>.css">
</head>
<script type="text/javascript" language="Javascript" src="../js/common.js"></script>
<script type="text/javascript" language="Javascript" src="../js/edit.jsp"></script>
<script type="text/javascript" language="Javascript" src="../js/date.jsp"></script>
<script type="text/javascript" src="../js/calendar/calendar.js"></script>
<script type="text/javascript" src="../js/calendar/calendar-en.js"></script>
<script type="text/javascript" src="../js/calendar/calendar-no.js" charset="utf-8"></script>
<script type="text/javascript" src="../js/calendar/calendar-setup.js"></script>

<script type="text/javascript" language="Javascript">
    var isSortOrderModified = false
    var hasSubmitted = false;

    function initialize() {
        try {
            // document.myform.elements[0].focus()
        } catch (e) {
            // Usynlig element som ikke kan fе fokus
        }
    }


    function moveItem(dir)
    {
        isSortOrderModified = true;
        var myList = document.myform.tmpsortlist;

        for (var i = 0; i < myList.options.length; i++) {
            if (myList.options[i].selected) {
                if (dir < 0) {
                    if (i == 0) {
                        return;
                    }
                } else if (dir > 0) {
                    if (i == myList.options.length - 1) {
                        return;
                    }
                }

                var tmpText = myList.options[i + dir].text;
                var tmpValue = myList.options[i + dir].value;

                myList.options[i + dir].text = myList.options[i].text;
                myList.options[i + dir].value = myList.options[i].value;

                myList.options[i].text = tmpText;
                myList.options[i].value = tmpValue;

                myList.options[i + dir].selected = true;

                return;
            }
        }

        setIsUpdated();
    }

    function saveContent(status) {

        if (document.myform.alias && document.myform.alias.value == "/") {
            alert("Du kan ikke bruke kun / som alias.  Det er adressen til hjemmesida");
        }

        if (document.myform.from_date) {
            // Sjekk dato+tid
            if (document.myform.from_date.value != "dd.mm.ееее" && document.myform.from_date.value != "" && checkDate(document.myform.from_date.value) == -1) {
                document.myform.from_date.focus();
                return;
            }
            if (document.myform.from_time.value != "tt:mm" && document.myform.from_time.value != "" && checkTime(document.myform.from_time.value) == -1) {
                document.myform.from_time.focus();
                return;
            }
        }

        if (document.myform.end_date) {
            if (document.myform.end_date.value != "dd.mm.ееее" && document.myform.end_date.value != "" && checkDate(document.myform.end_date.value) == -1) {
                document.myform.end_date.focus();
                return;
            }
            if (document.myform.end_time.value != "tt:mm" && document.myform.end_time.value != "" && checkTime(document.myform.end_time.value) == -1) {
                document.myform.end_time.focus();
                return;
            }
        }

        if (isSortOrderModified) {
            // Trenger ikke е trigger oppdatering av masse undersider med mindre de mе...
            var myList = document.myform.tmpsortlist;
            var val = "";
            for (var i = 0; i < myList.options.length; i++) {
                val = val + myList.options[i].value + ";";
            }
            document.myform.sortlist.value = val;
        }

        if (!hasSubmitted) {
            hasSubmitted = true;
            document.myform.status.value = status;
            document.myform.submit();
        }
    }

    function changeAssociationCategory() {
        location = "editpublishinfo.jsp?associationCategory=" + getSelectedValue(document.myform.associationCategory);
    }


    function lockAlias() {
        var locked = document.myform.locked;
        var help = document.getElementById("aliasHelp");
        var helpLocked = document.getElementById("aliasHelpLocked");

        if (locked.checked) {
            document.myform.alias.readOnly = true;
            help.style.display = "none";
            helpLocked.style.display = "block";
        } else {
            document.myform.alias.readOnly = false;
            help.style.display = "block";
            helpLocked.style.display = "none";
        }
    }

    function showAssociations() {
        var associationswin = window.open("../popups/showassociations.jsp?refresh=" + getRefresh(), "associationswin", "toolbar=no,width=400,height=300,resizable=yes,scrollbars=yes");
        associationswin.focus();
    }

</script>


<body onload="initialize()" class="bodyWithMargin">
<%@ include file="../include/infobox.jsf" %>
<form name="myform" action="SavePublishInfo.action" target="content" method="post">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
    <%
        DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());
        DateFormat tf = new SimpleDateFormat(Aksess.getDefaultTimeFormat());

        String startDate = "dd.mm.ееее";
        String startTime = "tt:mm";
        Date start = current.getPublishDate();
        if (start != null) {
            startDate = df.format(start);
            startTime = tf.format(start);
        }

        String expireDate = "dd.mm.ееее";
        String expireTime = "tt:mm";
        Date expire = current.getExpireDate();
        if (expire != null) {
            expireDate = df.format(expire);
            expireTime = tf.format(expire);
        }
    %>
        <!-- Gyldighet -->
<%
    if (!isStartPage) {
%>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.editpublishinfo.publishtime"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="560">
                    <tr>
                        <td width="80"><kantega:label key="aksess.editpublishinfo.publishtime.fra"/>&nbsp;</td>
                        <td width="100"><input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="<%=startDate%>" tabindex="10" onFocus="setFocusField(this)" onBlur="blurField()"></td>
                        <td width="80">
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><a href="#" id="velgdatofrom_date0"><img src="../bitmaps/common/buttons/mini_velg.gif" border="0" alt=""></a></td>
                                    <td><a href="#" id="velgdatofrom_date1" class="button"><kantega:label key="aksess.button.velg"/></a>&nbsp;</td>
                                </tr>
                            </table>
                        </td>
                        <td width="340"><input type="text" id="from_time" name="from_time" size="5" maxlength="5" value="<%=startTime%>" tabindex="20" onFocus="setFocusField(this)" onBlur="blurField()"></td>
                    </tr>
                    <script type="text/javascript">
                        Calendar.setup( { inputField  : "from_date", ifFormat : "%d.%m.%Y", button : "velgdatofrom_date0", firstDay: 1 } );
                        Calendar.setup( { inputField  : "from_date", ifFormat : "%d.%m.%Y", button : "velgdatofrom_date1", firstDay: 1 } );
                    </script>
                    <tr>
                        <td><kantega:label key="aksess.editpublishinfo.publishtime.til"/></td>
                        <td><input type="text" id="end_date" name="end_date" size="10" maxlength="10" value="<%=expireDate%>" tabindex="30" onFocus="setFocusField(this)" onBlur="blurField()"></td>
                        <td>
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><a href="#" id="velgdatoend_date0"><img src="../bitmaps/common/buttons/mini_velg.gif" border="0" alt=""></a></td>
                                    <td><a href="#" id="velgdatoend_date1" class="button"><kantega:label key="aksess.button.velg"/></a></td>
                                </tr>
                            </table>
                        </td>
                        <td><input type="text" id="end_time" name="end_time" size="5" maxlength="5" value="<%=expireTime%>" tabindex="40" onFocus="setFocusField(this)" onBlur="blurField()"></td>
                    </tr>
                    <script type="text/javascript">
                        Calendar.setup( { inputField  : "end_date", ifFormat : "%d.%m.%Y", button : "velgdatoend_date0", firstDay: 1 } );
                        Calendar.setup( { inputField  : "end_date", ifFormat : "%d.%m.%Y", button : "velgdatoend_date1", firstDay: 1 } );
                    </script>
                    <tr>
                        <td colspan="4">&nbsp;</td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <b><kantega:label key="aksess.editpublishinfo.publishtime.action"/></b>
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td colspan="3">
                            <%
                                int expireAction = current.getExpireAction();
                            %>
                            <input name="expireaction" type="radio" value="<%=ExpireAction.HIDE%>" <%if (expireAction == ExpireAction.HIDE) out.write(" checked");%>><kantega:label key="aksess.editpublishinfo.publishtime.action.hide"/><br>
                            <input name="expireaction" type="radio" value="<%=ExpireAction.ARCHIVE%>" <%if (expireAction == ExpireAction.ARCHIVE) out.write(" checked");%>><kantega:label key="aksess.editpublishinfo.publishtime.action.archive"/><br>
                            <input name="expireaction" type="radio" value="<%=ExpireAction.REMIND%>" <%if (expireAction == ExpireAction.REMIND) out.write(" checked");%>><kantega:label key="aksess.editpublishinfo.publishtime.action.remind"/><br>
                            <input name="expireaction" type="radio" value="<%=ExpireAction.DELETE%>" <%if (expireAction == ExpireAction.DELETE) out.write(" checked");%>><kantega:label key="aksess.editpublishinfo.publishtime.action.delete"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.editpublishinfo.alias"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td><input type="text" name="alias" size="62" style="width:600px;" value="<%=alias%>" maxlength="62" tabindex="50" onFocus="setFocusField(this)" onBlur="blurField()"<% if (current.isLocked()) out.write("readonly=\"readonly\"");%>><br>
                <div id="aliasHelp" class="helpText"<% if (current.isLocked()) out.write("style=\"display:none\"");%>><kantega:label key="aksess.editpublishinfo.alias.help"/></div>
                <div id="aliasHelpLocked" class="helpText"<% if (!current.isLocked()) out.write("style=\"display:none\"");%>><kantega:label key="aksess.editpublishinfo.alias.locked.help"/></div>
                <%
                    if (securitySession.isUserInRole(Aksess.getDeveloperRole())) {
                %>
                        <br><input type="checkbox" name="locked" value="true" <% if (current.isLocked()) out.write("checked=\"checked\"");%> id="locked" onclick="lockAlias()"> <label for="locked" class="radio"><kantega:label key="aksess.editpublishinfo.alias.locked"/></label>
                <%
                    }
                %>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
<%
    }
%>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.editpublishinfo.displayprops"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="600">
                    <tr>
                        <td width="80"><img src="../bitmaps/blank.gif" width="80" height="1"></td>
                        <td width="520"><img src="../bitmaps/blank.gif" width="520" height="1"></td>
                    </tr>
                    <%
                        if (allowedTemplates != null && allowedTemplates.size() > 0)  {
                    %>
                    <tr>
                        <td><kantega:label key="aksess.editpublishinfo.displaytemplate"/></td>
                        <td>
                            <select name="displaytemplate" style="width: 300px;" tabindex="60" onchange="setIsUpdated()">
                            <%
                                for (int i=0; i < allowedTemplates.size(); i++) {
                                    DisplayTemplate t = (DisplayTemplate)allowedTemplates.get(i);
                                    String name = t.getName();
                                    if (t.getContentTemplate().getId() == current.getContentTemplateId()) {
                                        name += " *";
                                    }
                                    if (t.getId() == current.getDisplayTemplateId()) {
                                        out.write("<option value=\"" + t.getId() + "\" selected>" + name + "</option>");
                                    } else {
                                        out.write("<option value=\"" + t.getId() + "\">" + name + "</option>");
                                    }
                                }
                            %>
                            </select>
                        </td>
                     </tr>
                     <tr>
                        <td colspan="2"><img src="../bitmaps/blank.gif" width="2" height="4"></td>
                     </tr>
                    <%
                            if (securitySession.isUserInRole(Aksess.getAdminRole())) {
                    %>
                        <tr>
                            <td colspan="2"><div class=helpText><kantega:label key="aksess.editpublishinfo.displaytemplate.hjelp"/></div></td>
                        </tr>
                        <tr>
                            <td colspan="2"><img src="../bitmaps/blank.gif" width="2" height="4"></td>
                        </tr>
                     <%
                            }
                        }
                     %>
                     <tr>
                        <td><kantega:label key="aksess.editpublishinfo.publisering"/></td>
                        <td><a href="Javascript:showAssociations()"><kantega:label key="aksess.editpublishinfo.publisering.link"/></a></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
<%
    if (current.getId() != -1 && allowedAssociations != null && allowedAssociations.size() > 0) {
%>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.editpublishinfo.rekkefolge"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="560">
                    <tr>
                        <td width="80"><img src="../bitmaps/blank.gif" width="80" height="1"></td>
                        <td width="320"><img src="../bitmaps/blank.gif" width="320" height="1"></td>
                        <td width="160"><img src="../bitmaps/blank.gif" width="160" height="1"></td>
                    </tr>
                    <tr>
                        <td><kantega:label key="aksess.editpublishinfo.rekkefolge.kategori"/></td>
                        <td>
                            <select name="associationCategory" onchange="changeAssociationCategory()" style="width:300px;" tabindex="70">
                            <%
                                for (int i = 0; i < allowedAssociations.size(); i++) {
                                    AssociationCategory tmp = (AssociationCategory)allowedAssociations.get(i);
                                    if (selectedAssociationCategory == -1 && i == 0) {
                                        selectedAssociationCategory = tmp.getId();
                                    }
                                    if (selectedAssociationCategory == tmp.getId()) {
                                        out.write("<option value=\"" + tmp.getId() + "\" selected>" + tmp.getName() + "</option>");
                                    } else {
                                        out.write("<option value=\"" + tmp.getId() + "\">" + tmp.getName() + "</option>");
                                    }
                                }
                            %>
                            </select>
                        </td>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <td colspan="3"><img src="../bitmaps/blank.gif" width="2" height="4"></td>
                    </tr>
                    <tr valign="top">
                        <td><kantega:label key="aksess.editpublishinfo.rekkefolge.rekkefolge"/></td>
                        <td>
                            <select name="tmpsortlist" style="width:300px;" size="15" tabindex="80">
                            <%
                                ContentQuery query = new ContentQuery();
                                query.setAssociatedId(current.getContentIdentifier());
                                AssociationCategory atype = null;
                                if (selectedAssociationCategory != -1) {
                                    atype = new AssociationCategory(selectedAssociationCategory);
                                    query.setAssociationCategory(atype);
                                }
                                List associatedContent = aksessService.getContentSummaryList(query, -1, new SortOrder("priority", false));
                                if (associatedContent != null) {
                                    for (int i = 0; i < associatedContent.size(); i++) {
                                        Content tmp = (Content)associatedContent.get(i);
                                        out.write("<option value=\"" + tmp.getAssociation().getId() + "\">" + tmp.getTitle() + "</option>");
                                    }
                                }
                            %>
                            </select>
                        </td>
                        <td>
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><a href="Javascript:moveItem(-1)"><img src="../bitmaps/common/buttons/innhold_flytt_opp.gif" border="0" alt=""></a></td>
                                    <td><a href="Javascript:moveItem(-1)" class="button" tabindex="90"><kantega:label key="aksess.button.flyttopp"/></a></td>
                                </tr>
                                <tr>
                                    <td colspan="2">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td><a href="Javascript:moveItem(1)"><img src="../bitmaps/common/buttons/innhold_flytt_ned.gif" border="0" alt=""></a></td>
                                    <td><a href="Javascript:moveItem(1)" class="button" tabindex="100"><kantega:label key="aksess.button.flyttned"/></a></td>
                                </tr>

                            </table>
                        </td>
                    </tr>
                </table><br>
                <div class=helpText><kantega:label key="aksess.editpublishinfo.rekkefolge.hjelp"/></div>
            </td>
        </tr>

        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
<%
    }
%>
    </table>
    <input type="hidden" name="status" value="">
    <input type="hidden" name="action" value="">
    <input type="hidden" name="sortlist" value="">
    <input type="hidden" name="currentId" value="<%=current.getId()%>">
    <input type="hidden" name="isModified" value="<%=current.isModified()%>">
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
