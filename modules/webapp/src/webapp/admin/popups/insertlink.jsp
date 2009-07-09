<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
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
    Content current = (Content)session.getAttribute("currentContent");

    RequestParameters param = new RequestParameters(request);
    String url = param.getString("url");
    String anchor = param.getString("anchor");
    if (url == null || url.length() == 0) {
        url = "http://";
    }
    if (anchor == null) {
        anchor = "";
    }

    boolean openInNewWindow = param.getBoolean("openInNewWindow", false);


    Locale lang = (Locale)request.getAttribute("aksess_locale");
    String locale_bildesti_framework = "../bitmaps/"+skin+"/"+ lang.getLanguage().toLowerCase() + "/framework/";
    String locale_bildesti_buttons = "../bitmaps/"+skin+"/"+ lang.getLanguage().toLowerCase() + "/buttons/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title><kantega:label key="aksess.insertlink.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/browserdetect.js"></script>
<script language="Javascript" src="../js/edit.jsp"></script>
<script language="Javascript" src="../../aksess/js/autocomplete.js"></script>

<script language="Javascript" type="text/javascript">
function doInsert() {
    var url = "";
    var frm = document.myform;
    if (window.opener) {

        var linktype = frm.linktype.options[frm.linktype.selectedIndex].value;
        if (linktype == "internal") {
            if (document.myform.smartlink.checked) {
                linktype = "contentId";
            } else {
                linktype = "associationId";
            }
        }
        var field = eval('frm.url_' + linktype);
        var type = field.type;
        if (type.indexOf("select") != -1) {
            url = field.options[field.selectedIndex].value;
        } else {
            url = field.value;
        }

        if (url == "" || url == "http://") {
            alert("<kantega:label key="aksess.insertlink.nourl"/>");
            return;
        } else {
            if (linktype == "associationId") {
                url = "/content.ap?thisId=" + url;
            } else if (linktype == "contentId") {
                url = "/content.ap?contentId=" + url + "&amp;contextId=$contextId$";
            } else if (linktype == "multimedia") {
                url = "/multimedia.ap?id=" + url;
            } else if (linktype == "email") {
                url = "mailto:" + url;
                var subject = frm.subject.value;
                if (subject != "") {
                    url = url + "?subject=" + escape(subject);
                }
            }

            var anchor = document.myform.anchor.value;
            if (anchor != "") {
                if (anchor.charAt(0) == '#') {
                    anchor = anchor.substring(0, anchor.length);
                }
                if (linktype == "attachment") {
                    // AttachmentRequestHandler will send correct anchor for PDF/Word
                    url = url + "&anchor=" + escape(anchor);
                    url = url + "#" + anchor;
                } else {
                    url = url + "#" + anchor;
                }
            }
            if (url.charAt(0) == '/') {
                url = "<%=URLHelper.getRootURL(request)%>" + url.substring(1, url.length);
            }

            if (linktype == "external" && frm.newwindow.checked) {
                // Cant use CreateLink with target
                var range = getRange(window.opener.focusField);
                if (range) {
                    var node = getNodeFromRange(range);
                    var selectedText = getHTMLFromRange(range);
                    try {
                        // if user has selected a link, remove old link
                        if (node.tagName == "A") {
                            selectedText = node.innerHTML;
                        }
                        pasteHTML(window.opener.focusField, '<a href="' + url + '" onclick="window.open(this.href); return false">' + selectedText  + '</a>');
                    } catch (e) {

                    }
                }
            } else {
                window.opener.createLink(url);
            }            
        }
    }
    window.close();
}


function updateVisibleFields() {
    var fields = ["external", "anchor", "associationId", "contentId", "email", "attachment", "multimedia"];
    for (var i = 0; i < fields.length; i++) {
        var obj = document.getElementById("linktype_" + fields[i]);
        obj.style.display = 'none';
    }

    var linktype = document.myform.linktype.options[document.myform.linktype.selectedIndex].value;
    var smartlink = document.getElementById("smartlink");
    var anchor = document.getElementById("anchor");

    if (linktype == "external" || linktype == "internal") {
        anchor.style.display = "block";
    } else {
        anchor.style.display = "none";
        document.myform.anchor.value = "";
    }

    if (linktype == "internal") {
        if (document.myform.smartlink.checked) {
            linktype = "contentId";
        } else {
            linktype = "associationId";
        }
        smartlink.style.display = "block";
    } else {
        smartlink.style.display = "none";
    }

    var selObj = document.getElementById("linktype_" + linktype);
    selObj.style.display = 'block';

}


function insertIdAndValueIntoForm(id, text)
{
    var frm = document.myform;
    var linktype = frm.linktype.options[frm.linktype.selectedIndex].value;
    if (linktype == "internal") {
        if (document.myform.smartlink.checked) {
            frm.url_contentId.value = id;
            frm.url_contentIdtext.value = text;
        } else {
            frm.url_associationId.value = id;
            frm.url_associationIdtext.value = text;
        }
    } else if (linktype == "multimedia") {
        frm.url_multimedia.value = id;
        frm.url_multimediatext.value = text;
    }
}

