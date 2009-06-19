<%@ page import="no.kantega.publishing.spring.RootContext"%>
<%@ page import="no.kantega.publishing.org.OrganizationManager"%>
<%@ page import="org.springframework.context.ApplicationContext"%>
<%@ page import="java.io.IOException"%>
<%@ page import="no.kantega.publishing.org.OrgUnit"%>
<%@ page import="java.util.*"%>
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

public void printUnit(OrgUnit unit, JspWriter out, OrganizationManager manager, Set openunits) throws IOException {

        out.print("<tr>");
        out.println("<td width=\"11\"></td>");
        if(unit != null) {
            String state = openunits.contains(unit.getExternalId()) ? "open" : "closed";
            String action = openunits.contains(unit.getExternalId()) ? "closeunit" : "openunit";
            out.println("<td width=11>");

            out.println("<a href=\"javascript:" + action +"('" +unit.getExternalId() + "')\"><img src=\"../bitmaps/common/navigator/nav_" +state +".gif\" border=\"0\" vspace=2></a>");
            out.println("</td>");
            out.println("<td><a href=\"javascript:selectUnit('" +unit.getExternalId() +"', '" + unit.getName() +"')\" class=\"navNormal\">" +unit.getName() +"</a></td>");
        } else {
            out.println("<td></td><td>Organisasjon</td>");
        }
        out.print("</tr>");

        if(unit == null || (openunits != null && openunits.contains(unit.getExternalId()))){
            List childUnits = manager.getChildUnits(unit);
            if(childUnits.size() > 0) {
                out.println("<tr><td width=\"11\"></td><td></td><td><table cellspacing=\"0\" cellpadding=\"0\">");
                for (int i = 0; i < childUnits.size(); i++) {
                    OrgUnit orgUnit = (no.kantega.publishing.org.OrgUnit) childUnits.get(i);
                    printUnit(orgUnit, out, manager, openunits);
                }
                out.println("</td></tr></table>");

            }
        }

    }
    %>
<html>
<head>
	<title>navigatorbottom.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <script type="text/javascript">
        function selectUnit(unit, title) {
            var w = window.parent.opener;
            if (w) {
                if (w.doInsertTag) {
                    w.insertValueIntoForm(unit);
                } else {
                    w.insertIdAndValueIntoForm(unit, title);
                }
                window.parent.close();
            }

        }
        function openunit(unitcode) {
            var openunits = document.tree.openunits.value;
            document.tree.addunit.value = unitcode;
            document.tree.submit();
        }
        function closeunit(unitcode) {
            var openunits = document.tree.openunits.value;
            document.tree.removeunit.value = unitcode;
            document.tree.submit();
        }

    </script>
</head>
<body>
<%
    RequestParameters param = new RequestParameters(request);

    Set newOpen = new TreeSet();

    if(request.getParameter("openunits") != null) {
        String[] openunits = param.getString("openunits").split(",");
        newOpen.addAll(Arrays.asList(openunits));
    }
    String addUnit = request.getParameter("addunit");

    if(addUnit != null && addUnit.length() > 0) {
        newOpen.add(addUnit);
    }
    String removeUnit = param.getString("removeunit");
    if(removeUnit != null && removeUnit.length() > 0) {
            newOpen.remove(removeUnit);
    }
    newOpen.remove("");
    String ou = "";
    Iterator ouit = newOpen.iterator();
    while (ouit.hasNext()) {
        String unit = (String) ouit.next();
        ou += unit;
        if(ouit.hasNext()) {
            ou += ",";
        }
    }
%>
<form action="selectorgunit_body.jsp" name="tree">
    <input name="openunits" type="hidden" value="<%=ou%>">
    <input name="addunit" type="hidden" value="">
    <input name="removeunit" type="hidden" value="">
</form>
<table cellpadding="0" cellspacing="0">
    <%
        ApplicationContext context = RootContext.getInstance();
        Map managers = context.getBeansOfType(OrganizationManager.class);
        Iterator i  = managers.values().iterator();

        if(i.hasNext()) {

            OrganizationManager manager = (OrganizationManager) i.next();

            %>
               <%


                   printUnit(null, out, manager, newOpen);
               %>

  <%
      }
    %>
    </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
