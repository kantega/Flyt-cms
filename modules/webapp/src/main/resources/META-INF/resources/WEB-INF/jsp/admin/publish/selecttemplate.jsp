<%@ page import="no.kantega.publishing.api.cache.SiteCache" %>
<%@ page import="no.kantega.publishing.api.content.ContentIdentifier" %>
<%@ page import="no.kantega.publishing.api.model.Site" %>
<%@ page import="no.kantega.publishing.api.path.PathEntry" %>
<%@ page import="no.kantega.publishing.common.data.*" %>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService" %>
<%@ page import="java.util.List" %>
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

<kantega:section id="head extras">
    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { content : {} };
        }
        properties.contextPath = '${pageContext.request.contextPath}';
    </script>
    <script type="text/javascript" src="<kantega:expireurl url="/aksess/js/aksess-i18n.jjs"/>"></script>
    <script type="text/javascript" src="<kantega:expireurl url="/admin/js/editcontext.js"/>"></script>
    <script type="text/javascript">
        openaksess.editcontext.insertValueAndNameIntoForm = function(id, title) {
            if (addedParents != "") {
                addedParents +=",";
            }
            addedParents += id;
            window.location.href = "${pageContext.request.contextPath}/admin/publish/AddContent.action?url=thisId=${parent.association.id}&addedParents=" + addedParents;
        };

        var addedParents = "${addedParents}";

        function selectContent() {
            openaksess.editcontext.doInsertTag = false;
            openaksess.common.modalWindow.open({title:'<kantega:label key="aksess.popup.selectcontent"/>', iframe:true, href: "popups/SelectContent.action",width: 400, height:450});
        }

        function showTemplateInfo(i, defaultCategory) {
            var templateinfo = document.getElementById('templateinfo');
            var templatedesc = document.getElementById('templatedesc');

            templatedesc.innerHTML = document.getElementById('template' + i).innerHTML;
            if (defaultCategory != -1) {
                showCategoryInfo(defaultCategory);
                var category = document.getElementById("category" + defaultCategory);
                if (category != null) category.checked = true;
            }
            templateinfo.style.display = 'block';
        }

        function showCategoryInfo(id) {
            var categoryinfo = document.getElementById('categoryinfo');
            var categorydesc = document.getElementById('categorydesc');

            var category = document.getElementById('categoryinfo' + id);
            if (category != null) {
                categorydesc.innerHTML = document.getElementById('categoryinfo' + id).innerHTML;
                categoryinfo.style.display = 'block';
            }
        }

        function doSelectTemplate() {
        <c:if test="${displayAddAssociation}">
            if (!isChecked(document.myform.parentIds)) {
                alert("<kantega:label key="aksess.selecttemplate.parent.notselected"/>");
                return;
            }
        </c:if>

            if (!isChecked(document.myform.templateId)) {
                alert("<kantega:label key="aksess.selecttemplate.template.notselected"/>");
                return;
            }

        <c:if test="${fn:length(allowedAssociations) > 1}">
            if (!isChecked(document.myform.associationCategory)) {
                alert("<kantega:label key="aksess.selecttemplate.category.notselected"/>");
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


        $(document).ready(function(){
            $("#ModesMenu .button").addClass("disabled");
        });

    </script>
</kantega:section>

<kantega:section id="modesMenu">
    <%@include file="../layout/fragments/publishModesMenu.jsp"%>
</kantega:section>

<kantega:section id="toolsMenu">
    <%@include file="../layout/fragments/publishToolsMenu.jsp"%>
</kantega:section>

<kantega:section id="contentclass">selecttemplate</kantega:section>

<kantega:section id="content">
    <%
        ContentManagementService aksessService = new ContentManagementService(request);
        SiteCache siteCache = WebApplicationContextUtils.getRequiredWebApplicationContext(application).getBean(SiteCache.class);

    %>

    <div id="MainPaneContent">
        <admin:box>
            <form action="SelectTemplate.action" name="myform" method="get">
                <input type="hidden" name="mainParentId" value="${parent.id}">
                <c:choose>
                    <c:when test="${displayAddAssociation}">
                        <h2><kantega:label key="aksess.selecttemplate.parent"/></h2>

                        <table width="100%">
                            <%
                                for (int i = 0; i < associations.size(); i++) {
                                    Association parentAssociation = (Association)associations.get(i);
                                    List<PathEntry> path = aksessService.getPathByAssociation(parentAssociation);
                                    out.write("<tr class=\"tableRow" + (i%2) + "\">");
                                    out.write("<td width=\"20\"><input type=\"checkbox\" name=\"parentIds\" value=\"" + parentAssociation.getId() +  "\" checked=\"checked\"></td>");
                                    out.write("<td>");
                                    for (int j = 0; j < path.size(); j++) {
                                        PathEntry entry = path.get(j);
                                        String title = entry.getTitle();
                                        if (j > 0) {
                                            out.write("&nbsp;&gt;&nbsp;");
                                        }
                                        if (j == 0) {
                                            // On the first level we print the site name
                                            Site site = siteCache.getSiteById(parentAssociation.getSiteId());
                                            out.write(site.getName());
                                        } else {
                                            out.write(title);
                                        }
                                    }

                                    ContentIdentifier cid =  ContentIdentifier.fromAssociationId(parentAssociation.getId());
                                    Content c = aksessService.getContent(cid);
                                    if (c != null) {
                                        if (path.size() > 0) {
                                            out.write("&nbsp;&gt;&nbsp;<b>" + c.getTitle() + "</b>");
                                        } else {
                                            Site site = siteCache.getSiteById(c.getAssociation().getSiteId());
                                            out.write(site.getName());
                                        }
                                    }
                                    out.write("</td></tr>");
                                }
                            %>
                        </table>
                        <c:if test="${notAuthorized}">
                            <div class="ui-state-highlight">
                                <kantega:label key="aksess.selecttemplate.notauthorized"/>
                            </div>
                        </c:if>
                        <div class="ui-state-highlight">
                            <kantega:label key="aksess.selecttemplate.addassociation"/>
                        </div>
                        <div style="text-align:right">
                            <a href="Javascript:selectContent()" class="button"><span class="add"><kantega:label key="aksess.button.addassociation"/></span></a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="association" items="${associations}">
                            <input type="hidden" name="parentIds" value="${association.id}">
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                <h2><kantega:label key="aksess.selecttemplate.template"/></h2>
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
                            if (name.contains("*") || allowedTemplates.size() == 1) {
                                isDefault = true;
                                defaultText = "<b>" + name + "</b><br>" + desc;
                                foundDefault = true;
                                defaultAssociationCategory = defaultAssociationCategoryForTemplate;
                            }
                        }
                        name = name.replace('*', ' ');
                        if (desc == null) desc = "";
                %>
                <div class="row">
                    <input id="template_<%=type%>_<%=id%>" type="radio" class="radio" name="templateId" value="<%=type%>_<%=id%>" onClick="showTemplateInfo(<%=i%>, <%=defaultAssociationCategoryForTemplate%>)" <% if(isDefault) out.write("checked");%>>
                    <label for="template_<%=type%>_<%=id%>" class="radio"><%=name%></label>
                    <div class="clearing"></div>
                </div>
                <div id="template<%=i%>" style="display:none;"><b><%=name%></b><br>
                    <%=desc%>
                </div>
                <%
                    }
                %>

                <div id="templateinfo" style="display:<% if (foundDefault) out.write("block"); else out.write("none");%>;" class="ui-state-highlight">
                    <div id="templatedesc"><%=defaultText%></div><br>
                    <img name="templateimage" src="../bitmaps/blank.gif" alt="">
                </div>
                <c:choose>
                    <c:when test="${fn:length(allowedAssociations) > 1}">
                        <h2><kantega:label key="aksess.selecttemplate.category"/></h2>
                        <%
                            if (defaultAssociationCategory == -1 && parent.getAssociation() != null) {
                                defaultAssociationCategory = parent.getAssociation().getCategory().getId();
                            }

                            for (int i = 0; i < allowedAssociations.size(); i++) {
                                AssociationCategory tmp = (AssociationCategory)allowedAssociations.get(i);
                                out.write("<div class=\"row\">");
                                if (defaultAssociationCategory == tmp.getId()) {
                                    out.write("<input type=\"radio\" class=\"radio\" name=\"associationCategory\" id=\"category" + tmp.getId() + "\" value=\"" + tmp.getId() + "\" onClick=\"showCategoryInfo(" + tmp.getId() + ")\" checked>");
                                    foundDefault = true;
                                    defaultText ="<b>" + tmp.getName() + "</b><br>" + tmp.getDescription();
                                } else {
                                    out.write("<input type=\"radio\" class=\"radio\" name=\"associationCategory\" id=\"category" + tmp.getId() + "\" value=\"" + tmp.getId() + "\" onClick=\"showCategoryInfo(" + tmp.getId() + ")\">");
                                }
                        %>
                        <label for="category<%=tmp.getId()%>" class="radio"><%=tmp.getName()%></label>
                        <div class="clearing"></div>
                        <%
                            out.write("</div>");
                        %>
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
                        <div class="clearing"></div>
                        <div id="categoryinfo" style="display:<% if (foundDefault) out.write("block"); else out.write("none");%>;" class="ui-state-highlight">
                            <div id="categorydesc"><%=defaultText%></div><br>
                            <img name="categoryimage" src="../bitmaps/blank.gif" alt="">
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
                    <span class="button"><input type="button" onclick="doSelectTemplate()" class="ok" value="<kantega:label key="aksess.button.continue"/>"></span>
                    <span class="button"><input type="button" onclick="window.location.href='Navigate.action'" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
                </div>

            </form>
        </admin:box>
    </div>

    <div class="clearing"></div>

</kantega:section>
<%@include file="../layout/fullwidthLayout.jsp"%>
