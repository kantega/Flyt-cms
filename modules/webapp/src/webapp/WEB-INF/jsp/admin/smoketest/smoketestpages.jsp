<pages>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ page contentType="text/xml" language="java" pageEncoding="iso-8859-1" %>
<c:forEach items="${pages}" var="page">
    <page url="<aksess:getattribute obj="${page}" name="url"/>"
          category="<aksess:getattribute obj="${page}" name="displaytemplate"/>"
            title="<aksess:getattribute obj="${page}" name="title"/>"
            id="<aksess:getattribute obj="${page}" name="contentId"/>">
    </page>
</c:forEach>

</pages>