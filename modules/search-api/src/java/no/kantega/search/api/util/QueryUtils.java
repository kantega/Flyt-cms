package no.kantega.search.api.util;

public class QueryUtils {
    /**
     * Escape chars that have a meaning to Lucene.
     * @param query to escape
     * @return query with chars that have meaning in Lucene escaped
     * Copied from org.apache.solr.client.solrj.util.ClientUtils
     */
    public static String escapeQueryChars(String query) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < query.length(); ++i) {
            char c = query.charAt(i);
            if(c == 92 || c == 43 || c == 45 || c == 33 || c == 40 || c == 41 || c == 58 || c == 94 || c == 91 || c == 93 || c == 34 || c == 123 || c == 125 || c == 126 || c == 42 || c == 63 || c == 124 || c == 38 || c == 59 || c == 47 || Character.isWhitespace(c)) {
                sb.append('\\');
            }

            sb.append(c);
        }

        return sb.toString();
    }
}
