package no.kantega.publishing.common;

public class ContentPatterns {
    /**
     * Regexp matching protocol, hostname and port of an url with the following named groups:
     * - <code>protocol</code>, http or https.
     * - <code>hostname</code>, the domain name.
     * - <code>port</code>, the defined port, e.g. 8080.
     */
    public static final String BASE_PATTERN = "(((?<protocol>https?)://)?(?<hostname>[\\w\\.\\-]+)(:(?<port>\\d+))?)?";

    /**
     * Regexp matching «pretty urls» fragment with the following named groups:
     * - <code>prettythisId</code>, the id in urls like /content/123/title.
     */
    public static final String PRETTY_PATTERN = "(/content/(?<prettythisId>\\d+)/([a-zA-Z_0-9-+\\.:]+))";

    /**
     * Regexp matching url fragment with content.ap with the following named groups:
     * - <code>thisId</code>, the value of the parameter thisId=123, if present.
     * - <code>contentId</code>, the value of the parameter content=321, if present.
     * - <code>version</code>, the value of the parameter version=1, if present.
     * - <code>language</code>, the value of the parameter language=1, if present.
     * The order of the parameters may be arbitrary.
     */
    public static final String CONTENT_AP_PATTERN = "(/content.ap" +
            "((&|\\?)(" + // The following regex matches ?contentId=A&thisId=B&version=C&language=D
               "(thisId=(?<thisId>\\d+))" + // where the parameters may be in any order.
               "|(contentId=(?<contentId>\\d+))" +
               "|(version=(?<version>\\d+))" +
               "|(language=(?<language>\\d+))" +
            "))+)";

    /**
     * Regexp matching url fragment like /alias, /alias/, /alias/alias2, and so on, with the following named group:
     * - <code>alias</code>, the whole alias.
     */
    public static final String ALIAS_PATTERN = "(?<alias>[\\w\\-\\+=/\\&]*)";

    /**
     * Regexp combining <code>PRETTY_PATTERN</code>, <code>CONTENT_AP_PATTERN</code>,
     * and <code>ALIAS_PATTERN</code> and introducing the following named groups:
     * - <code>content</code>, the rest part containing any of the content patterns.
     * - <code>rest</code>, the rest of the url following any of the other patterns.
     * This is supposed to match all urls that lead to <code>Content</code> objects.
     */
    public static final String CONTENT_PATTERNS = "(?<content>" +
            PRETTY_PATTERN + "|" +
            CONTENT_AP_PATTERN + "|" +
            ALIAS_PATTERN +
            ")(?<rest>.*)?";

    /**
     * Regexp combining <code>BASE_PATTERN</code>, <code>CONTENT_PATTERNS</code>, with
     * the given <code>contextPath</code> added.
     * This is supposed to match all urls that lead to <code>Content</code> objects.
     */
    public static String getPatternWithContextPath(String contextPath){
        return contextPath.equals("/") ?
                BASE_PATTERN + CONTENT_PATTERNS :
                BASE_PATTERN + contextPath + CONTENT_PATTERNS;

    }
}
