package no.kantega.commons.util;

import junit.framework.TestCase;
import no.kantega.publishing.common.util.MultimediaHelper;

import java.util.List;


public class MultimediaHelperTest extends TestCase {
    public void testShouldGet1MultimediaIdsUsingOldUrlFormat(){
        String text = "<p><img src=\"/multimedia.ap?id=1\"></p>";

        List<Integer> multimediaIds = MultimediaHelper.getMultimediaIdsFromText(text);

        assertEquals(1, multimediaIds.size());

        assertEquals(1, multimediaIds.get(0).intValue());
    }

    public void testShouldGet2MultimediaIdsUsingNewUrlFormat(){

        String text = "p>test</p>\n" +
                "<p><img title=\"Marit i Cambridge\" src=\"<@WEB@>/multimedia/242/Cambridge-et-av-de-mange-larestedene.JPG\" alt=\"Utenfor et erverdig universitet i Cambridge\" width=\"174\" height=\"207\"></p>\n" +
                "<p> </p>\n" +
                "<p>test</p>\n" +
                "<p><img title=\"Nadia\" src=\"<@WEB@>/multimedia/301/nadia_intranett.jpg\" alt=\"Nadia\" width=\"174\" height=\"255\"></p>";


        List<Integer> multimediaIds = MultimediaHelper.getMultimediaIdsFromText(text);

        assertEquals(2, multimediaIds.size());

        assertEquals(242, multimediaIds.get(0).intValue());

        assertEquals(301, multimediaIds.get(1).intValue());
    }

    public void testShouldReplaceMultimediaUrlsUsingOldFormatWithCid() {
        String beforeText = "<p><img src=\"<@WEB@>/multimedia.ap?id=1\"></p>";
        String afterText = MultimediaHelper.replaceMultimediaUrlsWithCid(beforeText);

        assertEquals("<p><img src=\"cid:image1\"></p>", afterText);
    }

    public void testShouldReplaceMultimediaUrlsUsingNewFormatWithCid() {
        String beforeText = "<p><img src=\"<@WEB@>/multimedia/1/file.jpg\"></p>";
        String afterText = MultimediaHelper.replaceMultimediaUrlsWithCid(beforeText);

        assertEquals("<p><img src=\"cid:image1\"></p>", afterText);
    }

    public void testShouldNotReplaceOtherImages() {
        String beforeText = "<p><img src=\"http://www.kantega.no/bitmaps/file.jpg\"></p>";
        String afterText = MultimediaHelper.replaceMultimediaUrlsWithCid(beforeText);

        assertEquals(beforeText, afterText);
    }
}