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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContentPatternTest {
    public static final String contentApPattern = "/content.ap" +
            "((&|\\?)(" +
            "(thisId=(?<thisId>\\d+))" +
            "|(contentId=(?<contentId>\\d+))" +
            "|(version=(?<version>\\d+))" +
            "|(language=(?<language>\\d+))" +
            "))+";

    @Test
    public void contentApPatternShouldExtractAllTheValues() throws ContentNotFoundException {
        Pattern pattern = Pattern.compile(contentApPattern);
        Map<String, List<Pair<String, String>>> values = new LinkedHashMap<>();
        values.put("/content.ap?thisId=1", asList(new Pair<>("thisId", "1")));
        values.put("/content.ap?thisId=1&contentId=1", asList(new Pair<>("thisId", "1"), new Pair<>("contentId", "1")));
        values.put("/content.ap?contentId=1&thisId=1", asList(new Pair<>("thisId", "1"), new Pair<>("contentId", "1")));
        values.put("/content.ap?thisId=1&version=1", asList(new Pair<>("thisId", "1"), new Pair<>("version", "1")));
        values.put("/content.ap?contentId=1&version=1", asList(new Pair<>("version", "1"), new Pair<>("contentId", "1")));
        values.put("/content.ap?thisId=1&language=1", asList(new Pair<>("thisId", "1"), new Pair<>("language", "1")));
        values.put("/content.ap?contentId=1&language=1", asList(new Pair<>("language", "1"), new Pair<>("contentId", "1")));
        values.put("/content.ap?thisId=1&contentId=1&version=1", asList(new Pair<>("thisId", "1"), new Pair<>("contentId", "1"), new Pair<>("version", "1")));
        values.put("/content.ap?contentId=1&thisId=1&version=1", asList(new Pair<>("thisId", "1"), new Pair<>("contentId", "1"), new Pair<>("version", "1")));
        values.put("/content.ap?thisId=1&contentId=1&language=1", asList(new Pair<>("thisId", "1"), new Pair<>("contentId", "1"), new Pair<>("language", "1")));
        values.put("/content.ap?contentId=1&thisId=1&language=1", asList(new Pair<>("thisId", "1"), new Pair<>("contentId", "1"), new Pair<>("language", "1")));
        values.put("/content.ap?thisId=1&contentId=1&version=1&language=1", asList(new Pair<>("thisId", "1"), new Pair<>("contentId", "1"), new Pair<>("version", "1"), new Pair<>("language", "1")));
        values.put("/content.ap?contentId=1&thisId=1&language=1&version=1", asList(new Pair<>("thisId", "1"), new Pair<>("contentId", "1"), new Pair<>("version", "1"), new Pair<>("language", "1")));
        values.put("/content.ap?thisId=1&version=1&contentId=1&language=1", asList(new Pair<>("thisId", "1"), new Pair<>("contentId", "1"), new Pair<>("language", "1"), new Pair<>("version", "1")));

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