function selectAssociationId() {
    doInsertTag = false;
    var contentwin = window.open("selectcontent.jsp", "contentWindow", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
    contentwin.focus();
}

function selectContentId() {
    doInsertTag = false;
    var contentwin = window.open("selectcontent.jsp?useContentId=true", "contentWindow", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
    contentwin.focus();
}

function selectMultimedia() {
    doInsertTag = false;
    var mmwin = window.open("../multimedia/", "mmWindow", "toolbar=no,width=780,height=450,resizable=yes,scrollbars=yes");
    mmwin.focus();
}


function addAnchors() {
    if (window.opener) {
        var fld = window.opener.focusField;
        var imgs = fld.contentWindow.document.getElementsByTagName("img");
        for (var j = 0; imgs && j < imgs.length; j++) {
            var img = imgs[j];
            if (img.src.indexOf("placeholder/anchor.gif") != -1) {
                var n = img.name;
                document.myform.url_anchor.options[document.myform.url_anchor.options.length] = new Option(n, '#' + n);
            }
        }
    }
}
</script>

<body class="bodyWithMargin" onLoad="addAnchors()">
<form name="myform">
        <table border="0" width="370" cellspacing="0" cellpadding="0">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>
            <tr>
                <td colspan="3" class="tableHeading"><b><kantega:label key="aksess.insertlink.title"/></b></td>
            </tr>
            <tr>
                <td colspan="3"><img src="../bitmaps/blank.gif" width="2" height="4"></td>
            </tr>
            <tr>
                <td><b><kantega:label key="aksess.insertlink.type"/></b></td>
                <td>
                <select name="linktype" style="width:250px;" onChange="updateVisibleFields(this)">
                    <option value="external"><kantega:label key="aksess.insertlink.external"/></option>
                    <option value="anchor"><kantega:label key="aksess.insertlink.anchor"/></option>
                    <option value="internal"><kantega:label key="aksess.insertlink.internal"/></option>
                    <option value="email"><kantega:label key="aksess.insertlink.email"/></option>
                    <option value="attachment"><kantega:label key="aksess.insertlink.attachment"/></option>
                    <option value="multimedia"><kantega:label key="aksess.insertlink.multimedia"/></option>
                </select>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td colspan="3"><img src="../bitmaps/blank.gif" width="2" height="4"></td>
            </tr>
        </table>
        <table border="0" width="370" cellspacing="0" cellpadding="0" id="linktype_external" style="display:block;">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>
            <tr>
                <td><b><kantega:label key="aksess.insertlink.external.url"/></b></td>
                <td><input type="text" name="url_external" value="<%=url%>" style="width:250px;" maxlength="1024"></td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td><input type="checkbox" name="newwindow" <%if (Aksess.doOpenLinksInNewWindow() || openInNewWindow) out.write("checked");%>><kantega:label key="aksess.insertlink.opennewwindow"/></td>
                <td>&nbsp;</td>
            </tr>
        </table>

        <table border="0" width="370" cellspacing="0" cellpadding="0" id="linktype_anchor" style="display:none;">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>
            <tr>
                <td><b><kantega:label key="aksess.insertlink.anchor.title"/></b></td>
                <td>
                    <select name="url_anchor" style="width:250px;">
                        <option value=""><kantega:label key="aksess.insertlink.anchor.select"/></option>
                    </select>
                </td>
                <td>&nbsp;</td>
            </tr>
        </table>

        <table border="0" width="370" cellspacing="0" cellpadding="0" id="linktype_associationId" style="display:none;">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>
            <tr>
                <td><b><kantega:label key="aksess.insertlink.internal.url"/></b></td>
                <td><input type="hidden" name="url_associationId" id="url_associationId" value=""><input type="text" name="url_associationIdtext" id="url_associationIdtext" onfocus="this.select()" value="<kantega:label key="aksess.insertlink.internal.hint"/>" style="width:250px;" maxlength="128"></td>
                <td align="right"><a href="Javascript:selectAssociationId()" class="button"><kantega:label key="aksess.button.velg"/></a></td>
                <script type="text/javascript">
                    Autocomplete.setup({'inputField' :'url_associationId', url:'../../ajax/SearchContentAsXML.action', 'minChars' :3 });
                </script>
            </tr>
        </table>

        <table border="0" width="370" cellspacing="0" cellpadding="0" id="linktype_contentId" style="display:none;">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>
            <tr>
                <td><b><kantega:label key="aksess.insertlink.internal.url"/></b></td>
                <td><input type="hidden" name="url_contentId" id="url_contentId" value=""><input type="text" name="url_contentIdtext" id="url_contentIdtext" onfocus="this.select()" value="<kantega:label key="aksess.insertlink.internal.hint"/>" style="width:250px;" maxlength="128"></td>
                <td align="right"><a href="Javascript:selectContentId()" class="button"><kantega:label key="aksess.button.velg"/></a></td>
                <script type="text/javascript">
                    Autocomplete.setup({'inputField' :'url_contentId', url:'../../ajax/SearchContentAsXML.action?useContentId=true', 'minChars' :3 });
                </script>
            </tr>
        </table>

        <table border="0" width="370" cellspacing="0" cellpadding="0" id="linktype_email" style="display:none;">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>
            <tr>
                <td><b><kantega:label key="aksess.insertlink.email.recipient"/></b></td>
                <td><input type="text" name="url_email" value="" style="width:250px;" maxlength="128"></td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td><b><kantega:label key="aksess.insertlink.email.subject"/></b></td>
                <td><input type="text" name="subject" value="" style="width:250px;" maxlength="128"></td>
                <td>&nbsp;</td>
            </tr>
        </table>
        <table border="0" width="370" cellspacing="0" cellpadding="0" id="linktype_attachment" style="display:none;">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>

            <tr>
                <td><b><kantega:label key="aksess.insertlink.attachment.title"/></b></td>
                <td>
                <select name="url_attachment" style="width:250px;">
                <option value=""><kantega:label key="aksess.insertlink.attachment.select"/></option>
                <%
                    if (current != null) {
                        List attachments = null;
                        if (current.getId() > 0) {
                            ContentIdentifier cid = new ContentIdentifier();
                            cid.setContentId(current.getId());
                            cid.setLanguage(current.getLanguage());
                            attachments = aksessService.getAttachmentList(cid);
                        } else {
                            attachments = current.getAttachments();
                        }
                        if (attachments != null) {
                            for (int i=0; i < attachments.size(); i++) {
                                Attachment a = (Attachment)attachments.get(i);
                                String filename = a.getFilename();
                                if (filename.length() > 40) {
                                    filename = filename.substring(0, 37) + "...";
                                }
                                out.write("<option value=\"/attachment.ap?id=" + a.getId() + "\">" + filename + "</option>");
                            }
                        }
                    }

                %>
                </select>
                </td>
                <td align="right"><a href="AddAttachment.action?insertLink=true" class="button"><kantega:label key="aksess.button.nyfil"/></a></td>
            </tr>
        </table>
        <table border="0" width="370" cellspacing="0" cellpadding="0" id="linktype_multimedia" style="display:none;">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>
            <tr>
                <td><b><kantega:label key="aksess.insertlink.multimedia.file"/></b></td>
                <td><input type="hidden" name="url_multimedia" id="url_multimedia" value=""><input type="text" name="url_multimediatext" id="url_multimediatext" onfocus="this.select()" value="<kantega:label key="aksess.insertlink.multimedia.hint"/>" style="width:250px;" maxlength="128"></td>
                <td align="right"><a href="Javascript:selectMultimedia()" class="button"><kantega:label key="aksess.button.velg"/></a></td>
                <script type="text/javascript">
                    Autocomplete.setup({'inputField' :'url_multimedia', url:'../../ajax/SearchMultimediaAsXML.action', 'minChars' :3 });
                </script>
            </tr>
        </table>
        <table border="0" width="370" cellspacing="0" cellpadding="0" id="smartlink" style="display:none;">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>
            <tr>
                 <td>&nbsp;</td>
                 <td><input type="checkbox" name="smartlink" onclick="updateVisibleFields()" <% if (Aksess.isSmartLinksDefaultChecked()) {%>checked="checked"<% } %>><kantega:label key="aksess.insertlink.smart"/></td>
                 <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td class="info">
                    <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td>
                                <kantega:label key="aksess.insertlink.smart.hint"/>
                            </td>
                        </tr>
                    </table>
                </td>
                <td>&nbsp;</td>
            </tr>

        </table>
        <table border="0" width="370" cellspacing="0" cellpadding="0" id="anchor" style="display:block;">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>
            <tr>
                 <td><b><kantega:label key="aksess.insertlink.anchor.title"/></b></td>
                 <td><input type="text" name="anchor" style="width:250px;" maxlength="64" value="<%=anchor%>"></td>
                 <td>&nbsp;</td>
             </tr>
        </table>
        <table border="0" width="370" cellspacing="0" cellpadding="0">
            <tr>
                <td width="70"><img src="../bitmaps/blank.gif" width="70" height="1"></td>
                <td width="250"><img src="../bitmaps/blank.gif" width="250" height="1"></td>
                <td width="50"><img src="../bitmaps/blank.gif" width="50" height="1"></td>
            </tr>
            <tr>
                <td colspan="3"><img src="../bitmaps/blank.gif" width="2" height="24"></td>
            </tr>
            <tr>
                <td colspan="3"><a href="Javascript:doInsert()"><img src="<%=locale_bildesti_buttons%>ok.gif" border="0"></a>&nbsp;&nbsp;<a href="Javascript:window.close()"><img src="<%=locale_bildesti_buttons%>avbryt.gif" border="0"></a></td>
            </tr>
        </table>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>