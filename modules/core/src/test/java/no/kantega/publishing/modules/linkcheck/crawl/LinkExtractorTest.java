package no.kantega.publishing.modules.linkcheck.crawl;

import no.kantega.publishing.api.content.ContentAO;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.HtmltextAttribute;
import no.kantega.publishing.common.data.attributes.UrlAttribute;
import no.kantega.publishing.eventlog.EventLog;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinkExtractorTest {

    private EventLog eventLog;
    private ContentAO contentAO;
    private LinkExtractor linkExtractor;

    @Before
    public void setUp() throws Exception {
        eventLog = mock(EventLog.class);
        contentAO = mock(ContentAO.class);
        linkExtractor = new LinkExtractor(eventLog, contentAO);
    }

    @Test
    public void extractUrlFromHtmlAttribute(){
        Content content = getContent();

        content.addAttribute(new HtmltextAttribute("html", "Ja, <a href=\"http://no.wikipedia.org/wiki/Mann\">mannen</a> " +
                "gikk og tok med <a href=\"http://no.wikipedia.org/wiki/Øks\">øksa</a>, " +
                "men Pål Andrestua fikk ikke øye på ham, " +
                "før han tok til bens det beste han kunne. " +
                "Mannen snudde og vendte på plogen og så på den på alle kanter, " +
                "og da han ikke kunne se noe uferdig på den, så gikk han tilbake igjen, " +
                "men på veien plukket han opp lefsesmulene gutten hadde sloppet ned. " +
                "<a href=\"http://no.wikipedia.org/wiki/Kjerring\">Kjerringa</a> sto en stund og så på dette, " +
                "og undres på hva det var mannen sanket opp."), AttributeDataType.CONTENT_DATA);

        MyLinkHandler linkHandler = new MyLinkHandler();
        linkExtractor.extractLinks(content, linkHandler);

        assertThat("Did not contain correct links", linkHandler.getLinks(), hasItems(
                "http://no.wikipedia.org/wiki/Mann",
                "http://no.wikipedia.org/wiki/Øks",
                "http://no.wikipedia.org/wiki/Kjerring"));
    }


    @Test
    public void extractFromUrlAttribute(){
        Content content = getContent();
        content.addAttribute(new UrlAttribute("url", "http://openaksess.org"), AttributeDataType.CONTENT_DATA);

        MyLinkHandler linkHandler = new MyLinkHandler();
        linkExtractor.extractLinks(content, linkHandler);

        assertThat("Did not contain correct links", linkHandler.getLinks(), hasItems(
                "http://openaksess.org"));
    }

    @Test
    public void addVAR_WEBToRelativeUrl(){
        Content content = getContent();
        String path = "/someUrl";
        content.addAttribute(new UrlAttribute("url", path), AttributeDataType.CONTENT_DATA);

        MyLinkHandler linkHandler = new MyLinkHandler();
        linkExtractor.extractLinks(content, linkHandler);

        assertThat("Did not contain correct links", linkHandler.getLinks(),
                hasItems(Aksess.VAR_WEB + path));
    }

    private Content getContent() {
        Content content = new Content();
        content.setId(1);

        when(contentAO.getContent(any(ContentIdentifier.class), anyBoolean())).thenReturn(content);
        return content;
    }

    private static class MyLinkHandler implements LinkHandler {
        private final List<String> links = new ArrayList<>();

        @Override
        public void contentLinkFound(Content content, String link) {
            links.add(link);
        }

        @Override
        public void attributeLinkFound(Content content, String link, String attributeName) {
            links.add(link);
        }

        public List<String> getLinks() {
            return links;
        }
    }
}
