<%@ page import="org.apache.tika.io.IOUtils" %>
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

<%

    URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
    printClassloader(out, classLoader);
    classLoader = (URLClassLoader)getClass().getClassLoader();
    printClassloader(out, classLoader);
    classLoader = (URLClassLoader) classLoader.getParent();
    printClassloader(out, classLoader);
%>
</body>
</html>
<%!
    private void printClassloader(JspWriter out, URLClassLoader classLoader) throws IOException {
        out.print("<ul>");
        for (URL urL : classLoader.getURLs()) {
            out.print("<li>");
            out.print(urL);
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
            out.print("</li>");
        }
        out.print("</ul>");
    }
%>
