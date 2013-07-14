package no.kantega.publishing.common;

import com.google.gdata.util.common.base.Pair;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ContentPatternTest {
    private static final Pair<String, String> https = new Pair<>("protocol", "https");
    private static final Pair<String, String> http = new Pair<>("protocol", "http");
    private static final Pair<String, String> noProtocol = new Pair<>("protocol", (String) null);
    private static final Pair<String, String> subDomain = new Pair<>("hostname", "sub.domain.no");
    private static final Pair<String, String> wwwSubDomain = new Pair<>("hostname", "www.sub.domain.no");
    private static final Pair<String, String> domain = new Pair<>("hostname", "domain.no");
    private static final Pair<String, String> wwwDomain = new Pair<>("hostname", "www.domain.no");
    private static final Pair<String, String> noPort = new Pair<>("port", (String) null);
    private static final Pair<String, String> port8080 = new Pair<>("port", "8080");
    private static final Pair<String, String> thisId = new Pair<>("thisId", "1");
    private static final Pair<String, String> prettyThisId = new Pair<>("prettythisId", "1234");
    private static final Pair<String, String> contentId = new Pair<>("contentId", "1");
    private static final Pair<String, String> language = new Pair<>("language", "1");
    private static final Pair<String, String> version = new Pair<>("version", "1");
    private static final Pair<String, String> siteId = new Pair<>("siteId", "1");
    private static final Pair<String, String> restContentId = new Pair<>("rest", "?contentId=1");

    @Test
    public void contentApPatternShouldExtractAllTheValues() throws ContentNotFoundException {
        Pattern pattern = Pattern.compile(ContentIdHelperHelper.CONTENT_AP_PATTERN);
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
    public void basePatternShouldExtractProtocolHostAndPort(){
        Pattern pattern = Pattern.compile(ContentIdHelperHelper.BASE_PATTERN);
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
    public void prettyUrlPatternSHouldExtractThisId(){
        Pattern pattern = Pattern.compile(ContentIdHelperHelper.PRETTY_PATTERN);
        Map<String, List<Pair<String, String>>> values = new LinkedHashMap<>();

        values.put("/content/1234/TittelHer", asList(prettyThisId));

        testPatternWithValues(pattern, values);
    }

    @Test
    public void aliasPatternShouldExtractAlias(){
        Pattern pattern = Pattern.compile(ContentIdHelperHelper.ALIAS_PATTERN);
        Map<String, List<Pair<String, String>>> values = new LinkedHashMap<>();

        values.put("/alias/", asList(new Pair<>("alias", "/alias/")));
        values.put("/alias", asList(new Pair<>("alias", "/alias")));
        values.put("/alias/aliaspart2/", asList(new Pair<>("alias", "/alias/aliaspart2/")));
        values.put("/alias/aliaspart2", asList(new Pair<>("alias", "/alias/aliaspart2")));

        values.put("/content/1234/TittelHer", asList(new Pair<>("alias", "/content/1234/TittelHer")));

        testPatternWithValues(pattern, values);
        assertFalse("Pattern should not have matched", pattern.matcher("/images/image.png").matches());
    }

    @Test
    public void fullPatternShouldExtractEverything(){
        Pattern pattern = Pattern.compile(ContentIdHelperHelper.FULL_URL_PATTERN);
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

        values.put("http://domain.no/content/1234/Adas", asList(http, domain, prettyThisId));
        values.put("http://sub.domain.no/content/1234/Adas", asList(http, subDomain, prettyThisId));
        values.put("https://www.sub.domain.no/content/1234/Adas", asList(https, wwwSubDomain, prettyThisId));
        values.put("http://domain.no/content/1234/Adas?contentId=1", asList(http, domain, prettyThisId, restContentId));
        values.put("http://www.domain.no/content/1234/Adas?contentId=1", asList(http, wwwDomain, prettyThisId, new Pair<>("rest", "?contentId=1")));
        values.put("http://sub.domain.no/content/1234/Adas?contentId=1", asList(http, subDomain, prettyThisId, new Pair<>("rest", "?contentId=1")));

        values.put("http://domain.no/alias/", asList(http, domain, new Pair<>("alias", "/alias/")));
        values.put("http://domain.no/alias", asList(http, domain, new Pair<>("alias", "/alias")));
        values.put("http://domain.no/alias/aliaspart2/", asList(http, domain, new Pair<>("alias", "/alias/aliaspart2/")));
        values.put("http://domain.no/alias/aliaspart2", asList(http, domain, new Pair<>("alias", "/alias/aliaspart2")));

        values.put("https://www.sub.domain.no/alias/", asList(https, wwwSubDomain, new Pair<>("alias", "/alias/")));
        values.put("https://www2.sub.domain.no/alias/", asList(https, new Pair<>("hostname", "www2.sub.domain.no"), new Pair<>("alias", "/alias/")));
        values.put("http://www.sub.domain.no/alias/", asList(http, wwwSubDomain, new Pair<>("alias", "/alias/")));
        values.put("http://domain.no:8080/alias", asList(http, domain, port8080, new Pair<>("alias", "/alias")));
        values.put("http://domain.no:8080/alias/aliaspart2/", asList(http, port8080, domain, new Pair<>("alias", "/alias/aliaspart2/")));
        values.put("http://domain.no:8080/alias/aliaspart2", asList(http, domain, port8080, new Pair<>("alias", "/alias/aliaspart2")));


        testPatternWithValues(pattern, values);
    }

    private void testPatternWithValues(Pattern pattern, Map<String, List<Pair<String, String>>> values) {
        for (Map.Entry<String, List<Pair<String, String>>> entry : values.entrySet()) {
            String url = entry.getKey();
            Matcher matcher = pattern.matcher(url);
            assertTrue("Pattern does not match " + url, matcher.matches());
            List<Pair<String, String>> testValues = entry.getValue();
            for (Pair<String, String> testValue : testValues) {
                assertEquals("Could not get " + testValue.first + " from " + url, testValue.second, matcher.group(testValue.first));
            }
        }
    }
}
