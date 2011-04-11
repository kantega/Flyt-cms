<%@ page import="no.kantega.publishing.spring.RootContext" %>
<%@ page import="no.kantega.publishing.modules.linkcheck.check.BrokenLinkNotifierJob"%>
<%--
<%
    LinkCrawlerJob crawler = (LinkCrawlerJob)RootContext.getInstance().getBean("linkCrawlJob");
    crawler.execute();
%>--%>


<html>
<body>
<h1>Executing mailer</h1>
<%
      BrokenLinkNotifierJob job = (BrokenLinkNotifierJob)RootContext.getInstance().getBean("brokenLinkNotifierJob");
      job.execute();
%>
</body>
</html>
