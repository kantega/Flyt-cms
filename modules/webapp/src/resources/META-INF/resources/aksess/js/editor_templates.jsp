<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="application/javascript;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
var tinyMCETemplateList = [
<%
    Set snippets = pageContext.getServletContext().getResourcePaths("/snippets/");

    Iterator it = snippets.iterator();
    boolean first = true;
    while (it.hasNext()) {
        String file = (String)it.next();
        if (file.endsWith(".html") || file.endsWith(".jsp")) {
            if (!first) {
                out.write(",");
            }
            first = false;
            if (file.contains("/")) {
                file = file.substring(file.lastIndexOf("/") + 1, file.length());
            }
            String name = file;

            if (name.contains(".")) {
                name = name.substring(0, name.lastIndexOf("."));
            }
            out.write("['" + name + "','" + request.getContextPath() + "/snippets/" + file + "','" + name + "']");
        }
    }
%>
];