<%@ page import="no.kantega.publishing.modules.linkcheck.crawl.LinkCrawlerJob" %>
<%@ page import="no.kantega.publishing.spring.RootContext" %>
<%
    LinkCrawlerJob crawler = (LinkCrawlerJob)RootContext.getInstance().getBean("linkCrawlJob");
    crawler.execute();
%>