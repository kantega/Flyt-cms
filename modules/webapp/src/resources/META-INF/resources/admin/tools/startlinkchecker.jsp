<%@ page import="no.kantega.publishing.modules.linkcheck.check.LinkCheckerJob" %>
<%@ page import="no.kantega.publishing.spring.RootContext" %>
<%@ page import="no.kantega.publishing.common.ao.LinkDao" %>
<%@ page import="no.kantega.publishing.modules.linkcheck.crawl.LinkCrawlerJob" %><%
    LinkCrawlerJob crawler = (LinkCrawlerJob)RootContext.getInstance().getBean("linkCrawlJob");
    crawler.execute();
%>