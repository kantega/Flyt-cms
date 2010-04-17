<%@ page import="no.kantega.publishing.spring.RootContext"%>
<%@ page import="org.springframework.context.ApplicationContext"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.util.*"%>
<%@ page import="no.kantega.publishing.admin.category.CategoryManager"%>
<%@ page import="no.kantega.commons.exception.SystemException"%>
<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%!

public void printCategory(Content base, ContentCategory category, JspWriter out, CategoryManager manager, Set opencategories) throws IOException, SystemException {


        out.print("<tr>");
        out.println("<td width=\"11\"></td>");
        if(category != null) {
            String state = opencategories.contains("" + category.getId()) ? "open" : "closed";
            String action = opencategories.contains("" + category.getId()) ? "closecat" : "opencat";
            out.println("<td width=11>");
            out.println("<a href=\"javascript:" + action +"('" + category.getId() + "')\"><img src=\"../bitmaps/common/navigator/nav_" +state +".gif\" border=\"0\" vspace=2></a>");
            out.println("</td>");
            out.println("<td><a href=\"javascript:selectCategory('" + category.getId()+"', '" + category.getName() + "')\" class=\"navNormal\">" + category.getName() + "</a></td>");
        }
        out.print("</tr>");

        if(category == null || (opencategories != null && opencategories.contains("" + category.getId()))){
            List childCategories = manager.getCategories(base, category);
            if(childCategories.size() > 0) {
                out.println("<tr><td width=\"11\"></td><td></td><td><table cellspacing=\"0\" cellpadding=\"0\">");
                for (int i = 0; i < childCategories.size(); i++) {
                    ContentCategory childCategory = (ContentCategory)childCategories.get(i);
                    printCategory(base, childCategory, out, manager, opencategories);
                }
                out.println("</td></tr></table>");
            }
        }
    }
    %>
<html>
<head>
	<title>selectcategory_bottom.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <script type="text/javascript">
        function selectCategory(category, title) {
            var w = window.parent.opener;
            if (w) {
                if (w.doInsertTag) {
                    w.openaksess.editcontext.insertValueIntoForm(category);
                } else {
                    w.openaksess.editcontext.insertIdAndValueIntoForm(category, title);
                }
                window.parent.close();
            }

        }
        function opencat(category) {
            var opencategories = document.tree.opencategories.value;
            document.tree.addcategory.value = category;
            document.tree.submit();
        }
        function closecat(category) {
            var opencategories = document.tree.opencategories.value;
            document.tree.removecategory.value = category;
            document.tree.submit();
        }

    </script>
</head>
<body>
<%
    RequestParameters param = new RequestParameters(request);

    Set newOpen = new TreeSet();

    if(param.getString("opencategories") != null) {
        String[] opencategories = param.getString("opencategories").split(",");
        newOpen.addAll(Arrays.asList(opencategories));
    }
    String addCategory = param.getString("addcategory");

    if(addCategory != null && addCategory.length() > 0) {
        newOpen.add(addCategory);
    }
    String removeCategory = param.getString("removecategory");
    if(removeCategory != null && removeCategory.length() > 0) {
        newOpen.remove(removeCategory);
    }
    newOpen.remove("");

    String oc = "";
    Iterator ocit = newOpen.iterator();
    while (ocit.hasNext()) {
        String unit = (String) ocit.next();
        oc += unit;
        if(ocit.hasNext()) {
            oc += ",";
        }
    }
%>
<form action="selectcategory_body.jsp" name="tree">
    <input name="opencategories" type="hidden" value="<%=oc%>">
    <input name="addcategory" type="hidden" value="">
    <input name="removecategory" type="hidden" value="">
</form>
<table cellpadding="0" cellspacing="0">
    <%
        ApplicationContext context = RootContext.getInstance();

        Map managers = context.getBeansOfType(CategoryManager.class);

        Iterator i  = managers.values().iterator();

        Content current = (Content)session.getAttribute("currentContent");

        if(current != null && i.hasNext()) {
            CategoryManager manager = (CategoryManager) i.next();
            printCategory(current, null, out, manager, newOpen);
        }
    %>
</table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
