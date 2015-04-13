package no.kantega.publishing.common;


import no.kantega.publishing.common.exception.ContentNotFoundException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ContentPatternTest {
    private static final Pair<String, String> https = Pair.of("protocol", "https");
    private static final Pair<String, String> http = Pair.of("protocol", "http");
    private static final Pair<String, String> noProtocol = Pair.of("protocol", (String) null);
    private static final Pair<String, String> subDomain = Pair.of("hostname", "sub.domain.no");
    private static final Pair<String, String> wwwSubDomain = Pair.of("hostname", "www.sub.domain.no");
    private static final Pair<String, String> domain = Pair.of("hostname", "domain.no");
    private static final Pair<String, String> wwwDomain = Pair.of("hostname", "www.domain.no");
    private static final Pair<String, String> noPort = Pair.of("port", (String) null);
    private static final Pair<String, String> port8080 = Pair.of("port", "8080");
    private static final Pair<String, String> thisId = Pair.of("thisId", "1");
    private static final Pair<String, String> prettyThisId = Pair.of("prettythisId", "1234");
    private static final Pair<String, String> contentId = Pair.of("contentId", "1");
    private static final Pair<String, String> language = Pair.of("language", "1");
    private static final Pair<String, String> version = Pair.of("version", "1");
    private static final Pair<String, String> siteId = Pair.of("siteId", "1");
    private static final Pair<String, String> restContentId = Pair.of("rest", "?contentId=1");

    @Test
    public void contentApPatternShouldExtractAllTheValues() throws ContentNotFoundException {
        Pattern pattern = Pattern.compile(ContentPatterns.CONTENT_AP_PATTERN);
        Map<String, List<Pair<String, String>>> values = new LinkedHashMap<>();

        values.put("/content.ap?thisId=1", asList(thisId));
        values.put("/content.ap?thisId=1&contentId=1", asList(thisId, contentId));
        values.put("/content.ap?contentId=1&thisId=1", asList(thisId, contentId));
        values.put("/content.ap?thisId=1&version=1", asList(thisId, version));
        values.put("/content.ap?contentId=1&version=1", asList(version, contentId));
        values.put("/content.ap?thisId=1&language=1", asList(thisId, language));
        values.put("/content.ap?contentId=1&language=1", asList(language, contentId));
        values.put("/content.ap?thisId=1&contentId=1&version=1", asList(thisId, contentId, version));
        values.put("/content.ap?contentId=1&thisId=1&version=1", asList(thisId, contentId, version));
        values.put("/content.ap?thisId=1&contentId=1&language=1", asList(thisId, contentId, language));
        values.put("/content.ap?contentId=1&thisId=1&language=1", asList(thisId, contentId, language));
        values.put("/content.ap?thisId=1&contentId=1&version=1&language=1", asList(thisId, contentId, version, language));
        values.put("/content.ap?contentId=1&thisId=1&language=1&version=1", asList(thisId, contentId, version, language));
        values.put("/content.ap?thisId=1&version=1&contentId=1&language=1", asList(thisId, contentId, language, version));

        testPatternWithValues(pattern, values);

    }

    @Test
    public void shouldExtractThisIdWhenContextPath(){
        Pattern pattern = Pattern.compile(ContentPatterns.getPatternWithContextPath("/hist-webapp"), Pattern.UNICODE_CHARACTER_CLASS);
        String url = "/hist-webapp/content.ap?thisId=110";
        Matcher matcher = pattern.matcher(url);
        assertTrue("Pattern did not match!", matcher.matches());
        assertEquals("110", matcher.group("thisId"));
    }

    @Test
    public void basePatternShouldExtractProtocolHostAndPort(){
        Pattern pattern = Pattern.compile(ContentPatterns.BASE_PATTERN);
        Map<String, List<Pair<String, String>>> values = new LinkedHashMap<>();

        values.put("https://sub.domain.no", asList(https, subDomain, noPort));
        values.put("http://sub.domain.no", asList(http, subDomain, noPort));
        values.put("sub.domain.no", asList(noProtocol, subDomain, noPort));
        values.put("sub.domain.no:8080", asList(noProtocol, subDomain, port8080));
        values.put("http://sub.domain.no:8080", asList(http, subDomain, port8080));
        values.put("www.sub.domain.no", asList(noProtocol, wwwSubDomain, noPort));
        values.put("www.sub.domain.no:8080", asList(noProtocol, wwwSubDomain, port8080));
        values.put("http://www.sub.domain.no:8080", asList(http, wwwSubDomain, port8080));
        values.put("domain.no", asList(noProtocol, domain, noPort));
        values.put("domain.no:8080", asList(noProtocol, domain, port8080));
        values.put("http://domain.no:8080", asList(http, domain, port8080));
        values.put("www.domain.no", asList(noProtocol, wwwDomain, noPort));
        values.put("www.domain.no:8080", asList(noProtocol, wwwDomain, port8080));
        values.put("http://www.domain.no:8080", asList(http, wwwDomain, port8080));

        testPatternWithValues(pattern, values);
    }

    @Test
    public void prettyUrlPatternShouldExtractThisId(){
        Pattern pattern = Pattern.compile(ContentPatterns.PRETTY_PATTERN);
        Map<String, List<Pair<String, String>>> values = new LinkedHashMap<>();

        values.put("/content/1234/TittelHer", asList(prettyThisId));

        testPatternWithValues(pattern, values);
    }

    @Test
    public void aliasPatternShouldExtractAlias(){
        Pattern pattern = Pattern.compile(ContentPatterns.ALIAS_PATTERN, Pattern.UNICODE_CHARACTER_CLASS);
        Map<String, List<Pair<String, String>>> values = new LinkedHashMap<>();

        values.put("/aliasæ/", asList(Pair.of("alias", "/aliasæ/")));
        values.put("/alias/", asList(Pair.of("alias", "/alias/")));
        values.put("/alias", asList(Pair.of("alias", "/alias")));
        values.put("/alias/aliaspart2/", asList(Pair.of("alias", "/alias/aliaspart2/")));
        values.put("/alias/aliaspart2", asList(Pair.of("alias", "/alias/aliaspart2")));
        values.put("/alias/mypage.dk", asList(Pair.of("alias", "/alias/mypage.dk")));

        values.put("/content/1234/TittelHer", asList(Pair.of("alias", "/content/1234/TittelHer")));

        testPatternWithValues(pattern, values);
        assertFalse("Pattern should not have matched", pattern.matcher("/alias/pattern,png").matches());
    }

    @Test
    public void fullPatternShouldExtractEverything(){
        Pattern pattern = Pattern.compile(ContentPatterns.getPatternWithContextPath(""));
        Map<String, List<Pair<String, String>>> values = new LinkedHashMap<>();

        values.put("http://domain.no/content.ap?thisId=1", asList(http, domain, thisId));
        values.put("http://sub.domain.no/content.ap?thisId=1", asList(http, subDomain, thisId));
        values.put("http://domain.no/content.ap?thisId=1&contentId=1", asList(http, domain, thisId, contentId));
        values.put("http://domain.no:8080/content.ap?thisId=1&contentId=1", asList(http, port8080, domain, thisId, contentId));
        values.put("https://domain.no:8080/content.ap?thisId=1&contentId=1", asList(https, port8080, domain, thisId, contentId));
        values.put("https://sub.domain.no:8080/content.ap?thisId=1&contentId=1", asList(https, port8080, subDomain, thisId, contentId));
        values.put("https://www.sub.domain.no:8080/content.ap?thisId=1&contentId=1", asList(https, port8080, wwwSubDomain, thisId, contentId));
        values.put("https://www.sub.domain.no:8080/content.ap?thisId=1&contentId=1", asList(https, wwwSubDomain, thisId, contentId));
        values.put("http://domain.no/content.ap?contentId=1&thisId=1", asList(http, domain, thisId, contentId));
        values.put("http://domain.no/content.ap?thisId=1&contentId=1&version=1", asList(http, domain, thisId, contentId, version));
        values.put("http://domain.no/content.ap?thisId=1&contentId=1&version=1&language=1", asList(http, domain, thisId, contentId, version, language));
        values.put("/content.ap?thisId=1&contentId=1&version=1&language=1", asList(thisId, contentId, version, language));

        values.put("http://domain.no/content/1234/Adas", asList(http, domain, prettyThisId));
        values.put("/content/1234/Adas", asList(prettyThisId));
        values.put("http://sub.domain.no/content/1234/Adas", asList(http, subDomain, prettyThisId));
        values.put("https://www.sub.domain.no/content/1234/Adas", asList(https, wwwSubDomain, prettyThisId));
        values.put("http://domain.no/content/1234/Adas?contentId=1", asList(http, domain, prettyThisId, restContentId));
        values.put("/content/1234/Adas?contentId=1", asList(prettyThisId, restContentId));
        values.put("http://www.domain.no/content/1234/Adas?contentId=1", asList(http, wwwDomain, prettyThisId, Pair.of("rest", "?contentId=1")));
        values.put("http://sub.domain.no/content/1234/Adas?contentId=1", asList(http, subDomain, prettyThisId, Pair.of("rest", "?contentId=1")));

        values.put("http://domain.no/alias/", asList(http, domain, Pair.of("alias", "/alias/")));
        values.put("/alias/", asList(Pair.of("alias", "/alias/")));
        values.put("http://domain.no/alias", asList(http, domain, Pair.of("alias", "/alias")));
        values.put("/alias", asList(Pair.of("alias", "/alias")));
        values.put("http://domain.no/alias/aliaspart2/", asList(http, domain, Pair.of("alias", "/alias/aliaspart2/")));
        values.put("http://domain.no/alias/aliaspart2", asList(http, domain, Pair.of("alias", "/alias/aliaspart2")));
        values.put("/alias/aliaspart2/", asList(Pair.of("alias", "/alias/aliaspart2/")));
        values.put("/alias/aliaspart2", asList(Pair.of("alias", "/alias/aliaspart2")));

        values.put("https://www.sub.domain.no/alias/", asList(https, wwwSubDomain, Pair.of("alias", "/alias/")));
        values.put("https://www2.sub.domain.no/alias/", asList(https, Pair.of("hostname", "www2.sub.domain.no"), Pair.of("alias", "/alias/")));
        values.put("http://www.sub.domain.no/alias/", asList(http, wwwSubDomain, Pair.of("alias", "/alias/")));
        values.put("http://domain.no:8080/alias", asList(http, domain, port8080, Pair.of("alias", "/alias")));
        values.put("http://domain.no:8080/alias/aliaspart2/", asList(http, port8080, domain, Pair.of("alias", "/alias/aliaspart2/")));
        values.put("http://domain.no:8080/alias/aliaspart2", asList(http, domain, port8080, Pair.of("alias", "/alias/aliaspart2")));


        testPatternWithValues(pattern, values);
    }

    @Test
    public void fullPatternWithContextPathShouldExtractEverything(){
        Pattern pattern = Pattern.compile(ContentPatterns.getPatternWithContextPath("/contextpath"));
        Map<String, List<Pair<String, String>>> values = new LinkedHashMap<>();

        values.put("http://domain.no/contextpath/content.ap?thisId=1", asList(http, domain, thisId));
        values.put("http://sub.domain.no/contextpath/content.ap?thisId=1", asList(http, subDomain, thisId));
        values.put("http://domain.no/contextpath/content.ap?thisId=1&contentId=1", asList(http, domain, thisId, contentId));
        values.put("/contextpath/content.ap?thisId=1&contentId=1", asList(thisId, contentId));
        values.put("http://domain.no:8080/contextpath/content.ap?thisId=1&contentId=1", asList(http, port8080, domain, thisId, contentId));
        values.put("https://domain.no:8080/contextpath/content.ap?thisId=1&contentId=1", asList(https, port8080, domain, thisId, contentId));
        values.put("https://sub.domain.no:8080/contextpath/content.ap?thisId=1&contentId=1", asList(https, port8080, subDomain, thisId, contentId));
        values.put("https://www.sub.domain.no:8080/contextpath/content.ap?thisId=1&contentId=1", asList(https, port8080, wwwSubDomain, thisId, contentId));
        values.put("https://www.sub.domain.no:8080/contextpath/content.ap?thisId=1&contentId=1", asList(https, wwwSubDomain, thisId, contentId));
        values.put("/contextpath/content.ap?thisId=1&contentId=1", asList(thisId, contentId));
        values.put("http://domain.no/contextpath/content.ap?contentId=1&thisId=1", asList(http, domain, thisId, contentId));
        values.put("http://domain.no/contextpath/content.ap?thisId=1&contentId=1&version=1", asList(http, domain, thisId, contentId, version));
        values.put("http://domain.no/contextpath/content.ap?thisId=1&contentId=1&version=1&language=1", asList(http, domain, thisId, contentId, version, language));
        values.put("/contextpath/content.ap?thisId=1&contentId=1&version=1&language=1", asList(thisId, contentId, version, language));

        values.put("http://domain.no/contextpath/content/1234/Adas", asList(http, domain, prettyThisId));
        values.put("/contextpath/content/1234/Adas", asList(prettyThisId));
        values.put("http://sub.domain.no/contextpath/content/1234/Adas", asList(http, subDomain, prettyThisId));
        values.put("https://www.sub.domain.no/contextpath/content/1234/Adas", asList(https, wwwSubDomain, prettyThisId));
        values.put("http://domain.no/contextpath/content/1234/Adas?contentId=1", asList(http, domain, prettyThisId, restContentId));
        values.put("/contextpath/content/1234/Adas?contentId=1", asList(prettyThisId, restContentId));
        values.put("http://www.domain.no/contextpath/content/1234/Adas?contentId=1", asList(http, wwwDomain, prettyThisId, Pair.of("rest", "?contentId=1")));
        values.put("http://sub.domain.no/contextpath/content/1234/Adas?contentId=1", asList(http, subDomain, prettyThisId, Pair.of("rest", "?contentId=1")));

        values.put("http://domain.no/contextpath/alias/", asList(http, domain, Pair.of("alias", "/alias/")));
        values.put("http://domain.no/contextpath/alias", asList(http, domain, Pair.of("alias", "/alias")));
        values.put("/contextpath/alias", asList(Pair.of("alias", "/alias")));
        values.put("http://domain.no/contextpath/alias/aliaspart2/", asList(http, domain, Pair.of("alias", "/alias/aliaspart2/")));
        values.put("/contextpath/alias/aliaspart2/", asList(Pair.of("alias", "/alias/aliaspart2/")));
        values.put("http://domain.no/contextpath/alias/aliaspart2", asList(http, domain, Pair.of("alias", "/alias/aliaspart2")));

        values.put("https://www.sub.domain.no/contextpath/alias/", asList(https, wwwSubDomain, Pair.of("alias", "/alias/")));
        values.put("https://www2.sub.domain.no/contextpath/alias/", asList(https, Pair.of("hostname", "www2.sub.domain.no"), Pair.of("alias", "/alias/")));
        values.put("http://www.sub.domain.no/contextpath/alias/", asList(http, wwwSubDomain, Pair.of("alias", "/alias/")));
        values.put("http://domain.no:8080/contextpath/alias", asList(http, domain, port8080, Pair.of("alias", "/alias")));
        values.put("http://domain.no:8080/contextpath/alias/aliaspart2/", asList(http, port8080, domain, Pair.of("alias", "/alias/aliaspart2/")));
        values.put("http://domain.no:8080/contextpath/alias/aliaspart2", asList(http, domain, port8080, Pair.of("alias", "/alias/aliaspart2")));

        values.put("/", Collections.<Pair<String,String>>emptyList());

        testPatternWithValues(pattern, values);
    }

    private void testPatternWithValues(Pattern pattern, Map<String, List<Pair<String, String>>> values) {
        for (Map.Entry<String, List<Pair<String, String>>> entry : values.entrySet()) {
            String url = entry.getKey();
            Matcher matcher = pattern.matcher(url);
            assertTrue("Pattern does not match " + url, matcher.matches());
            List<Pair<String, String>> testValues = entry.getValue();
            for (Pair<String, String> testValue : testValues) {
                assertEquals("Could not get " + testValue.getLeft() + " from " + url, testValue.getRight(), matcher.group(testValue.getLeft()));
            }
        }
    }
}
