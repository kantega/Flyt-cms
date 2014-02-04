<%@ page import="no.kantega.publishing.modules.linkcheck.check.BrokenLinkNotifierJob" %>
<%@ page import="no.kantega.publishing.spring.RootContext"%>

<html>
<body>
<h1>Executing mailer</h1>
<%
      BrokenLinkNotifierJob job = (BrokenLinkNotifierJob)RootContext.getInstance().getBean("brokenLinkNotifierJob");
      job.execute();
%>
</body>
</html>
