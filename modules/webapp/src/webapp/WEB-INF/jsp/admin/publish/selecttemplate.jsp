<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.exception.ChildContentNotAllowedException"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="no.kantega.publishing.common.cache.SiteCache"%>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege"%>
<%@ page import="no.kantega.publishing.common.data.ContentTemplate" %>
<%@ page import="no.kantega.publishing.common.data.DisplayTemplate" %>
<%@ page import="no.kantega.publishing.common.data.AssociationCategory" %>
<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="java.util.Locale" %>
<%@ include file="../../../../admin/include/jsp_header.jsf" %>
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
    List allowedAssociations = (List)request.getAttribute("allowedAssociations");
    List allowedTemplates = (List)request.getAttribute("allowedTemplates");
    List associations = (List)request.getAttribute("associations");
    Content parent = (Content)request.getAttribute("parent");

    Locale lang = (Locale)request.getAttribute("aksess_locale");
    String locale_bildesti_buttons = "../bitmaps/"+skin+"/"+ lang.getLanguage().toLowerCase() + "/buttons/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>selecttemplate.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">

var addedParents = "<c:out value="${addedParents}"/>"

function selectContent() {
   doInsertTag = false;
   var contentwin = window.open("../popups/selectcontent.jsp", "contentWindow", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
   contentwin.focus();
}

function insertIdAndValueIntoForm(id, title) {
    if (addedParents != "") addedParents +=",";
    addedParents+=id;
    location = "AddContent.action?addedParents=" + addedParents;
}

function showTemplateInfo(i, defaultCategory) {
    var templateinfo = document.getElementById('templateinfo');
    var templatedesc = document.getElementById('templatedesc');

    templatedesc.innerHTML = document.getElementById('template' + i).innerHTML;
    if (defaultCategory != -1) {
        showCategoryInfo(defaultCategory);
        document.getElementById("category" + defaultCategory).checked = true;
    }
    templateinfo.style.display = 'block';
}

function showCategoryInfo(id) {
    var categoryinfo = document.getElementById('categoryinfo');
    var categorydesc = document.getElementById('categorydesc');

    categorydesc.innerHTML = document.getElementById('categoryinfo' + id).innerHTML;
    categoryinfo.style.display = 'block';
}

function doSelectTemplate() {
    if (!isChecked(document.myform.parentIds)) {
        alert("Angi minst en plassering for siden");
        return;
    }

    if (!isChecked(document.myform.templateId)) {
        alert("Velg en mal");
        return;
    }

<%

    if (allowedAssociations.size() > 1) {
%>
    if (!isChecked(document.myform.associationCategory)) {
        alert("Velg en kategori");
        return;
    }
<%
    }
%>
    document.myform.submit();
}

function isChecked(elm) {
    if (!elm) return false;
    if (!elm.length) return true;

    for (var i = 0; i < elm.length; i++) {
        if (elm[i].checked) return true;
    }

    return false;
}
    
</script>

<body class="bodyWithMargin">
    <form action="SelectTemplate.action" target="content" name="myform" method="get">
    <input type="hidden" name="mainParentId" value="<c:out value="${parent.id}"/>">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr class="tableHeading">
            <td><h1><kantega:label key="aksess.selecttemplate.velgplassering"/></h1></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>

        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="600">
                <%
                    List path ;
                    for (int i = 0; i < associations.size(); i++) {
                        Association parentAssociation = (Association)associations.get(i);
                        path = aksessService.getPathByAssociation(parentAssociation);
                        out.write("<tr class=\"tableRow" + (i%2) + "\">");
                        out.write("<td width=\"20\"><input type=\"checkbox\" name=\"parentIds\" value=\"" + parentAssociation.getId() +  "\" checked=\"true\"></td>");
                        out.write("<td width=\"580\">");
                        for (int j = 0; j < path.size(); j++) {
                            PathEntry entry = (PathEntry)path.get(j);
                            String title = entry.getTitle();
                            if (j > 0) {
                                out.write("&nbsp;&gt;&nbsp;");
                            }
                            if (j == 0) {
                                // P? f?rste niv?et skriver vi navnet p? nettstedet
                                Site site = SiteCache.getSiteById(parentAssociation.getSiteId());
                                out.write(site.getName());
                            } else {
                                out.write(title);
                            }
                        }

                        ContentIdentifier cid = new ContentIdentifier();
                        cid.setAssociationId(parentAssociation.getId());
                        Content c = aksessService.getContent(cid);
                        if (c != null) {
                            if (path.size() > 0) {
                                out.write("&nbsp;&gt;&nbsp;<b>" + c.getTitle() + "</b>");
                            } else {
                                Site site = SiteCache.getSiteById(c.getAssociation().getSiteId());
                                out.write(site.getName());
                            }
                        }
                        out.write("</td></tr>");
                    }
                %>
                </table>
                <c:if test="${error}">
                <table border="0" cellspacing="0" cellpadding="0" class="info">
                    <tr>
                        <td>
                            <c:out value="${error}"/>
                        </td>
                    </tr>
                </table>
                </c:if>
            </td>
        </tr>
        <tr>
            <td align="right">
                <br>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><a href="Javascript:selectContent()"><img src="../bitmaps/common/buttons/mini_legg_til.gif" border="0"></a></td>
                        <td><a href="Javascript:selectContent()" class="button"><kantega:label key="aksess.button.leggtil"/></a></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr class="tableHeading">
            <td><b><kantega:label key="aksess.selecttemplate.velgmal"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2" alt=""></td>
        </tr>
        <tr>
            <td>
                <%
                    boolean foundDefault = false;
                    String defaultText = "";
                    int defaultAssociationCategory = -1;
                    int defaultAssociationCategoryForTemplate = -1;
                    for (int i = 0; i < allowedTemplates.size(); i++) {
                        int id = -1;
                        String name = "";
                        String type = "";
                        String desc = "";
                        String image = "";
                        Object t = allowedTemplates.get(i);
                        if (t instanceof DisplayTemplate) {
                            DisplayTemplate dt = (DisplayTemplate)t;
                            id = dt.getId();
                            type = "dt";
                            name = dt.getName();
                            desc = dt.getDescription();
                            image = dt.getImage();
                            ContentTemplate ct = aksessService.getContentTemplate(dt.getContentTemplate().getId());
                            if (ct != null) {
                                AssociationCategory a = ct.getDefaultAssociationCategory();
                                if (a != null) {
                                    defaultAssociationCategoryForTemplate = a.getId();
                                }
                            }
                        } else if (t instanceof ContentTemplate) {
                            ContentTemplate ct = (ContentTemplate)t;
                            id = ct.getId();
                            type = "ct";
                            name = ct.getName();
                            desc = "";
                            AssociationCategory a = ct.getDefaultAssociationCategory();
                            if (a != null) {
                                defaultAssociationCategoryForTemplate = a.getId();
                            }
                        }
                        boolean isDefault = false;
                        if (!foundDefault) {
                            if (name.indexOf("*") != -1 || allowedTemplates.size() == 1) {
                                isDefault = true;
                                defaultText = "<b>" + name + "</b><br>" + desc;
                                foundDefault = true;
                                defaultAssociationCategory = defaultAssociationCategoryForTemplate;
                            }
                        }
                        name = name.replace('*', ' ');
                %>
                <input id="template_<%=type%>;<%=id%>" type="radio" name="templateId" value="<%=type%>;<%=id%>" onClick="showTemplateInfo(<%=i%>, <%=defaultAssociationCategoryForTemplate%>)" <% if(isDefault) out.write("checked");%>>
                <label style="display:inline;font-weight:normal;" for="template_<%=type%>;<%=id%>">
                    <%=name%>
                </label>
                <br>
                            <div id="template<%=i%>" style="display:none;"><b><%=name%></b><br>
                            <%=desc%>
                            </div>
                <%
                    }
                %>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>
        <tr>
            <td id="templateinfo" style="display:<% if (foundDefault) out.write("block"); else out.write("none");%>;">
                <div>
                    <table border="0" cellspacing="0" cellpadding="0" class="info">
                        <tr>
                            <td>
                                <div id="templatedesc"><%=defaultText%></div><br>
                                <img name="templateimage" src="../bitmaps/blank.gif" alt="">
                            </td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
    <%

        if (allowedAssociations.size() > 1) {
    %>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="10"></td>
        </tr>
        <tr class="tableHeading">
            <td><b><kantega:label key="aksess.selecttemplate.velgkategori"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>

                <%
                    if (defaultAssociationCategory == -1 && parent.getAssociation() != null) {
                        defaultAssociationCategory = parent.getAssociation().getCategory().getId();
                    }
                    for (int i = 0; i < allowedAssociations.size(); i++) {
                        AssociationCategory tmp = (AssociationCategory)allowedAssociations.get(i);
                        if (defaultAssociationCategory == tmp.getId()) {
                            out.write("<input type=\"radio\" name=\"associationCategory\" id=\"category" + tmp.getId() + "\" value=\"" + tmp.getId() + "\" onClick=\"showCategoryInfo(" + tmp.getId() + ")\" checked>" + tmp.getName() + "<br>");
                            foundDefault = true;
                            defaultText ="<b>" + tmp.getName() + "</b><br>" + tmp.getDescription();
                        } else {
                            out.write("<input type=\"radio\" name=\"associationCategory\" id=\"category" + tmp.getId() + "\" value=\"" + tmp.getId() + "\" onClick=\"showCategoryInfo(" + tmp.getId() + ")\">" + tmp.getName() + "<br>");
                        }
                        %>
                        <div id="categoryinfo<%=tmp.getId()%>" style="display:none;"><b><%=tmp.getName()%></b><br>
                        <%
                            if (tmp.getDescription() != null) {
                                out.write(tmp.getDescription());
                            }
                        %>
                        </div>
                        <%
                    }
                %>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>
        <tr>
            <td id="categoryinfo" style="display:<% if (foundDefault) out.write("block"); else out.write("none");%>;">
                <div>
                    <table border="0" cellspacing="0" cellpadding="0" class="info">
                        <tr>
                            <td>
                                <div id="categorydesc"><%=defaultText%></div><br>
                                <img name="categoryimage" src="../bitmaps/blank.gif" alt="">
                            </td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
        </tr>

    <%
        } else {
            AssociationCategory tmp = (AssociationCategory)allowedAssociations.get(0);
            out.write("<input type=\"hidden\" name=\"associationCategory\" value=\"" + tmp.getId() + "\">");
        }
    %>
    </table>
    <p>&nbsp;</p>
    <p>
        <a href="Javascript:doSelectTemplate()"><img src="<%=locale_bildesti_buttons%>fortsett.gif" border="0"></a>
        <a href="content.jsp?action=previewcontent" target="content"><img src="<%=locale_bildesti_buttons%>avbryt.gif" border="0"></a>
    </p>
    </form>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>