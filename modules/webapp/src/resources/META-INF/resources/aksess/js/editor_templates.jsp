<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="application/json;charset=utf-8" trimDirectiveWhitespaces="true" %>
[
    <%
        Set<String> snippets = pageContext.getServletContext().getResourcePaths("/snippets/");
        if (snippets != null) {
            String filePrefix = request.getContextPath() + "/snippets/";

            Iterator<String> it = snippets.iterator();
            boolean first = true;
            while (it.hasNext()) {
                String file = it.next();
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
                    out.write("{'title':'" + name + "', 'description': '"+ name + "', 'url': '" + filePrefix + file + "'}");
                }
            }
        }
    %>
]
