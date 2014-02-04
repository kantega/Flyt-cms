<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.URLClassLoader" %>
<%@ page import="java.util.jar.JarInputStream" %>
<%@ page import="java.util.zip.ZipEntry" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<html>
<head>
    <title>Vis innholdet på classpath</title>
</head>
<body>
<a href="?printclasses=true">Print all classes</a>
<%
    boolean printclasses = request.getParameter("printclasses") != null;
    URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
    printClassloader(out, classLoader, printclasses);
    classLoader = (URLClassLoader)getClass().getClassLoader();
    printClassloader(out, classLoader, printclasses);
    classLoader = (URLClassLoader) classLoader.getParent();
    printClassloader(out, classLoader, printclasses);
%>
</body>
</html>
<%!
    private void printClassloader(JspWriter out, URLClassLoader classLoader, boolean printClasses) throws IOException {
        out.print("<ul>");
        for (URL urL : classLoader.getURLs()) {
            out.print("<li>");
            out.print(urL);

            if (printClasses) {
                printClasses(out, urL);
            }

            out.print("</li>");
        }
        out.print("</ul>");
    }

    private void printClasses(JspWriter out, URL urL) throws IOException {
        out.print("<ul>");
        JarInputStream jarFile = null;
        try {
            jarFile = new JarInputStream(urL.openStream());
            ZipEntry nextEntry = jarFile.getNextEntry();
            while (nextEntry != null){
                if (!nextEntry.isDirectory()) {
                    out.print("<li>");
                    out.print(nextEntry.getName());
                    out.print("</li>");
                }
                nextEntry = jarFile.getNextEntry();
            }
        } finally {
            IOUtils.closeQuietly(jarFile);
        }
        out.print("</ul>");
    }
%>
