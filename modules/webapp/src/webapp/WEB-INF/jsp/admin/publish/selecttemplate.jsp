<%@ page import="no.kantega.publishing.admin.content.InputScreenRenderer" %>
<%@ page import="no.kantega.publishing.common.data.enums.AttributeDataType" %>
<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="no.kantega.publishing.common.data.*" %>
<%@ page import="no.kantega.publishing.common.cache.SiteCache" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
    Content parent = (Content)request.getAttribute("parent");
    List associations = (List)request.getAttribute("associations");
    List allowedAssociations = (List)request.getAttribute("allowedAssociations");
%>

<kantega:section id="title">
    <kantega:label key="aksess.edit.title"/>
</kantega:section>

<kantega:section id="content">
<script type="text/javascript">

    var addedParents = "${addedParents}";

    function selectContent() {
        doInsertTag = false;
        var contentwin = window.open("popups/SelectContent.action", "contentWindow", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
        contentwin.focus();
    }

    function insertIdAndValueIntoForm(id, title) {
        if (addedParents != "") {
            addedParents +=",";
        }
        addedParents += id;
        window.location.href = "AddContent.action?addedParents=" + addedParents;
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
            alert("<kantega:label key="aksess.selecttemplate.parent.notselected"/>");
            return;
        }

        if (!isChecked(document.myform.templateId)) {
            alert("<kantega:label key="aksess.selecttemplate.template.notselected"/>");
            return;
        }

        <c:if test="${fn:length(allowedAssociations) > 1}">
        if (!isChecked(document.myform.associationCategory)) {
            alert("<kantega:label key="aksess.selecttemplate.menu.notselected"/>");
            return;
        }
        </c:if>
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
    <div class="fieldset">
        <fieldset>
            <legend><kantega:label key="aksess.selecttemplate.parent"/></legend>

            <table width="100%">
                <%
                    List path ;
                    ContentManagementService aksessService = new ContentManagementService(request);
                    for (int i = 0; i < associations.size(); i++) {
                        Association parentAssociation = (Association)associations.get(i);
                        path = aksessService.getPathByAssociation(parentAssociation);
                        out.write("<tr class=\"tableRow" + (i%2) + "\">");
                        out.write("<td width=\"20\"><input type=\"checkbox\" name=\"parentIds\" value=\"" + parentAssociation.getId() +  "\" checked=\"true\"></td>");
                        out.write("<td>");
                        for (int j = 0; j < path.size(); j++) {
                            PathEntry entry = (PathEntry)path.get(j);
                            String title = entry.getTitle();
                            if (j > 0) {
                                out.write("&nbsp;&gt;&nbsp;");
                            }
                            if (j == 0) {
                                // On the first level we print the site name
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
            <c:if test="${notAuthorized}">
                <div class="info">
                    <kantega:label key="aksess.selecttemplate.notauthorized"/>
                </div>
            </c:if>
            <a href="Javascript:selectContent()" class="button add"><span><kantega:label key="aksess.button.leggtil"/></span></a>
        </fieldset>
    </div>

    <div class="fieldset">
        <fieldset>
            <legend><kantega:label key="aksess.selecttemplate.template"/></legend>
            <%
                List allowedTemplates = (List) request.getAttribute("allowedTemplates");
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
            <label for="template_<%=type%>;<%=id%>">
                <%=name%>
            </label>
            <br>
            <div id="template<%=i%>" style="display:none;"><b><%=name%></b><br>
                <%=desc%>
            </div>
            <%
                }
            %>

            <div id="templateinfo" style="display:<% if (foundDefault) out.write("block"); else out.write("none");%>;" class="info">
                <div id="templatedesc"><%=defaultText%></div><br>
                <img name="templateimage" src="../bitmaps/blank.gif" alt="">
            </div>
        </fieldset>
    </div>

    <c:choose>
        <c:when test="${fn:length(allowedAssociations) > 1}">
            <div class="fieldset">
                <fieldset>
                    <legend><kantega:label key="aksess.selecttemplate.menu"/></legend>
                    <%
                        if (defaultAssociationCategory == -1 && parent.getAssociation() != null) {
                            defaultAssociationCategory = parent.getAssociation().getCategory().getId();
                        }                        
                        for (int i = 0; i < allowedAssociations.size(); i++) {
                            AssociationCategory tmp = (AssociationCategory)allowedAssociations.get(i);
                            if (defaultAssociationCategory == tmp.getId()) {
                                out.write("<input type=\"radio\" name=\"associationCategory\" id=\"category" + tmp.getId() + "\" value=\"" + tmp.getId() + "\" onClick=\"showCategoryInfo(" + tmp.getId() + ")\" checked>");
                                foundDefault = true;
                                defaultText ="<b>" + tmp.getName() + "</b><br>" + tmp.getDescription();
                            } else {
                                out.write("<input type=\"radio\" name=\"associationCategory\" id=\"category" + tmp.getId() + "\" value=\"" + tmp.getId() + "\" onClick=\"showCategoryInfo(" + tmp.getId() + ")\">");
                            }
                    %>
                    <label for="category_<%=tmp.getId()%>"><%=tmp.getName()%></label><br>    
                    <div id="categoryinfo<%=tmp.getId()%>" style="display:none;">
                        <strong><%=tmp.getName()%></strong><br>
                        <%
                            if (tmp.getDescription() != null) {
                                out.write(tmp.getDescription());
                            }
                        %>
                    </div>
                    <%
                        }
                    %>

                    <div id="categoryinfo" style="display:<% if (foundDefault) out.write("block"); else out.write("none");%>;">
                        <div id="categorydesc"><%=defaultText%></div><br>
                        <img name="categoryimage" src="../bitmaps/blank.gif" alt="">
                    </div>
                </fieldset>
            </div>

        </c:when>
        <c:otherwise>
            <%
                AssociationCategory tmp = (AssociationCategory)allowedAssociations.get(0);
                out.write("<input type=\"hidden\" name=\"associationCategory\" value=\"" + tmp.getId() + "\">");
            %>
        </c:otherwise>
    </c:choose>

    <div class="buttonGroup">
        <input type="button" onclick="doSelectTemplate()" class="button ok" value="<kantega:label key="aksess.button.continue"/>">
        <input type="button" onclick="window.location.href='Navigate.action'" class="button cancel" value="<kantega:label key="aksess.button.cancel"/>">
    </div>

</form>
</kantega:section>
<%@include file="../layout/contentNavigateLayout.jsp"%>